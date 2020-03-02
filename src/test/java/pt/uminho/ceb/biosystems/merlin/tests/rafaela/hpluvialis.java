package pt.uminho.ceb.biosystems.merlin.tests.rafaela;

import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.aibench.utilities.LoadFromConf;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.Connection;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.DatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.MySQLDatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.Enumerators.DatabaseType;
import pt.uminho.ceb.biosystems.merlin.tests.excel.ReadExcelFile;
import pt.uminho.ceb.biosystems.merlin.utilities.io.FileUtils;

public class hpluvialis {

	@Test
	public void test() throws SQLException {
		
		String path = "C:\\Users\\BioSystems\\Downloads\\ids.xlsx";

		List<String[]> excel = ReadExcelFile.getData(path);
		
		Map<String, String> ids = findKeggIDs(excel);
		Map<String, String> paths = findPathways(excel);
		
		Connection conn = new Connection(generateDBAccess());
		
		Statement stmt = conn.createStatement();
		
//		List<String> compartments = getCompartments(stmt);
		
		//executeIDSUpdate(stmt, ids, compartments);
		
		Map<String, Integer> pathways = getPathways(stmt);
		
		Set<String> has = new HashSet<>();
		Set<String> hasNot = new HashSet<>();
		
		for(String biggID : paths.keySet()) {
				
			String[] pathsNames = paths.get(biggID).split(";");
			
			for(String pathName : pathsNames) {
				
				pathName = pathName.trim();
				
				if(pathways.containsKey(pathName)) {
					has.add(pathName);
//					System.out.println(pathName);
				}
				else {
					hasNot.add(pathName);
//					System.out.println(pathName);
				}
			}
				
			
//			if(pathways.get(pathwayID).contains(";"))
//				System.out.println(pathwayID + "\t" + pathways.get(pathwayID));
			
		}
		
		System.out.println(has.size());
		System.out.println(hasNot.size());
		
		for(String p : hasNot)
			System.out.println(p);
			
		System.out.println("done");
		
		stmt.close();
		conn.closeConnection();
	}


	public static Map<String, String> findKeggIDs(List<String[]> excel){

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
	
	public static Map<String, String> findPathways(List<String[]> excel){

		Map<String, String> pathways = new HashMap<>();

		for(String[] line : excel) {
			if(line.length > 10 && line[11] != null && !line[11].trim().isEmpty()) {
				String biggID = line[0];
				String path = line[4];
				
				if(pathways.containsKey(biggID))
					System.out.println("WARNING -> " + biggID + " alrady exists!");
				
				pathways.put(biggID, path);
			}
		}

		return pathways;
	}
	
	public static DatabaseAccess generateDBAccess() {
		
		Map<String, String> credentials = LoadFromConf.loadDatabaseCredentials(FileUtils.getConfFolderPath());
		
		String username = null, password = null, host = null, port = null, database = null;

		username = credentials.get("username");
		password = credentials.get("password");
		host = credentials.get("host");
		port = credentials.get("port");
		database = "hpluvialis";
		
		return new MySQLDatabaseAccess(username, password, host, port, database);
	}
	
	public static List<String> getCompartments(Statement stmt) throws SQLException{
		
		List<String> comp = new ArrayList<String>();
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM model_compartment;");
		
		while(rs.next())
			comp.add(rs.getString(3).trim());
		
		return comp;
		
	}
	
	public static void executeIDSUpdate(Statement stmt, Map<String, String> ids, List<String> compartments) throws SQLException{
		
		for(String biggID : ids.keySet()) {
			for(String c : compartments) {
				c = "_".concat(c);
				stmt.execute("UPDATE model_reaction SET name = '" + ids.get(biggID).concat(c) + "' WHERE name = '" + biggID.concat(c) + "';");
			}
		}
		
	}
	
	public static Map<String, Integer> getPathways(Statement stmt) throws SQLException{
		
		Map<String, Integer> paths = new HashMap<>();
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM model_pathway;");
		
		while(rs.next())
			paths.put(rs.getString(3).trim(), rs.getInt(1));
		
		return paths;
		
	}
	
}
