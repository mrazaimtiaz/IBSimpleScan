package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

public class CertificateVerificationException extends Exception {
    private static final long serialVersionUID = 1L;

    public CertificateVerificationException(String var1, Throwable var2) {
        super(var1, var2);
    }

    public CertificateVerificationException(String var1) {
        super(var1);
    }
}
