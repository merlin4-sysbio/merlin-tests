package pt.uminho.ceb.biosystems.merlin.tests.hibernate;

import java.util.List;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.dataAccess.InitDataAccess;

public class Pathways {

	@Test
	public void countGenesEncodingProteins() {
		try {
			
			List<String[]> res = InitDataAccess.getInstance().getDatabaseService("new3").getUpdatedPathways(false, true);
			
			System.out.println(res.size());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
