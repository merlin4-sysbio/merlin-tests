package pt.uminho.ceb.biosystems.merlin.tests.excel;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcelFile {

	/**
	 * Get all data from excel file
	 * 
	 * @param path
	 * @return
	 */
	public static List<String[]> getData(String path) {

		List<String[]> results = new ArrayList<>();

		try {
			FileInputStream excelFile = new FileInputStream(new File(path));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet sheet = workbook.getSheetAt(0);

			Iterator<Row> iterator = sheet.iterator();

			iterator = sheet.iterator();
			DataFormatter formatter = new DataFormatter();

			while (iterator.hasNext()) {
				
				Row currentRow = iterator.next();
				
				if(currentRow.getRowNum() != 0){
					Iterator<Cell> cellIterator = currentRow.iterator();

					String[] line = new String[currentRow.getLastCellNum()+1];

					while (cellIterator.hasNext()) {

						Cell currentCell = cellIterator.next();

						String value = formatter.formatCellValue(currentCell);

						line[currentCell.getColumnIndex()] = value;

					}
					
//					System.out.println(Arrays.asList(line));

					results.add(line);
					
				}
			}
			
			workbook.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

		return results;
	}
	
	/**
	 * Method to print a given table.
	 * 
	 * @param table
	 */
	public static void printTable(List<String[]> table) {
		
		for(int i = 0; i < table.size(); i++){

			String[] line = table.get(i);

			for(int j = 0; j < line.length; j++){

				System.out.print(line[j] + "           ");
			}

			System.out.println();
		}
		
	}
	
	public static Map<String, List<String>> readStatusValidation(String path){
		
		Map<String, List<String>> results = new HashMap<>();
		
		List<String[]> table = getData(path);
		
		for(String[] line : table) {
			
			List<String> l = new ArrayList<>();
			
			if(line.length > 5)
				l.add(line[5]);

			if(line.length > 6)
				l.add(line[6]);
			
			results.put(line[0], l);
		}
		
		return results;
	}


}