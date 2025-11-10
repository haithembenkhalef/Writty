package org.example;

import java.util.ArrayList;
import java.util.List;

public class ContentManager {

    List<StringBuilder> buffer = new ArrayList<>();

    void handleChar(int cursorX, int cursorY, char c) {
        StringBuilder sb = buffer.get(cursorX);
        sb.append(c);
    }

    void deleteChar(int cursorX, int cursorY) {
        StringBuilder sb = buffer.get(cursorX);
        sb.deleteCharAt(cursorY);
    }

    void deleteRow(int cursorX) {
        buffer.remove(cursorX);
    }

    public List<StringBuilder> getBuffer() {
        return buffer;
    }

    public void handleNewLine(int cursorX) {
        buffer.add(cursorX, new StringBuilder());
    }

    public int getYPos(int cursorX) {
        return buffer.get(cursorX).length();
    }

    public int getRowCount() {
        return buffer.size();
    }
}
