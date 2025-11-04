package org.example;

public class CursorControlsUtil {

    public static void moveCursorUpBy(int nLines) {
        System.out.printf("\033[%dA", nLines);
    }

    public static void moveCursorDownBy(int nLines) {
        System.out.printf("\033[%dB", nLines);
    }

    public static void moveCursorRightBy(int nLines) {
        System.out.printf("\033[%dC", nLines);
    }

    public static void moveCursorLeftBy(int nLines) {
        System.out.printf("\033[%dD", nLines);
    }
}
