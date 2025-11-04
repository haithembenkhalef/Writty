package org.example;

import org.example.clib.CLib;
import org.example.clib.WinSize;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.example.FontConstants.*;
import static org.example.KeyConstants.*;
import static org.example.clib.CLib.TIOCGWINSZ;

public class SimpleTextEditor {

    // === Constants ===
    private static final String CRLF = "\r\n";
    private static final int FOOTER_SIZE = 0;
    private static final int HEADER_SIZE = 0;

    private static final String TITLE = BG_CYAN + FG_WHITE + "Writty Editor V1\033[K" + RESET;

    private static final String FOOTER = BG_LIGHT_BLUE + FG_WHITE + "\tType your text below. Press Ctrl+Q to quit." + RESET;

    // --- New fields for scrolling region ---
    private int scrollTop;
    private int scrollBottom;

    private int scrollLeft;
    private int scrollRight;


    private int cursorX = 0;
    private int cursorY = 0;

    private boolean running = true;

    // === Instance Fields ===
    private final ContentManager contentManager;
    private WinSize windowSize;


    // === Constructor ===
    public SimpleTextEditor(ContentManager contentManager) {
        this.contentManager = contentManager;
    }


    // === Public API ===
    public void start(String[] args) throws IOException {
        try {
            enableRawMode();
            openNewScreen();
            clearViewport();
            windowSize = fetchWindowSize();
            setScrollingRegion();

            eventLoop();
        } catch (Exception e) {
            exitEditor();
            System.out.println(e.getMessage());
        }

    }


    // === Core Loop ===
    private void eventLoop() throws IOException {
        int keyCode;
        while (running && (keyCode = System.in.read()) != -1) {
            if (keyCode == '\033') continue; // skip ESC
            if (keyCode == '[') {
                int nextKey = System.in.read();
                if (nextKey != -1) {
                    switch (nextKey) {
                        case 'A' -> keyCode = CUSTOM_ARROW_UP;
                        case 'B' -> keyCode = CUSTOM_ARROW_DOWN;
                        case 'C' -> keyCode = CUSTOM_ARROW_RIGHT;
                        case 'D' -> keyCode = CUSTOM_ARROW_LEFT;
                        default -> {
                            continue;
                        }
                    }
                }
            }
            boolean fullReDraw = false;
            switch (keyCode) {
                case CTRL_Q -> exitEditor();
                case BACKSPACE -> handleBackspace();
                case RETURN -> handleEnter();
                case CUSTOM_ARROW_UP, CUSTOM_ARROW_DOWN, CUSTOM_ARROW_LEFT, CUSTOM_ARROW_RIGHT ->
                        handleCursorMovement(keyCode);
                default -> handleCharacterInput((char) keyCode);
            }
            drawLine(fullReDraw);
        }
    }

    private void drawLine(boolean fullReDraw) {
        StringBuilder sb = new StringBuilder();
        sb.append("\033[H\033[2J");
        List<StringBuilder> buffer = contentManager.getBuffer();
        buffer.forEach(stringBuilder -> sb.append(stringBuilder).append(CRLF));
        sb.append(String.format("\033[%d;%dH", cursorX + 1, cursorY + 1));
        System.out.print(sb);
    }

    // === Editor Actions ===
    private void handleCursorMovement(int keyCode) {
        switch (keyCode) {
            case CUSTOM_ARROW_UP -> {
                if (cursorX > 0) {
                    cursorX--;
                    cursorY = contentManager.getYPos(cursorX);
                    //CursorControlsUtil.moveCursorUpBy(1);
                }
            }
            case CUSTOM_ARROW_DOWN -> {
                if (cursorX < scrollBottom && cursorX < contentManager.getRowCount() - 1) {
                    cursorX++;
                    cursorY = contentManager.getYPos(cursorX);
                    //CursorControlsUtil.moveCursorDownBy(1);
                }
            }
            case CUSTOM_ARROW_LEFT -> {
                if (cursorY > 0) {
                    cursorY--;
                    //CursorControlsUtil.moveCursorLeftBy(1);
                }
            }
            case CUSTOM_ARROW_RIGHT -> {
                if (cursorY < scrollRight && cursorY < contentManager.getYPos(cursorX)) {
                    cursorY++;
                    //CursorControlsUtil.moveCursorRightBy(1);
                }
            }
        }
    }

    private boolean handleBackspace() {
        if (cursorY > 0) {
            cursorY--;
            contentManager.deleteChar(cursorX, cursorY);
        } else if (cursorY == 0) {
            if (cursorX > 0) {
                contentManager.deleteRow(cursorX);
                cursorX--;
                cursorY = contentManager.getYPos(cursorX);

            }
        }

        return true;
    }

    private void handleEnter() {
        cursorY = 0;
        cursorX++;
        contentManager.handleNewLine(cursorX);
    }

    private void handleCharacterInput(char c) {
        if (contentManager.handleChar(cursorX, cursorY, c)) {
            cursorY++;
        }
    }


    // === Terminal Management ===
    private void setScrollingRegion() {
        scrollTop = HEADER_SIZE + 1;
        scrollBottom = windowSize.ws_row - (FOOTER_SIZE + 1);

        scrollLeft = 1;
        scrollRight = windowSize.ws_col;

        System.out.printf("\033[%d;%dr", scrollTop, scrollBottom);
        System.out.printf("\033[%d;%dc", scrollLeft, scrollRight);
        System.out.flush();
        System.out.print("\033[?6h");
        contentManager.handleNewLine(cursorX);
        resetCursor();
    }

    private void openNewScreen() {
        System.out.print("\033[?1049h");
    }

    private void restoreScreen() {
        System.out.println("\033[?1049l");
        System.out.print("\033[r");
        System.out.print("\033[?6l");
    }

    private void clearViewport() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void resetCursor() {
        System.out.printf("\033[%d;%dH", cursorX + 1, cursorY + 1);
    }

    private void enableRawMode() throws IOException {
        Runtime.getRuntime().exec(new String[]{"sh", "-c", "stty raw -echo < /dev/tty"});
    }

    private void disableRawMode() throws IOException {
        Runtime.getRuntime().exec(new String[]{"sh", "-c", "stty sane < /dev/tty"});
    }


    // === Window and Exit ===
    private WinSize fetchWindowSize() {
        WinSize ws = new WinSize();
        int result = CLib.INSTANCE.ioctl(0, TIOCGWINSZ, ws);
        if (result != 0) throw new RuntimeException("Could not execute ioctl");
        return ws;
    }

    private void exitEditor() throws IOException {
        running = false;
        System.out.print(CRLF + "Exiting editor...");
        restoreScreen();
        disableRawMode();
    }
}
