package pt.uminho.ceb.biosystems.merlin.tests.core;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.ceb.biosystems.merlin.BiomassMetabolite;
import pt.uminho.ceb.biosystems.merlin.Enumerators.ReturnType;
import pt.uminho.ceb.biosystems.merlin.EstimateBiomassContents;
import pt.uminho.ceb.biosystems.merlin.Utilities;
import pt.uminho.ceb.biosystems.merlin.core.utilities.DatabaseLoaders;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.Connection;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.DatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.H2DatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.utilities.io.FileUtils;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.MapUtils;

public class TestBiomass {
	
	private static final Logger logger = LoggerFactory.getLogger(TestBiomass.class);

	
	public void doubleConverter() throws ScriptException{
		
		String stoichiometry = "-(n-2)";
		
		stoichiometry = stoichiometry.replaceAll("n", "1");
	
		  // create a script engine manager
	    ScriptEngineManager factory = new ScriptEngineManager();
	    // create a JavaScript engine
	    ScriptEngine engine = factory.getEngineByName("JavaScript");
	    // evaluate JavaScript code from String
	    Object obj = engine.eval(stoichiometry);
	    System.out.println( obj );
	}
	
	@Test
	public void test() throws Exception {
	
		//String proteinPath = "C:/Users/Oscar Dias/Desktop/biomassTests/E.coli_CDS_AA.txt",
		String proteinPath = "D:/OD/OnlineDesktop/biomassTests/Ecoli_MG1655.faa",
				dnaPath = "D:/OD/OnlineDesktop/biomassTests/E.coli_DNA.fna",
				rRnaPath = "D:/OD/OnlineDesktop/biomassTests/E.coli_rRNA.txt",
				mRnaPath = "D:/OD/OnlineDesktop/biomassTests/E.coli_mRNA.fna", 
				tRnaPath = "D:/OD/OnlineDesktop/biomassTests/E.coli_tRNA.txt",
				exportFilePath = "D:/OD/OnlineDesktop/biomassTests/",
				geneData = "D:/OD/OnlineDesktop/biomassTests/E.coli_Genedata.csv",
				separator = ";";
		
		double proteinBiomassContents= 0.55,
				dnaBiomassContents= 0.031,
				rnaBiomassContents= 0.2;
		
		double rRnaContents = 0.8,
				mRnaContents = 0.05,
				tRnaContents = 0.15;
		
		String user = "root";
		String password = "password";
		String databaseName = "merlindb";
		DatabaseAccess da = new H2DatabaseAccess(user, password, databaseName, FileUtils.getHomeFolderPath());;
		Connection connection = new Connection(da);
		
		Map<String, BiomassMetabolite> biomassMetabolites = Utilities.getBiomassMetabolites();
		biomassMetabolites = DatabaseLoaders.getModelInformationForBiomass(biomassMetabolites, connection.createStatement());
		connection.closeConnection();
		
		System.out.println(biomassMetabolites);
		
		logger.info("Proteins ");
		Map<BiomassMetabolite, Double> proteins = EstimateBiomassContents.getProteinsRelativeAbundance(proteinPath, proteinBiomassContents, true, ReturnType.MMol_GDW, exportFilePath+"_prot.txt", biomassMetabolites, geneData, separator);
		logger.info("\nProteins {}", MapUtils.prettyToString(proteins));
		logger.info("DNA");
		Map<BiomassMetabolite, Double> dna = EstimateBiomassContents.getNucleotides_RelativeAbundance(dnaPath, dnaBiomassContents, true, ReturnType.MMol_GDW, exportFilePath+"_dna.txt", biomassMetabolites, false);
		logger.info("\nDNA {}", MapUtils.prettyToString(dna));
		logger.info("rRNA ");
		Map<BiomassMetabolite, Double> rRNA = EstimateBiomassContents.getNucleotides_RelativeAbundance(rRnaPath, rnaBiomassContents, true, ReturnType.MMol_GDW, exportFilePath+"_rrna.txt", biomassMetabolites, true);
		logger.info("mRNA ");
		Map<BiomassMetabolite, Double> mRNA = EstimateBiomassContents.getNucleotides_RelativeAbundance(mRnaPath, rnaBiomassContents, true, ReturnType.MMol_GDW, exportFilePath+"_mrna.txt", biomassMetabolites, true);
		logger.info("tRNA ");
		Map<BiomassMetabolite, Double> tRNA = EstimateBiomassContents.getNucleotides_RelativeAbundance(tRnaPath, rnaBiomassContents, true, ReturnType.MMol_GDW, exportFilePath+"_trna.txt", biomassMetabolites, true);
		logger.info("RNA ");
		Map<BiomassMetabolite, Double> rna = Utilities.mergeRNAMaps(mRNA, mRnaContents, tRNA, tRnaContents, rRNA, rRnaContents);
		
		logger.info("\nRNA {}", MapUtils.prettyToString(rna));

	}
}
