package pt.uminho.ceb.biosystems.merlin.tests.sequence;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.junit.Test;

public class FastaReader {

	@Test
	public void test() {
		
		try {
			LinkedHashMap<String, ProteinSequence> sequences = FastaReaderHelper.readFastaProteinSequence(new File("C:\\Users\\BioSystems\\Downloads\\emanuel.faa"));
			
			for(String key : sequences.keySet()) {
				
				ProteinSequence seq = sequences.get(key);
				
				System.out.println(key);
				System.out.println(seq.getOriginalHeader());
				System.out.println(seq.getSequenceAsString());
				System.out.println(seq.getLength());
				System.out.println(seq.getSequenceAsString().length());
				System.out.println();
				
				
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
	}

}
