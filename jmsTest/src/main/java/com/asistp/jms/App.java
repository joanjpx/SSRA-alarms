package com.asistp.jms;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		/*
		 * String date = "2018-02-26 14:32:57"; SimpleDateFormat simpleDate = new
		 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); Date dateAttribute = null; try {
		 * dateAttribute = simpleDate.parse(date); } catch (ParseException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } System.out.println(
		 * "Hello World!" + dateAttribute );
		 */

		/*Workbook workbook;
		try {
			workbook = WorkbookFactory.create(new File("C:\\Users\\Manuel CD\\Downloads\\match-Nokia-V.4.xlsx"));
			Sheet sheet = workbook.getSheetAt(1);
			
			Map<String, String[]> codes = new HashMap<>();
			codes.put("sdsd", new String[] {"dd", "33"});

			// Create a DataFormatter to format and get each cell's value as String
			DataFormatter dataFormatter = new DataFormatter();
			sheet.forEach(row -> {
				String alarmCode = dataFormatter.formatCellValue(row.getCell(2));
				String alarmName = dataFormatter.formatCellValue(row.getCell(1));
				String alarmProbableCause = dataFormatter.formatCellValue(row.getCell(5));
				String alarmDescription = dataFormatter.formatCellValue(row.getCell(6));
					System.out.print("codes.put(\"" + alarmCode + "\", new String[] {\"" + alarmName + "\", \"" + alarmProbableCause + "\", \"" + alarmDescription + "\"});");
				
				System.out.println();
			});

			// Closing the workbook
			workbook.close();

		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
		/*
		 * try { throw new Exception("wewewewewe"); } catch (Exception e) { try {
		 * System.out.println("Dentro accccccc"); } catch (Exception ex) {
		 * System.out.println("Error(): " + e.getMessage()); }
		 * 
		 * }
		 */
	}
}
