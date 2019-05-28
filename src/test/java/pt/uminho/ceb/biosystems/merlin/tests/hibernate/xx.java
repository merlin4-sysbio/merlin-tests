package pt.uminho.ceb.biosystems.merlin.tests.hibernate;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.core.datatypes.view.ModelDesnormalizedReactionPathwayAndCompartment;
import pt.uminho.ceb.biosystems.merlin.dataAccess.InitDataAccess;
import pt.uminho.ceb.biosystems.merlin.entities.model.ModelSequence;

public class xx {

//	@Test
	public void test() {
		try {
//			int res = InitDataAccess.getInstance().getDatabaseService().getStoichiometryID(66, "-1208", 1, "-1");
			List<ModelDesnormalizedReactionPathwayAndCompartment> res = InitDataAccess.getInstance().getDatabaseService("aaaHibernateTest").getAllReactionsView(true, true);
			
			System.out.println(res);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void test2() {
		try {
			boolean res = InitDataAccess.getInstance().getDatabaseService("aaaHibernateTest").checkReactionsAreOriginal();
			
			System.out.println(res);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test3() {
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

}
