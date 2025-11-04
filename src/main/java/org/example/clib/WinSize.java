package org.example.clib;

import com.sun.jna.Structure;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

import java.util.Arrays;
import java.util.List;

public class WinSize extends Structure {
    public short ws_row;    // rows
    public short ws_col;    // columns
    public short ws_xpixel; // optional: pixel width
    public short ws_ypixel; // optional: pixel height

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("ws_row", "ws_col", "ws_xpixel", "ws_ypixel");
    }
}
