package com.core.server.log;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class JhPrintStream extends PrintStream {
    public JhPrintStream(OutputStream out) {
        super(out);
    }

    public void println() {
        this.newLine();
    }

    public void println(Object x) {
        String s = String.valueOf(x);
        synchronized(this) {
            this.print(s);
            this.newLine();
        }
    }

    public void println(long x) {
        synchronized(this) {
            this.print(x);
            this.newLine();
        }
    }

    public void println(double x) {
        synchronized(this) {
            this.print(x);
            this.newLine();
        }
    }

    public void println(int x) {
        synchronized(this) {
            this.print(x);
            this.newLine();
        }
    }

    public void println(float x) {
        synchronized(this) {
            this.print(x);
            this.newLine();
        }
    }

    public void println(char x) {
        synchronized(this) {
            this.print(x);
            this.newLine();
        }
    }

    public void println(boolean x) {
        synchronized(this) {
            this.print(x);
            this.newLine();
        }
    }

    public void println(char[] x) {
        synchronized(this) {
            this.print(x);
            this.newLine();
        }
    }

    public void println(String x) {
        String s = String.valueOf(x);
        synchronized(this) {
            this.print(s);
            this.newLine();
        }
    }

    private void newLine() {
        this.print("<br/>");
    }
}
