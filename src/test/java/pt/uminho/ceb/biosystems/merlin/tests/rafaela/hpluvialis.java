package pt.uminho.ceb.biosystems.merlin.tests.rafaela;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import pt.uminho.ceb.biosystems.merlin.aibench.utilities.LoadFromConf;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.Connection;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.DatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.MySQLDatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.tests.excel.ReadExcelFile;
import pt.uminho.ceb.biosystems.merlin.utilities.io.FileUtils;

public class hpluvialis {

	@Test
	public void test() throws SQLException {

		try {
			String path = "C:\\Users\\BioSystems\\Downloads\\NIHMS731891-supplement-Supp_TableS5.xlsx";

			List<String[]> excel = ReadExcelFile.getData(path);

			Map<String, String> ids = findKeggIDs(excel);

			Map<String, String> paths = findPathways(excel);

			Connection conn = new Connection(generateDBAccess());

			Statement stmt = conn.createStatement();

			executePathwaysAssociation(stmt, paths, ids);

			stmt.close();
			conn.closeConnection();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static Map<String, String> findKeggIDs(List<String[]> excel){

		Map<String, String> reactions = new HashMap<>();

		for(String[] line : excel) {
			if(line.length > 10 && line[11] != null && !line[11].trim().isEmpty()) {
				String bigg = line[0];
				String kegg = line[11].split(";")[0].split("\\+")[0].trim();

				if(reactions.containsKey(bigg))
					System.out.println("WARNING -> " + bigg + " alrady exists!");

				reactions.put(bigg, kegg);
			}
		}

		return reactions;
	}

	private static Map<String, String> findPathways(List<String[]> excel){

		Map<String, String> pathways = new HashMap<>();

		for(String[] line : excel) {
			if(line.length > 10) {
				String biggID = line[0];
				String path = line[4];

				if(pathways.containsKey(biggID))
					System.out.println("WARNING -> " + biggID + " alrady exists!");

				//				System.out.println(biggID + "\t" + path);

				pathways.put(biggID, path);
			}
		}

		return pathways;
	}

	private static DatabaseAccess generateDBAccess() {

		Map<String, String> credentials = LoadFromConf.loadDatabaseCredentials(FileUtils.getConfFolderPath());

		String username = null, password = null, host = null, port = null, database = null;

		username = credentials.get("username");
		password = credentials.get("password");
		host = credentials.get("host");
		port = credentials.get("port");
		database = "hpluvialis_restored";

		return new MySQLDatabaseAccess(username, password, host, port, database);
	}

	private static List<String> getCompartments(Statement stmt) throws SQLException{

		List<String> comp = new ArrayList<String>();

		ResultSet rs = stmt.executeQuery("SELECT * FROM model_compartment;");

		while(rs.next())
			comp.add(rs.getString(3).trim());

		return comp;

	}

	private static void executePathwaysAssociation(Statement stmt, Map<String, String> pathwaysBigg, Map<String, String> keggIDs) throws SQLException{

		Map<String, Integer> pathwaysKegg = getPathways(stmt);
		Map<String, Integer> reactionsTable = getReactions(stmt);
		List<String> compartments = getCompartments(stmt);

		for(String biggID : pathwaysBigg.keySet()) {

			String[] biggPaths = pathwaysBigg.get(biggID).split(";");

			Set<Integer> paths = new HashSet();

			for(String bp : biggPaths) {

				Integer pathID = null;

				if(pathwaysKegg.containsKey(bp.trim())) {
					pathID = pathwaysKegg.get(bp.trim());
				}
				else if(bp.equals("TCA cycle"))
					pathID = pathwaysKegg.get("Citrate cycle (TCA cycle)");
				else if(bp.equals("Exchange"))
					pathID = pathwaysKegg.get("Drains pathway");
				else if(bp.equals("Exchange") || biggID.startsWith("EX_"))
					pathID = pathwaysKegg.get("Drains pathway");
				else if(bp.contains("Transport"))
					pathID = pathwaysKegg.get("Membrane transport");
				else if(bp.contains("Biomass"))
					pathID = pathwaysKegg.get("Biomass Pathway");
				else if(bp.equals("ATP maintenance"))
					pathID = pathwaysKegg.get("Oxidative phosphorylation");

				if(pathID != null)
					paths.add(pathID);
			}

			Integer reactionID = null;

			if(reactionsTable.containsKey(biggID.trim()))
				reactionID = reactionsTable.get(biggID.trim());
			else {

				for(String c : compartments) {

					if(biggID.equals("ALCDH(nadp)i"))
						reactionID = reactionsTable.get("ALCDH_nadp_c");
					else if(biggID.equals("CYOR(q8)m"))
						reactionID = reactionsTable.get("CYOR_m");
					else if(biggID.equals("SUCDH(q8)m"))
						reactionID = reactionsTable.get("SUCDHm_m");
					else if(biggID.equals("EX_rib-D(e)"))
						reactionID = reactionsTable.get("EX_rib_e");
					else if(biggID.equals("EX_lac-D(e)"))
						reactionID = reactionsTable.get("EX_lac_e");
					else if(biggID.equals("DM_dad-5(c)"))
						reactionID = reactionsTable.get("DM_dad_c");

					String aux = biggID.replace("-", "_").replace("(", "_").replace(")", "_");

					aux += "_".concat(c);

					aux = aux.replace("__", "_");

					aux = aux.replaceAll("_$", "");

					if(reactionsTable.containsKey(aux.trim()))
						reactionID = reactionsTable.get(aux.trim());

					if(reactionID == null) {

						aux = biggID.replace("-", "_").replace("(" + c + ")", "_" + c);

						aux = aux.replace("(", "_").replace(")", "_").replace("__", "_");

						aux = aux.replaceAll("_$", "");

						if(reactionsTable.containsKey(aux.trim()))
							reactionID = reactionsTable.get(aux.trim());
						else if(reactionsTable.containsKey(aux.replace("_L", "")))
							reactionID = reactionsTable.get(aux.replace("_L", ""));
					}
				}
			}

			try {
				for(Integer pathID : paths) {
					if(reactionID != null && pathID != null) {
						stmt.execute("INSERT INTO model_pathway_has_reaction (reaction_idreaction, pathway_idpathway) VALUES ( " + reactionID + ", " + pathID + ");");
//					System.out.println("INSERT INTO model_pathway_has_reaction VALUES ( " + reactionID + ", " + pathID + ");");
					}
				}
				if(keggIDs.containsKey(biggID) && reactionID != null) {
//				System.out.println(keggIDs.get(biggID));
					stmt.execute("UPDATE model_reaction SET name = '" + keggIDs.get(biggID) + "' WHERE idreaction = " + reactionID + ";");
				}
			} 
			catch (MySQLIntegrityConstraintViolationException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				System.out.println("Duplicate entry");
			}


		}
		
		System.out.println("done");
	}

	private static Map<String, Integer> getPathways(Statement stmt) throws SQLException{

		Map<String, Integer> paths = new HashMap<>();

		ResultSet rs = stmt.executeQuery("SELECT * FROM model_pathway;");

		while(rs.next())
			paths.put(rs.getString(3).trim(), rs.getInt(1));

		return paths;

	}

	private static Map<String, Integer> getReactions(Statement stmt) throws SQLException{

		Map<String, Integer> paths = new HashMap<>();

		ResultSet rs = stmt.executeQuery("SELECT * FROM model_reaction;");

		while(rs.next())
			paths.put(rs.getString(2).trim(), rs.getInt(1));

		return paths;

	}

}
