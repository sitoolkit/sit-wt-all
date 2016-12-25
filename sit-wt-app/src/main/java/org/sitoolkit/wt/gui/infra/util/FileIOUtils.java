package org.sitoolkit.wt.gui.infra.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.sitoolkit.wt.gui.infra.UnExpectedException;

public class FileIOUtils {

    private static final Logger LOG = Logger.getLogger(FileIOUtils.class.getName());

    public static void download(String url, File destFile) {
        Stopwatch.start();

        LOG.log(Level.INFO, "downloading url : {0}, destFile : {1}", new Object[]{url, destFile.getAbsolutePath()});

        File destDir = destFile.getParentFile();
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        try (InputStream stream = new URL(url).openStream()) {
            Files.copy(stream, destFile.toPath());
        } catch (IOException e) {
            throw new UnExpectedException(e);
        }

        LOG.log(Level.INFO, "downloaded in {0}", Stopwatch.end());
    }

    public static void unarchive(File srcFile, File destDir) {
        Stopwatch.start();

        LOG.log(Level.INFO, "unarchive src : {0}, dest : {1}",
                new Object[]{srcFile.getAbsolutePath(), destDir.getAbsolutePath()});

        try (ZipFile zipFile = new ZipFile(srcFile)) {
            Enumeration<? extends ZipEntry> enu = zipFile.entries();

            while (enu.hasMoreElements()) {
                ZipEntry zipEntry = enu.nextElement();

                String name = zipEntry.getName();

                File entryFile = new File(destDir, name);

                if (entryFile.exists()) {
                    continue;
                }

                // Do we need to create a directory ?
                if (name.endsWith("/")) {
                    entryFile.mkdirs();
                    continue;
                }

                File parent = entryFile.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }

                // Extract the file

                try (InputStream is = zipFile.getInputStream(zipEntry)) {
                    Files.copy(is, entryFile.toPath());
                } catch (IOException e) {
                    throw new UnExpectedException(e);
                }

            }

        } catch (IOException e) {
            throw new UnExpectedException(e);
        }

        LOG.log(Level.INFO, "unarchived in {0}", Stopwatch.end());
    }

    public static String read(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    public static void stream2file(InputStream stream) {

    }

    public static String file2str(File file) {

        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.lines().forEach(line -> {
                sb.append(line);
                sb.append(System.lineSeparator());
            });

        } catch (IOException e) {
            throw new UnExpectedException(e);
        }

        return sb.toString();
    }

    public static void copy(File src, File dst) {
        LOG.log(Level.INFO, "{0} copy to {1}",
                new Object[] { src.getAbsolutePath(), dst.getAbsolutePath() });
        try {
            Files.copy(src.toPath(), dst.toPath());
        } catch (IOException e) {
            throw new UnExpectedException(e);
        }
    }
}
