package pt.uminho.ceb.biosystems.merlin.tests.Diogo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.utilities.Enumerators;
import pt.uminho.ceb.biosystems.merlin.services.ProjectServices;
import pt.uminho.ceb.biosystems.merlin.utilities.blast.ebi_blastparser.EBIApplicationResult;
import pt.uminho.ceb.biosystems.merlin.utilities.blast.ncbi_blastparser.BlastOutput;

public class RandomTests {

	
	//@Test
	public void tester () {
		
		try {
			//System.out.println(Enumerators.FileExtensions.PROTEIN_FAA.getName());
			Long taxId = Long.valueOf(ProjectServices.getOrganismID("thiomonas_v4").toString());
			System.out.println(taxId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void orderMapByValueInteger() {
		
		try {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			map.put("a", 4);
			map.put("c", 6);
			map.put("b", 2);
			
			LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
			//Use Comparator.reverseOrder() for reverse ordering
			map.entrySet()
			   .stream()
			   .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			   .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
			System.out.println("Reverse Sorted Map   : " + reverseSortedMap);
		
			for(String key : reverseSortedMap.keySet())
				System.out.println(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	//@Test
	public void cleanOldFilesFromDirectory (String directory, Integer daysThreshold) {
		
		try {
	
			File folder = new File(directory);
			File[] listOfFiles = folder.listFiles();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			
			if(listOfFiles == null) { // wrong input, if the folder does not exist there is nothing to clean
				throw new Exception("Input directory does not exist");
			}
			
			for (File file : listOfFiles) {
			    if (file.isFile()) {
			    	
			    	Date lastModified = sdf.parse(sdf.format(file.lastModified())); // last modified date of file
			    	Calendar c = Calendar.getInstance();
			    	Date currentDate = new Date(System.currentTimeMillis()); // current time
			    	c.setTime(currentDate);
			    	c.add(Calendar.DAY_OF_YEAR, -daysThreshold); // go back "input" days in time
			    	Date thresholdDate = c.getTime();
			    	Long difference = lastModified.getTime() - thresholdDate.getTime(); 
			    	long parsedDifference = TimeUnit.DAYS.convert(difference,  TimeUnit.MILLISECONDS); // difference in days between the last modified date of the file and today's date
			    	
			    	if(parsedDifference < 0) { // file is older than the input threshold, thus it must be deleted
			    		file.delete();			    		
			    	}
			    }
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//@Test
		public void runTest () {
		
		String directory = "C:\\Users\\Diogo\\Desktop\\daysTt";
		Integer days = 0;
		cleanOldFilesFromDirectory(directory, days);
	}
	
		
		@Test
		public void ebiParser () {
			
			try {
		
				EBIApplicationResult ebiResult = new EBIApplicationResult();
				
				File outputXml = new File ("C:\\Users\\Diogo\\Desktop\\merlin_debug\\streamXML.xml");
				JAXBContext jc = JAXBContext.newInstance(BlastOutput.class);
		        Unmarshaller unmarshaller = jc.createUnmarshaller();
				EBIApplicationResult blout = (EBIApplicationResult) unmarshaller.unmarshal(outputXml);
				

				
		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//	@Test
//		public boolean compareTimes (Calendar calendar1, Calendar calendar2, Integer secondsThreshold) {
//		
//		
//		boolean largerThanThreshold = true;
//		Long difference = calendar1.getTime().getTime() - calendar2.getTime().getTime(); 
//		long parsedDifference = TimeUnit.SECONDS.convert(difference,  TimeUnit.MILLISECONDS); // difference in days between the last modified date of the file and today's
//		if(parsedDifference > secondsThreshold)
//			return largerThanThreshold;
//		
//
//}
//	
}
