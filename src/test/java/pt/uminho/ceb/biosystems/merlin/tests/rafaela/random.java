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
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.H2DatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.MySQLDatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.Enumerators.DatabaseType;
import pt.uminho.ceb.biosystems.merlin.tests.excel.ReadExcelFile;
import pt.uminho.ceb.biosystems.merlin.utilities.io.FileUtils;

public class random {

	@Test
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
