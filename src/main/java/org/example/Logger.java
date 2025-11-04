package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
    private static final String LOG_FILE = "debug.log";

    public static void log(String msg) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
