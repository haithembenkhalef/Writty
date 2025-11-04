package org.example.clib;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface CLib extends Library {

    int SYSTEM_OUT_FD = 0;
    int ISIG = 1, ICANON = 2, ECHO = 10, TCSAFLUSH = 2,
            IXON = 2000, ICRNL = 400, IEXTEN = 100000, OPOST = 1, VMIN = 6, VTIME = 5, TIOCGWINSZ = 0x5413;


    CLib INSTANCE = Native.load("c", CLib.class); // load libc

    void printf(String format, Object... args);

    int tcgetattr(int fd, Termios termios);

    int tcsetattr(int fd, int optional_actions,
                  Termios termios);

    int ioctl(int fd, int cmd, WinSize winsize);

}