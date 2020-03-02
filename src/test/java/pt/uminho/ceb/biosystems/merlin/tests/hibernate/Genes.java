package pt.uminho.ceb.biosystems.merlin.tests.hibernate;

import java.util.List;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.core.containers.model.GeneContainer;
import pt.uminho.ceb.biosystems.merlin.dataAccess.InitDataAccess;
import pt.uminho.ceb.biosystems.merlin.utilities.Enumerators.SequenceType;

public class Genes {
	
//	@Test
	public void countGenesEncodingProteins() {
		try {
			
			long res = InitDataAccess.getInstance().getDatabaseService("aaaDBTest24").countGenesEncodingProteins();
			
			int e = (int) res;
			
			System.out.println(e + 1);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
	public void countGenesWithName() {
		try {
			
			long res = InitDataAccess.getInstance().getDatabaseService("aaaDBTest24").countGenesWithName();
			
			System.out.println(res);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
	public void getSequenceInfo() {
		try {
			
			List<GeneContainer> res = InitDataAccess.getInstance().getDatabaseService("aaaaLatestDatabase").getSequenceByGeneId(4);
			
			for(GeneContainer gene : res) {
				
				System.out.println(gene.getIdGene());
				System.out.println(gene.getSequenceType());
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//@Test
	public void checkSequencesByType() {
		try {
			
			System.out.println(InitDataAccess.getInstance().getDatabaseService("aaTestdb").checkGenomeSequencesByType(SequenceType.PROTEIN));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void insertModelGeneHasCompartment() {
		try {
			
			
			String databaseName = "testDlimaInsertsUpdates";
			//InitDataAccess.getInstance().getDatabaseService(databaseName).insertModelGeneHasCompartment(true, "score1", 1, 1);
			
			InitDataAccess.getInstance().getDatabaseService(databaseName).insertModelCompound(null,null,null,null,null,null,null,null,null,null);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
