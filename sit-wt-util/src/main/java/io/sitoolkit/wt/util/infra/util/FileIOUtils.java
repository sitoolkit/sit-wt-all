package io.sitoolkit.wt.util.infra.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import io.sitoolkit.wt.util.infra.UnExpectedException;
import io.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import io.sitoolkit.wt.util.infra.process.StdoutListener;
import io.sitoolkit.wt.util.infra.process.StdoutListenerContainer;

public class FileIOUtils {

    private static final Logger LOG = Logger.getLogger(FileIOUtils.class.getName());

    public static void download(String url, File destFile) {
        Stopwatch.start();

        LOG.log(Level.INFO, "downloading url : {0}, destFile : {1}", new Object[]{url, destFile.getAbsolutePath()});

        File destDir = destFile.getParentFile();
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        URLConnection conn = null;
        try {
            conn = new URL(url).openConnection();
        } catch (IOException e) {
            throw new UnExpectedException(e);
        }

        DownloadWatcher watcher = new DownloadWatcher(conn.getContentLength(), destFile);
        ExecutorContainer.get().execute(watcher);

        try (InputStream stream = conn.getInputStream()) {
            Files.copy(stream, destFile.toPath());
            watcher.downloaded = true;
        } catch (IOException e) {
            throw new UnExpectedException(e);
        }

        LOG.log(Level.INFO, "downloaded in {0}", Stopwatch.end());
    }

    static class DownloadWatcher implements Runnable {

        boolean downloaded = false;
        int contentLength;
        File destFile;

        DownloadWatcher(int contentLength, File destFile) {
            super();
            this.contentLength = contentLength;
            this.destFile = destFile;
        }

        @Override
        public void run() {
            for (StdoutListener listener : StdoutListenerContainer.get().getListeners()) {
                listener.nextLine("downloading " + destFile.getAbsolutePath());
            }
            while (!downloaded) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    LOG.log(Level.WARNING, "", e);
                }
                String log = "downloaded " + destFile.length() / 1024 + " / " + contentLength / 1024 + " KB";
                for (StdoutListener listener : StdoutListenerContainer.get().getListeners()) {
                    listener.nextLine(log);
                }
                LOG.log(Level.INFO, log);
            }
        }

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

    public static void copyDirectoryWithPermission(Path src, Path parent) {
        try {
            Path dst = Paths.get(parent.toString(), src.toFile().getName());
            Files.copy(src, dst, StandardCopyOption.COPY_ATTRIBUTES);

            if (Files.isDirectory(src)) {
                try (Stream<Path> children = Files.list(src)) {
                    children.forEach(child -> {
                        copyDirectoryWithPermission(child, dst);
                    });
                } catch (IOException e) {
                    throw new UnExpectedException(e);
                }
            }
        } catch (IOException e) {
            throw new UnExpectedException(e);
        }
    }

    public static void sysRes2file(String resourceName, Path targetPath) {
        sysRes2file(resourceName, targetPath, false);
    }

    public static void sysRes2file(String resourceName, Path targetPath, boolean deleteOnExit) {
        URL resourceUrl = ClassLoader.getSystemResource(resourceName);

        try {
            File targetFile = targetPath.toFile();
            if (deleteOnExit) targetFile.deleteOnExit();
            FileUtils.copyURLToFile(resourceUrl, targetFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void main(String[] args) {
        download("https://github.com/sitoolkit/sit-wt-all/releases/download/v2.0/maven-repository-sit-wt.zip", new File("./temp.zip"));
    }
}
