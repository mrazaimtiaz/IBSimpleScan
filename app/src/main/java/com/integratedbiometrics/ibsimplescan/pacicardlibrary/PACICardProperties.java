package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


public class PACICardProperties {
    private int _myAvailablePinAttempts = 3;
    private boolean _myCardLocked = false;
    private int _myCardEdition = 3;
    private int _myKeySize = 2048;
    private boolean _myDigitalSignatureAvailable = false;

    public PACICardProperties() {
    }

    public int AvailablePinAttempts() {
        return this._myAvailablePinAttempts;
    }

    public boolean IsCardLocked() {
        return this._myCardLocked;
    }

    public int CardEdition() {
        return this._myCardEdition;
    }

    public int KeySize() {
        return this._myKeySize;
    }

    public boolean IsDigitalSignatureAvailable() {
        return this._myDigitalSignatureAvailable;
    }

    public void setAvailablePinAttempts(int var1) {
        this._myAvailablePinAttempts = var1;
    }

    public void setCardLocked(boolean var1) {
        this._myCardLocked = var1;
    }

    public void setCardEdition(int var1) {
        this._myCardEdition = var1;
    }

    public void setKeySize(int var1) {
        this._myKeySize = var1;
    }

    public void setDigitalSignatureAvailable(boolean var1) {
        this._myDigitalSignatureAvailable = var1;
    }

    public String toJSONString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("{\"").append(this.getClass().getName()).append("\":{").append("\"AvailablePinAttempts\":").append(this._myAvailablePinAttempts).append(",").append("\"CardLocked\":").append(this._myCardLocked).append(",").append("\"CardEdition\":").append(this._myCardEdition).append(",").append("\"KeySize\":").append(this._myKeySize).append(",").append("\"DigitalSignatureAvailable\":").append(this._myDigitalSignatureAvailable).append("}}");
        return var1.toString();
    }
}

