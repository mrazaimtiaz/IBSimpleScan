package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

class ModelAPDUResponse {
    public byte[] ResponseData;
    public byte SW1;
    public byte SW2;

    public ModelAPDUResponse(byte[] var1) {
        this.ResponseData = new byte[var1.length - 2];
        System.arraycopy(var1, 0, this.ResponseData, 0, this.ResponseData.length);
        this.SW1 = var1[var1.length - 2];
        this.SW2 = var1[var1.length - 1];
    }

    public boolean ResponseOK() {
        return this.SW1 == 97 || this.SW1 == -112 && this.SW2 == 0;
    }
}
