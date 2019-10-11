package pt.uminho.ceb.biosystems.merlin.tests.rafaela;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.Connection;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.DatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.Enumerators.DatabaseType;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.H2DatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.MySQLDatabaseAccess;

public class random {

//	@Test
	public void revertTablesNames() throws SQLException {
		
		String host = "palsson.di.uminho.pt";
		String databaseName = "hpluvialis_restored";
		String password = "dev$2018merlin";
		String port = "2401";
		String username = "merlindev";
		DatabaseType type = DatabaseType.MYSQL;
		
		DatabaseAccess dbAccess = generateDBAccess(host, databaseName, password, port, username, type);
		
		Connection connection = new Connection(dbAccess);
		
		Set<String> tables = getTablesNames(connection);
		
		convertTablesNames(connection, tables);
		
		connection.closeConnection();
		
	}
	
	@Test
	public void romeu() throws SQLException {
		
		String host = "palsson.di.uminho.pt";
		String databaseName = "calbicans";
		String password = "dev$2018merlin";
		String port = "2401";
		String username = "merlindev";
		DatabaseType type = DatabaseType.MYSQL;
		
		DatabaseAccess dbAccess = generateDBAccess(host, databaseName, password, port, username, type);
		
		Connection connection = new Connection(dbAccess);
		
		Statement statemnt = connection.createStatement();
		
		Set<Integer> ids = new HashSet<>();
		
		ResultSet rs = statemnt.executeQuery("SELECT stoichiometry.idstoichiometry FROM reaction INNER JOIN stoichiometry ON idreaction = stoichiometry.reaction_idreaction WHERE SOURCE LIKE 'DRAIN%' AND stoichiometry.compartment_idcompartment = 25;");
		
		while(rs.next())
			ids.add(rs.getInt(1));
		
		for(int id : ids)
			statemnt.execute("UPDATE stoichiometry SET stoichiometry.compartment_idcompartment = 18 WHERE stoichiometry.idstoichiometry = " + id +";");
		
		System.out.println(ids.size());
		
		connection.closeConnection();
		statemnt.close();
		
	}
	
	public static DatabaseAccess generateDBAccess(String host, String databaseName, String password, String port,
			String username, DatabaseType type) {
		
		if(type.equals(DatabaseType.MYSQL))
			return new MySQLDatabaseAccess(username, password, host, port, databaseName);
		else
			return new H2DatabaseAccess(username, password, databaseName, host);
	}
	
	public Set<String> getTablesNames(Connection connection) {
		
		Set<String> tables = new HashSet<>();
		
		try {
			Statement statement = connection.createStatement();
			
			ResultSet rs = statement.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = '" + connection.getDatabaseName() + "';");
			
			while(rs.next()) {
				tables.add(rs.getString(1));
			}
			
			rs.close();
			statement.close();
		
		
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tables;
	}
	
	public void convertTablesNames(Connection connection, Set<String> tables) {

		try {
			
			Statement statement = connection.createStatement();
			
			for(String table : tables) {
				
				String newTable = table;
				
				if(newTable.startsWith("enzymes_annotation_"))
					newTable = newTable.replace("enzymes_annotation_", "");
				else if(newTable.startsWith("compartments_annotation_"))
					newTable = newTable.replace("compartments_annotation_", "");
				else if(newTable.startsWith("model_"))
					newTable = newTable.replace("model_", "");
				else if(newTable.startsWith("interpro_interpro_"))
					newTable = newTable.replace("interpro_interpro_", "interpro_");
				
				if(!newTable.equals(table))
					statement.execute("RENAME TABLE " + table + " TO " + newTable + ";");
			}
			
			statement.close();
			
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
