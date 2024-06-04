package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.security.cert.PKIXCertPathBuilderResult;

class CertificateVerificationResult {
    private boolean valid;
    private PKIXCertPathBuilderResult result;
    private Throwable exception;

    public CertificateVerificationResult(PKIXCertPathBuilderResult var1) {
        this.valid = true;
        this.result = var1;
    }

    public CertificateVerificationResult(Throwable var1) {
        this.valid = false;
        this.exception = var1;
    }

    public boolean isValid() {
        return this.valid;
    }

    public PKIXCertPathBuilderResult getResult() {
        return this.result;
    }

    public Throwable getException() {
        return this.exception;
    }
}
