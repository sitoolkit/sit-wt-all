package org.sitoolkit.wt.infra;

import java.io.File;

import org.sitoolkit.util.tabledata.FileOverwriteChecker;

public class VoidFileOverwriteChecker extends FileOverwriteChecker {

    @Override
    public boolean isWritable(File arg0) {
        return true;
    }
}
