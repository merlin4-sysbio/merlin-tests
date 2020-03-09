package pt.uminho.ceb.biosystems.merlin.tests.merlin.gpr;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.database.connector.databaseAPI.ModelAPI;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.Connection;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.DatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.database.connector.datatypes.MySQLDatabaseAccess;
import pt.uminho.ceb.biosystems.merlin.gpr.rules.core.FilterModelReactions;
import pt.uminho.ceb.biosystems.merlin.gpr.rules.core.IdentifyGenomeSubunits;
import pt.uminho.ceb.biosystems.merlin.utilities.Enumerators.Method;

public class GprTests {

	@Test
	public void test() throws Exception {
		
		String args[] = null;
		
			long reference_organism_id = Long.parseLong(args[0]);
			double similarity_threshold = Double.parseDouble(args[1]);
			double referenceTaxonomyThreshold = Double.parseDouble(args[2]); 
			String user = args[3];
			String password = args[4];
			String server = args[5]; 
			int port = Integer.parseInt(args[6]);
			String database = args[7];
			String file = args[8];
			boolean compareToFullGenome = Boolean.parseBoolean(args[10]);
			boolean identifyOrAssign = Boolean.parseBoolean(args[11]);
			boolean integrateToDatabase = Boolean.parseBoolean(args[12]);
			boolean keepReactionsWithNotes = Boolean.parseBoolean(args[13]);
			double threshold = Double.parseDouble(args[14]);
			boolean keepManualReactions = true;

			boolean originalReaction = false;

			DatabaseAccess msqlmt = new MySQLDatabaseAccess(user, password, server, port, database);
			Connection connection = new Connection(msqlmt);
			
			if(identifyOrAssign) {

				Method method = Method.SmithWaterman;

				Map<String, List<String>> ec_numbers = ModelAPI.getECNumbers(connection);
				System.out.println("Enzymes size:\t"+ec_numbers.keySet().size());

				Map<String, AbstractSequence<?>> genome =  new HashMap<>();
				genome.putAll(FastaReaderHelper.readFastaProteinSequence(new File(file)));

				Map<String, AbstractSequence<?>> newGenome = genome;

				System.out.println("Genome size:\t"+newGenome.keySet().size());

				IdentifyGenomeSubunits i = new IdentifyGenomeSubunits(ec_numbers, newGenome, reference_organism_id, connection, similarity_threshold, 
						referenceTaxonomyThreshold, method, compareToFullGenome);
				i.runIdentification(true);

			}
			else {


				FilterModelReactions f = new FilterModelReactions(connection, originalReaction);
				f.filterReactions(IdentifyGenomeSubunits.runGPRsAssignment(threshold, new Connection(msqlmt)));

				if(integrateToDatabase) {

					f.removeReactionsFromModel(keepReactionsWithNotes, keepManualReactions);
					f.setModelGPRsFromTool();
				}
			}
	}

}
