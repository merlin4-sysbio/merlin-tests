package pt.uminho.ceb.biosystems.merlin.tests.blast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.utilities.blast.ebi_blastparser.EbiBlastParser;

public class BlastTest {

	@Test
	public void test() throws IOException {

		URL link1 = new URL("https://www.uniprot.org/blast/uniprot/B20200214E5A08BB0B2D1C45B0C7BC3B55FD2655614EB29E.xml");
	    InputStream xml = link1.openStream();

		EbiBlastParser ebp = new EbiBlastParser(xml);
		
	}

}
