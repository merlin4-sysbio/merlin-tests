package pt.uminho.ceb.biosystems.merlin.tests.hibernate;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.core.datatypes.view.ModelDesnormalizedReactionPathwayAndCompartment;
import pt.uminho.ceb.biosystems.merlin.dataAccess.InitDataAccess;
import pt.uminho.ceb.biosystems.merlin.entities.model.ModelSequence;

public class Reactions {
	
//	@Test
//	public void createDatabase() throws IOException {
//		InitDataAccess.getInstance().getDatabaseService("s2m2");
//	}

	@Test
	public void test() {
		try {
//			int res = InitDataAccess.getInstance().getDatabaseService().getStoichiometryID(66, "-1208", 1, "-1");
			List<ModelDesnormalizedReactionPathwayAndCompartment> res = InitDataAccess.getInstance().getDatabaseService("aaTestdb").getAllReactionsView(false, false);
			
//			System.out.println(res);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void isCompartimentalizedModel() {
		try {
			boolean res = InitDataAccess.getInstance().getDatabaseService("aaaHibernateTest").isCompartimentalizedModel();
			
			System.out.println(res);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
	public void gelAllEntriesFromModelSequence() {
		try {
			List<ModelSequence> modelSequence = InitDataAccess.getInstance().getDatabaseService("aaaHibernateTest").getAllModelSequence();
			
			for(ModelSequence s : modelSequence) {
				
				System.out.println(s.getSequence());
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
	public void countEntriesInTableModelGene() {
		try {
			Long n = InitDataAccess.getInstance().getDatabaseService("newDatabaseFormat").countEntriesInGene();
			
			System.out.println(n);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
//	public void insertReactio() {
//		try {
//			
//			Integer modelSequence = InitDataAccess.getInstance().getDatabaseService("aaaDBTest24").insertNewReaction(false, 0, 2, "1 and a 3",
//					"a + b = c", true, false, true, "a", "aa", 1);
//			
//			System.out.println(modelSequence);
////			
////			InitDataAccess.getInstance().getDatabaseService("newDatabaseFormat").insertNewReaction(false, "outside", -9.0, 1.0, "I and G or U", null, null);
//			
//			System.out.println("done");
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	@Test
	public void findAvailableDatabases() {
		try {
			
//			List<ModelSequence> modelSequence = InitDataAccess.getInstance().getDatabaseService("aaaNewTest4").getAllModelSequence();
			
//			System.out.println(modelSequence.size());
			
			System.out.println(InitDataAccess.getInstance().getDatabasesAvailable());
			
//			System.out.println("done");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
