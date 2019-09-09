package pt.uminho.ceb.biosystems.merlin.tests.hibernate;

import java.util.List;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.dataAccess.InitDataAccess;

public class Aliases {

//	@Test
	public void checkAliases() {
		try {
			
			boolean exists = InitDataAccess.getInstance().getDatabaseService("keggData").checkEntityFromAliases("c", 6, "Glycan");
			
			System.out.println(exists);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void InsertAlias() {
		try {
			
			InitDataAccess.getInstance().getDatabaseService("keggData").insertNewModelAliasEntry("de", 4, "dsad");
			
			System.out.println("keggData3");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
