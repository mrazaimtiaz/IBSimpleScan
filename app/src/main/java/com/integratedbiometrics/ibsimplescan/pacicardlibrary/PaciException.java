package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

public class PaciException extends Exception {
    int ErrorNumber;

    public PaciException(String var1) {
        super(var1);
        this.ErrorNumber = 0;
    }

    public PaciException(String var1, int var2) {
        super("Error [" + var2 + "]: " + var1);
        this.ErrorNumber = var2;
    }

    public int getError() {
        return this.ErrorNumber;
    }

    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

