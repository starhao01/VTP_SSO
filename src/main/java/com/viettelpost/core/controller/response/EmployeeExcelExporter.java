package com.viettelpost.core.controller.response;

import com.viettelpost.core.services.domains.EmployeeInfo;

import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import static org.apache.poi.ss.util.CellUtil.createCell;

public class EmployeeExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<EmployeeInfo> listEmployee;

    public EmployeeExcelExporter(List<EmployeeInfo> listEmployee) {
        this.listEmployee = listEmployee;
        workbook = new XSSFWorkbook();
    }
    private void writeHeaderLine() {
        sheet = workbook.createSheet("EmployeeInfo");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "ID", style);
        createCell(row, 1, "Username", style);
        createCell(row, 2, "Employee Code", style);
        createCell(row, 3, "Display Name", style);
        createCell(row, 4, "Email", style);
        createCell(row, 5, "IdentifierCreateOnDate", style);
        createCell(row, 6, "Active", style);

    }
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (EmployeeInfo employeeInfo : listEmployee) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, employeeInfo.getId().toString(), style);
            createCell(row, columnCount++, employeeInfo.getUser_name(), style);
            createCell(row, columnCount++, employeeInfo.getEmployee_code(), style);
            createCell(row, columnCount++, employeeInfo.getDisplay_name(), style);
            createCell(row, columnCount++, employeeInfo.getEmail(), style);
            createCell(row, columnCount++, employeeInfo.getIdentifiercreateondate(), style);
            createCell(row, columnCount++, employeeInfo.getActive().toString(), style);
        }
    }
    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

    }
}
