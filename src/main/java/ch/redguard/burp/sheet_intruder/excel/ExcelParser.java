package ch.redguard.burp.sheet_intruder.excel;

import burp.api.montoya.logging.Logging;
import ch.redguard.burp.sheet_intruder.parser.IParser;
import ch.redguard.burp.sheet_intruder.parser.Mode;
import org.apache.poi.ss.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ExcelParser implements IParser {
    private final File file;
    private final Logging logging;

    public ExcelParser(File file, Logging logging) {
        this.file = file;
        this.logging = logging;
    }

    private static void replaceValues(Map<String, String> replacements, Row row) {
        for (Cell cell : row) {
            if (cell.getCellType() == CellType.STRING) {
                String cellValue = cell.getStringCellValue();
                for (Map.Entry<String, String> entry : replacements.entrySet()) {
                    if (cellValue.contains(entry.getKey())) {
                        cellValue = cellValue.replace(entry.getKey(), entry.getValue());
                        cell.setCellValue(cellValue);
                    }
                }
            }
        }
    }

    public static Validity getValidity(File file) {
        try (var ignored = WorkbookFactory.create(file)) {
            return new Validity(true, "");
        } catch (Exception e) {
            return new Validity(false, e.getMessage());
        }
    }

    public Optional<byte[]> readAndReplace(Map<String, String> replacements, Mode mode) {
        logging.raiseDebugEvent("Reading file " + file.getName() + " from config and applying replacements. Mode: " + mode.name());
        try (FileInputStream fileInputStream = new FileInputStream(file); Workbook workbook =
                WorkbookFactory.create(fileInputStream)) {
            workbook.setForceFormulaRecalculation(true);
            if (mode == Mode.CELL) {
                replaceCells(replacements, workbook);
            } else {
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    Sheet sheet = workbook.getSheetAt(i);

                    for (Row row : sheet) {
                        replaceValues(replacements, row);
                    }
                }
            }
            var byteOutput = new ByteArrayOutputStream();
            workbook.write(byteOutput);
            workbook.close();
            byteOutput.close();
            return Optional.of(byteOutput.toByteArray());
        } catch (IOException e) {
            logging.raiseErrorEvent("An error occurred while reading the excel file: " + e.getMessage());
        }
        logging.raiseDebugEvent("No excel file content found, no replacements made!");
        return Optional.empty();
    }

    private void replaceCells(Map<String, String> replacements, Workbook workbook) throws IOException {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String cellReference = entry.getKey();
            int exclamationIndex = cellReference.indexOf("!");
            Sheet targetSheet;
            if (exclamationIndex == -1) {
                targetSheet = workbook.getSheetAt(0);
            } else {
                String sheetName = cellReference.substring(0, exclamationIndex);
                targetSheet = workbook.getSheet(sheetName);
                cellReference = cellReference.substring(exclamationIndex + 1);
            }
            if (targetSheet == null) {
                throw new IOException("Configured sheet not found");
            }

            if (cellReference.contains(":")) {
                replaceRange(cellReference, entry.getValue(), targetSheet);
            } else {
                replaceCell(cellReference, entry.getValue(), targetSheet);
            }
        }
    }

    private void replaceCell(String cellReference, String value, Sheet targetSheet) {
        int columnIndex = getColumnIndex(cellReference);
        int rowIndex = Integer.parseInt(cellReference.replaceAll("[A-Za-z]", "")) - 1;
        Row targetRow = targetSheet.getRow(rowIndex);
        if (targetRow == null) {
            targetRow = targetSheet.createRow(rowIndex);
        }
        Cell cell = targetRow.getCell(columnIndex);
        if (cell == null) {
            cell = targetRow.createCell(columnIndex);
        }
        cell.setCellValue(value);
    }

    private void replaceRange(String cellRange, String value, Sheet targetSheet) {
        String[] cellReferences = cellRange.split(":");
        String startCellReference = cellReferences[0];
        String endCellReference = cellReferences[1];

        int startRowIndex = Integer.parseInt(startCellReference.replaceAll("[A-Za-z]", "")) - 1;
        int endRowIndex = Integer.parseInt(endCellReference.replaceAll("[A-Za-z]", "")) - 1;
        int startColumnIndex = getColumnIndex(startCellReference);
        int endColumnIndex = getColumnIndex(endCellReference);

        for (int rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++) {
            Row targetRow = targetSheet.getRow(rowIndex);
            if (targetRow == null) {
                targetRow = targetSheet.createRow(rowIndex);
            }
            for (int columnIndex = startColumnIndex; columnIndex <= endColumnIndex; columnIndex++) {
                Cell cell = targetRow.getCell(columnIndex);
                if (cell == null) {
                    cell = targetRow.createCell(columnIndex);
                }
                cell.setCellValue(value);
            }
        }
    }

    private int getColumnIndex(String cellReference) {
        String columnReference = cellReference.replaceAll("[0-9]", "");
        int columnIndex = -1;
        for (int i = 0; i < columnReference.length(); i++) {
            columnIndex = (columnIndex + 1) * 26 + columnReference.charAt(i) - 'A';
        }
        return columnIndex;
    }
}

