package ch.redguard.burp.sheet_intruder.excel;

import ch.redguard.burp.sheet_intruder.TestLogging;
import ch.redguard.burp.sheet_intruder.TestUtil;
import ch.redguard.burp.sheet_intruder.parser.Mode;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ExcelParserTest {
    @Test
    void readAndReplace() throws IOException {
        var logging = new TestLogging();
        var parser = new ExcelParser(TestUtil.getXlsxFile(), logging);
        var replacedBytes = parser.readAndReplace(Map.of(
                "valueToReplace", "replacement",
                "valueToReplace2", "replacement2"
        ), Mode.DEFAULT).get();

        try (ByteArrayInputStream bis = new ByteArrayInputStream(replacedBytes)) {
            Workbook workbook = new XSSFWorkbook(bis);
            try (FileOutputStream fis = new FileOutputStream("src/test/resources/changed.xlsx")) {
                workbook.write(fis);
            }
        }

        var cellValues = getAllCellValues(replacedBytes);
        var frequency = cellValues.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        assertEquals(2, frequency.get("replacement"));
        assertEquals(2, frequency.get("replacement2"));
    }

    @Test
    void testXls() throws IOException {
        var logging = new TestLogging();
        var parser = new ExcelParser(TestUtil.getXlsFile(), logging);
        var replacedBytes = parser.readAndReplace(Map.of(
                "valueToReplace", "replacement",
                "valueToReplace2", "replacement2"
        ), Mode.DEFAULT).get();

        var cellValues = getAllCellValues(replacedBytes);
        var frequency = cellValues.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        assertEquals(2, frequency.get("replacement"));
        assertEquals(2, frequency.get("replacement2"));
    }

    @Test
    void testCellMode() throws IOException {
        var logging = new TestLogging();
        var parser = new ExcelParser(TestUtil.getXlsFile(), logging);
        var excelBytes = parser.readAndReplace(Map.of(
                "A1", "A1Replaced",
                "B12", "B12Replaced"
        ), Mode.CELL).get();

        ByteArrayInputStream bis = new ByteArrayInputStream(excelBytes);
        Workbook workbook = WorkbookFactory.create(bis);
        var sheet = workbook.getSheetAt(0);
        assertEquals("A1Replaced", sheet.getRow(0).getCell(0).getStringCellValue());
        assertEquals("B12Replaced", sheet.getRow(11).getCell(1).getStringCellValue());
    }

    @Test
    void testCellModeWithSheet() throws IOException {
        var logging = new TestLogging();
        var parser = new ExcelParser(TestUtil.getXlsFile(), logging);
        var excelBytes = parser.readAndReplace(Map.of(
                "A1", "A1Replaced",
                "CustomSheet!B21", "otherSheetB21"
        ), Mode.CELL).get();

        ByteArrayInputStream bis = new ByteArrayInputStream(excelBytes);
        Workbook workbook = WorkbookFactory.create(bis);
        var sheet = workbook.getSheetAt(0);
        assertEquals("A1Replaced", sheet.getRow(0).getCell(0).getStringCellValue());

        var otherSheet = workbook.getSheet("CustomSheet");
        assertEquals("otherSheetB21", otherSheet.getRow(21 - 1).getCell(1).getStringCellValue());

        assertEquals("shouldStay", sheet.getRow(21 - 1).getCell(1).getStringCellValue());
    }

    @Test
    void testCellModeWithRanges() throws IOException {
        var logging = new TestLogging();
        var parser = new ExcelParser(TestUtil.getXlsFile(), logging);
        var excelBytes = parser.readAndReplace(Map.of(
                "A1:D5", "replacement"
        ), Mode.CELL).get();

        ByteArrayInputStream bis = new ByteArrayInputStream(excelBytes);
        Workbook workbook = WorkbookFactory.create(bis);
        var sheet = workbook.getSheetAt(0);

        for (int rowNum = 0; rowNum < 4; rowNum++) {
            for (int cellNum = 0; cellNum < 4; cellNum++) {
                assertEquals("replacement", sheet.getRow(rowNum).getCell(cellNum).getStringCellValue());
            }
        }
    }

    @Test
    void testCellModeWithRangeOtherSheet() throws IOException {
        var logging = new TestLogging();
        var parser = new ExcelParser(TestUtil.getXlsFile(), logging);
        var excelBytes = parser.readAndReplace(Map.of(
                "CustomSheet!A1:D5", "replacement"
        ), Mode.CELL).get();

        ByteArrayInputStream bis = new ByteArrayInputStream(excelBytes);
        Workbook workbook = WorkbookFactory.create(bis);
        var otherSheet = workbook.getSheet("CustomSheet");

        for (int rowNum = 0; rowNum < 4; rowNum++) {
            for (int cellNum = 0; cellNum < 4; cellNum++) {
                assertEquals("replacement", otherSheet.getRow(rowNum).getCell(cellNum).getStringCellValue());
            }
        }

    }

    @Test
    void testReferenceInvalidSheet() {
        var logging = new TestLogging();
        var parser = new ExcelParser(TestUtil.getXlsFile(), logging);
        var excelBytes = parser.readAndReplace(Map.of(
                "SheetDoesNotExist!B21", "otherSheetB21"
        ), Mode.CELL);

        assertEquals(Optional.empty(), excelBytes);
    }


    private List<String> getAllCellValues(byte[] excelBytes) throws IOException {
        List<String> cellValues = new ArrayList<>();

        ByteArrayInputStream bis = new ByteArrayInputStream(excelBytes);
        Workbook workbook = WorkbookFactory.create(bis);

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellType().equals(CellType.STRING)) {
                        cellValues.add(cell.getStringCellValue());
                    }
                }
            }
        }

        bis.close();
        return cellValues;
    }

}