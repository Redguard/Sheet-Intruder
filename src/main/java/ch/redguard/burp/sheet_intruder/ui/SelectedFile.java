package ch.redguard.burp.sheet_intruder.ui;

import java.io.File;
import java.util.Optional;

public class SelectedFile {
    private static SelectedFile instance;
    private Optional<File> file;

    private SelectedFile() {
    }

    public static SelectedFile getInstance() {
        if (instance == null) {
            instance = new SelectedFile();
        }
        return instance;
    }


    public Optional<File> getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = Optional.of(file);
    }

    public void unloadFile() {
        this.file = Optional.empty();
    }

}
