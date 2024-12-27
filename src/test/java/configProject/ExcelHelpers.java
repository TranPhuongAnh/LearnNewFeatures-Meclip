package configProject;

import StepDefinitions.DataExport;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelHelpers {

    private FileInputStream fis;
    private FileOutputStream fileOut;
    private Workbook wb;
    private Sheet sh;
    private Cell cell;
    private Row row;
    private CellStyle cellstyle;
    private Color mycolor;
    private String excelFilePath;
    private final Map<String, Integer> columns = new HashMap<>();
    private final String[] fileHeader = {"Phone number", "Url", "Previous views", "After views", "Error message"};

    public void setExcelFile(String ExcelPath, String SheetName) throws Exception {
        try {
            File f = new File(ExcelPath);

            if (!f.exists()) {
                f.createNewFile();
                System.out.println("File doesn't exist, so created!");
            }

            fis = new FileInputStream(ExcelPath);
            wb = WorkbookFactory.create(fis);
            sh = wb.getSheet(SheetName);
            //sh = wb.getSheetAt(0); //0 - index of 1st sheet
            if (sh == null) {
                sh = wb.createSheet(SheetName);
            }

            this.excelFilePath = ExcelPath;

            //adding all the column header names to the map 'columns'
            sh.getRow(0).forEach(cell -> {
                columns.put(cell.getStringCellValue(), cell.getColumnIndex());
            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String getCellData(int rownum, int colnum) throws Exception {
        try {
            cell = sh.getRow(rownum).getCell(colnum);
            String CellData = null;
            switch (cell.getCellType()) {
                case STRING:
                    CellData = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        CellData = String.valueOf(cell.getDateCellValue());
                    } else {
                        CellData = String.valueOf((long) cell.getNumericCellValue());
                    }
                    break;
                case BOOLEAN:
                    CellData = Boolean.toString(cell.getBooleanCellValue());
                    break;
                case FORMULA:
                    CellData = String.valueOf(cell.getNumericCellValue());
                    break;
                case BLANK:
                    CellData = "";
                    break;
            }
            return CellData;
        } catch (Exception e) {
            return "";
        }
    }

    //Gọi ra hàm này nè

    /**
     * Hàm ghi đè, chuyển gọi tên cột đã đặt trong file
     * @param columnName
     * @param rownum
     * @return
     * @throws Exception
     */
    public String getCell(String columnName, int rownum) throws Exception {
        return getCellData(rownum, columns.get(columnName));
    }

    public void setCellData(String text, int rownum, int colnum) throws Exception {
        try {
            row = sh.getRow(rownum);
            if (row == null) {
                row = sh.createRow(rownum);
            }
            cell = row.getCell(colnum);

            if (cell == null) {
                cell = row.createCell(colnum);
            }
            cell.setCellValue(text);

            fileOut = new FileOutputStream(excelFilePath);
            wb.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (Exception e) {
            throw (e);
        }
    }

    public int getSumRow(String path, String sheetName) throws Exception {
        setExcelFile(path, sheetName);
        int sum_row = sh.getLastRowNum();
        return sum_row;
    }

    public int getSumCol(String path, String sheetName) throws Exception {
        setExcelFile(path, sheetName);
        int sum_col = sh.getRow(0).getLastCellNum();
        return sum_col;
    }

    public Sheet ExcelFileCreate(Workbook workbook) throws IOException {
        // Tạo file .xls
        Sheet file = workbook.createSheet("Report");

        // Style
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.RED.getIndex());
        CellStyle style = workbook.createCellStyle();
        style.setFont(headerFont);

        // Tạo Row đầu tiên
        Row headerRow = file.createRow(0);
        for (int i = 0; i < fileHeader.length ; i++){
            Cell c = headerRow.createCell(i);
            c.setCellValue(fileHeader[i]);
            c.setCellStyle(style);
        }

        return file;
    }

    public void setDataExl_String(Sheet sheet, int rowNum, int col, String data){
        Row r = sheet.createRow(rowNum);
        r.createCell(col).setCellValue(data);
    }
    public void setDataExl_Int(Sheet sheet, int rowNum, int col, int data){
        Row r = sheet.createRow(rowNum);
        r.createCell(col, CellType.NUMERIC).setCellValue(data);
    }
    public void setData_Object(Sheet sheet, Object[][] objData){
        int rowCount = 0;
        for (Object[] a : objData) {
            Row row = sheet.createRow(++rowCount);

            int columnCount = 0;

            for (Object field : a) {
                Cell cell = row.createCell(++columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
                sheet.autoSizeColumn(++columnCount);
            }
        }
    }
    public void setData_Class(Sheet sheet, List<DataExport> data){
        int rowCount = 0;
        for (DataExport obj : data) {
            Row r = sheet.createRow(++rowCount);
            r.createCell(0).setCellValue(obj.getPhone());
            r.createCell(1).setCellValue(obj.getUrl());
            r.createCell(2).setCellValue(obj.getPre_view());
            r.createCell(3).setCellValue(obj.getAfter_view());
            r.createCell(4).setCellValue(obj.getMessage_error());

        }
        // auto-fit column trong sheet
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
    }

    public List<String> readUrlExcel(String path, String sheetName) throws Exception {
        List<String> urls = new ArrayList<>();
        int sum_row = getSumRow(path, sheetName);
        for (int i = 0 ; i < sum_row ; i++){
            Row r = sh.getRow(i);
            if (r != null){
                Cell c = r.getCell(0);
                if (c != null){
                    String url = c.getStringCellValue();
                    if (url != null && !url.isEmpty()){
                        urls.add(url);
                    }
                }
            }
        }

        return urls;
    }
}
