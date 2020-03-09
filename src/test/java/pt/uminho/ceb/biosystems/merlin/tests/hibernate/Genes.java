package pt.uminho.ceb.biosystems.merlin.tests.hibernate;

import java.util.List;
import java.util.Map;

import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.core.containers.model.GeneContainer;
import pt.uminho.ceb.biosystems.merlin.dataAccess.InitDataAccess;
import pt.uminho.ceb.biosystems.merlin.services.annotation.AnnotationEnzymesServices;
import pt.uminho.ceb.biosystems.merlin.services.model.ModelSequenceServices;
import pt.uminho.ceb.biosystems.merlin.utilities.DatabaseProgressStatus;
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


	public void checkSequencesByType() {
		try {
			
			System.out.println(InitDataAccess.getInstance().getDatabaseService("aaTestdb").checkGenomeSequencesByType(SequenceType.PROTEIN));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
	public void checkInsertHomologyH2() {
		try {
			
			String databaseName ="t_blast_bastos" ; 
			String locusTag = "wp_coboy";
			String uniProtEC = "1.2.3.6";
			Boolean uniprot_star_boolean = true;
			int homologySetupID = 1;
			String query = "";
			Integer idGene = 2;
			SequenceType type = SequenceType.PROTEIN;
			String sequence = "MAHSHATATATATTE";
			Integer sequenceLength = 10;
			int seqId = 5;
			String chromosome = "XY";
			String organelle = "cytoplasm";
			
//			ModelSequenceServices.insertNewSequence(databaseName, idGene, type, sequence, sequenceLength);
			
//			System.out.println(AnnotationEnzymesServices.insertGeneHomologyEntry(databaseName, locusTag, uniProtEC, uniprot_star_boolean, homologySetupID, query, DatabaseProgressStatus.NO_SIMILARITY, 1, "XYZ", "exo"));
			
			AnnotationEnzymesServices.updateGeneHomologyEntry(databaseName, locusTag, uniProtEC, uniprot_star_boolean, homologySetupID, query, DatabaseProgressStatus.PROCESSING, seqId, chromosome, organelle, 1);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
	public void updateGeneStatus() {
		try {
			
			String databaseName ="t_blast_bastos" ; 
			String locusTag = "wp_coboy";
			String uniProtEC = "1.2.3.6";
			Boolean uniprot_star_boolean = true;
			int homologySetupID = 1;
			String query = "";
			Integer idGene = 2;
			SequenceType type = SequenceType.PROTEIN;
			String sequence = "MAHSHATATATATTE";
			Integer sequenceLength = 10;
			int seqId = 5;
			String chromosome = "XY";
			String organelle = "cytoplasm";
			
			AnnotationEnzymesServices.updateGeneHomologyStatus(databaseName, locusTag, DatabaseProgressStatus.PROCESSED);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

//	@Test
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
	
	@Test
	public void testgetGenomeFromDatabase() {
		try {
			
			
			String databaseName = "testBlast2";
			InitDataAccess.getInstance().getDatabaseService(databaseName).getReactionDataForStats(false);
//			InitDataAccess.getInstance().getDatabaseService(databaseName).checkBiochemicalOrTransportReactions(false)
			InitDataAccess.getInstance().dropConnection(databaseName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

