package org.sitoolkit.wt.gui.infra;

import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutableStreamReader implements Runnable {

    private InputStream is;

    private Console console;

    public ExecutableStreamReader(InputStream is, Console console) {
        super();
        this.is = is;
        this.console = console;
    }

    public static void read(InputStream is, Console console) {
        Executor exe = Executors.newCachedThreadPool();
        exe.execute(new ExecutableStreamReader(is, console));
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(is);

        while (scanner.hasNextLine()) {
            console.append(scanner.nextLine());
        }

        scanner.close();

    }

}