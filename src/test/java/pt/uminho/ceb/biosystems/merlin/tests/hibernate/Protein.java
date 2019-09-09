package pt.uminho.ceb.biosystems.merlin.tests.hibernate;

import java.io.IOException;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.dataAccess.InitDataAccess;
import pt.uminho.ceb.biosystems.merlin.services.model.ModelEnzymesServices;

public class Protein {

//	@Test
	public void insertProtein() throws IOException, Exception {
		System.out.println(InitDataAccess.getInstance().getDatabaseService("aaTestdb").insertNewProteinEntry("-", "thi's"));
		
	}
	
//	@Test
	public void insertEnzyme() throws IOException, Exception {
		InitDataAccess.getInstance().getDatabaseService("aaTestdb").insertEnzymes(1, "1.1.1.1", false);
		
	}

	@Test
	public void insertEnzyme2() throws IOException, Exception {
		ModelEnzymesServices.insertEnzyme("aaTestdb", 1, "1.2.1", true, "KEGG");
//		InitDataAccess.getInstance().getDatabaseService("aaTestdb").insertEnzymes(1, "1.1.1.1", false);
		
	}
}
