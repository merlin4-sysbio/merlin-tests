package pt.uminho.ceb.biosystems.merlin.tests.hibernate;

import java.io.IOException;

import pt.uminho.ceb.biosystems.merlin.dataAccess.InitDataAccess;

public class Protein {

//	@Test
	public void insertProtein() throws IOException, Exception {
		System.out.println(InitDataAccess.getInstance().getDatabaseService("aaTestdb").insertNewProteinEntry("-", "thi's"));
		
	}
	
//	@Test
	public void insertEnzyme() throws IOException, Exception {
		InitDataAccess.getInstance().getDatabaseService("aaTestdb").insertEnzymes(1, "1.1.1.1", false);
		
	}

}
