package pt.uminho.ceb.biosystems.merlin.tests.bioapis;

import java.util.List;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.kegg.KeggAPI;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.kegg.KeggOperation;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.kegg.KeggRestful;

public class KeggTests {

	@Test
	public void test() throws Exception {
		
		System.out.println(KeggAPI.getProduct("PP_0001"));
	}

	//@Test
	public void main() throws Exception {
		
		System.out.println(KeggRestful.fetch(KeggOperation.get, "cpd:C15025"));
		KeggRestful.fetch(KeggOperation.get, "cpd:C15025");
	}
	
//	@Test
	public void koTest() throws Exception {
		
		System.out.println(KeggRestful.findOrthologsByECnumber("2.1.1.5"));
	}
	
	@Test
	public void findEntryByTaxonomyID() throws Exception  {

		List<String> keggIDs = KeggAPI.findKeggTaxonomyID("243276");

		System.out.println(keggIDs);

	}
	
}
