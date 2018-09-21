package io.sitoolkit.wt.gui.infra.fx;

import java.io.File;

public class FileWrapper {

    private File file;

    public FileWrapper(String file) {
        this(new File(file));
    }

    public FileWrapper(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return file.getName();
    }

}
