package ch.redguard.burp.sheet_intruder;

import java.io.File;

public class TestUtil {
    public static File getXlsxFile() {
        return new File("src/test/resources/Book1.xlsx");
    }


    public static File getXlsFile() {
        return new File("src/test/resources/Book1.xls");
    }

}
