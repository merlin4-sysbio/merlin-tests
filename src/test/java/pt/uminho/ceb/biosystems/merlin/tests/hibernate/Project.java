package pt.uminho.ceb.biosystems.merlin.tests.hibernate;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.dataAccess.InitDataAccess;

public class Project {

	
//	@Test
	public void getProjectID() {
		try {
			
			
			System.out.println(InitDataAccess.getInstance().getDatabaseService("aaaDBTest24").getProjectID(Long.valueOf(35)));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void updateProject() {
		try {
			
			InitDataAccess.getInstance().getDatabaseService("aaaDBTest24").updateOrganismID(Long.valueOf(35));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
