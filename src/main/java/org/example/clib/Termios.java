package org.example.clib;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class Termios extends Structure {
    public int c_iflag, c_oflag, c_cflag, c_lflag;

    public byte[] c_cc = new byte[19];

    public Termios() {
    }

    public static Termios of(Termios t) {
        Termios copy = new Termios();
        copy.c_iflag = t.c_iflag;
        copy.c_oflag = t.c_oflag;
        copy.c_cflag = t.c_cflag;
        copy.c_lflag = t.c_lflag;
        copy.c_cc = t.c_cc.clone();
        return copy;
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("c_iflag", "c_oflag", "c_cflag", "c_lflag", "c_cc");
    }
}