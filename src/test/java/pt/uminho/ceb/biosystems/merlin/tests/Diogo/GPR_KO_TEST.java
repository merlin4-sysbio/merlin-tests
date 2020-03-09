package pt.uminho.ceb.biosystems.merlin.tests.Diogo;

import org.junit.Test;

public class GPR_KO_TEST {


	@Test
	public void runGPR_KO() {

		try {

			String inputFile = "C://Users//BioSystems//Desktop//kos.txt";
			String outputFilePath = "C://Users//BioSystems//Desktop//kos.xlsx";
			
			String inputFile2 = "C://Users//BioSystems//Desktop//kos2.txt";
			String outputFilePath2 = "C://Users//BioSystems//Desktop//kos2.xlsx";
			
			String inputFile3 = "C://Users//BioSystems//Desktop//kos3.txt";
			String outputFilePath3 = "C://Users//BioSystems//Desktop//kos3.xlsx";
			
			//GPR_KO.main(inputFile, outputFilePath);
			//GPR_KO.main(inputFile2, outputFilePath2);
			GPR_KO.main(inputFile3, outputFilePath3);



		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

