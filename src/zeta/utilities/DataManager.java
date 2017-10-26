package zeta.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 * 
 * @author nwisnewski
 *
 */
public class DataManager {
	private XSSFWorkbook workBook; 	
	private XSSFSheet dataSheet;
	private final String path=System.getProperty("user.dir")+ File.separator +"testData"+File.separator+"ZetaData.xlsx";
	private static DataManager instance = null;
	
	public DataManager(){
		this.workBook = getWorkBook();
		this.dataSheet = getSheet(workBook);
	}
	
	public static DataManager getData(){
		if(instance == null){
			instance = new DataManager();
		}
		return instance;
	}

	public Hashtable<String, String>  loadTestData(String contentTemplate) {
		ArrayList<String> columNames= getColumnNames(dataSheet);
		Row testDataRow= getTestData(dataSheet, contentTemplate);
		if(testDataRow==null){
			throw new RuntimeException("Unable to find testdata row for " +contentTemplate);
		}
		Hashtable<String, String> testData=prepareTestDataRowHashTable(columNames,testDataRow);
		return testData;
		
	}
	

	private XSSFSheet getSheet(XSSFWorkbook xssfWorkbook) {
		XSSFSheet sheet = xssfWorkbook.getSheetAt(0); 
		return sheet;
	}

	private XSSFWorkbook getWorkBook() {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(path);
			XSSFWorkbook hssfWorkbook = new XSSFWorkbook(fileInputStream);
			fileInputStream.close();
			return hssfWorkbook;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	private Hashtable<String, String> prepareTestDataRowHashTable(
		ArrayList<String> columNames, Row testDataRow) {
		Hashtable<String, String> testDataRowHashTable= new Hashtable<String,String>();		
		for (Cell cell : testDataRow) {
			String columnName=columNames.get(cell.getColumnIndex());
			String columnValue=cell.getStringCellValue();
			testDataRowHashTable.put(columnName, columnValue);
		}
		return testDataRowHashTable;
	}

	private ArrayList<String> getColumnNames(Sheet testDataSheet) {
		ArrayList<String> columnNameList= new ArrayList<String>();
		Row row = testDataSheet.getRow(0);
		for (Cell cell : row) {
			columnNameList.add(cell.getStringCellValue());
		}
		return columnNameList;
	}

		
	private Row getTestData(Sheet testDataSheet, String contentTemplate) {
		for (Row row : testDataSheet) {
			if (IsRequiredTestCaseRow(row,contentTemplate)) {
				return row;
			} 
		}		
		return null;
	}

	private boolean IsRequiredTestCaseRow(Row row, String contentName) {
		Cell testCaseIdCell= row.getCell(0);
		String testCaseId=testCaseIdCell.getStringCellValue();
		if (testCaseId.equals(contentName)) {
			return true;
		}	
		return false;
	}
	
	public void purgeSuiteRunResults() throws IOException{
		int col = -1;
		Row columnHeaders = dataSheet.getRow(0);
		for(Cell header: columnHeaders){
			if(header.getStringCellValue().equalsIgnoreCase("Status")){
				col = header.getColumnIndex();
				System.out.println(col);
				break;
			}
		}
		Cell purgeValue = null;
		for(Row row: dataSheet){
			if(row.getRowNum()>0){
				purgeValue = row.getCell(col);
				System.out.println(purgeValue.getStringCellValue());
				purgeValue.setCellValue("Null");
			}
		}
		FileOutputStream out = new FileOutputStream(path);
		workBook.write(out);
	    out.close();
	}
	
	
	public void writeTestData(String contentTemplate, String cmsData, String dataTitle) throws IOException{
		int col = -1;
		int row = -1;
		Row columnHeaders = dataSheet.getRow(0);
		for(Cell header: columnHeaders){
			if(header.getStringCellValue().equalsIgnoreCase(cmsData)){
				col = header.getColumnIndex();
				break;
			}
		}
		for(Row rowLabel : dataSheet){
			if(rowLabel.getCell(0).getStringCellValue().equalsIgnoreCase(contentTemplate)){
				row = rowLabel.getCell(0).getRowIndex();
				break;
			}
		}
		Row cellCreater = dataSheet.getRow(row);
		Cell createValue = cellCreater.getCell(col);
		createValue.setCellValue(dataTitle);
		FileOutputStream out = new FileOutputStream(path);
		workBook.write(out);
	    out.close();
	}
}


