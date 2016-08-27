package org.sitoolkit.wt.gui.infra;

import java.io.InputStream;
import java.util.Scanner;

public class ConsoleStreamReader implements Runnable {

    private InputStream is;

    private Console console;

    public ConsoleStreamReader(InputStream is, Console console) {
        super();
        this.is = is;
        this.console = console;
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