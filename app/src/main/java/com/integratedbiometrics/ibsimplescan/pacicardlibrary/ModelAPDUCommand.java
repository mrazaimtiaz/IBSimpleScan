package com.integratedbiometrics.ibsimplescan.pacicardlibrary;


public class ModelAPDUCommand {
    byte cla;
    byte ins;
    byte P1;
    byte P2;
    byte[] CommandData;
    byte Le;
    boolean HaveLe;

    ModelAPDUCommand(byte var1, byte var2, byte var3, byte var4) {
        this.HaveLe = false;
        this.CommandData = null;
        this.cla = var1;
        this.ins = var2;
        this.P1 = var3;
        this.P2 = var4;
    }

    public ModelAPDUCommand(byte var1, byte var2, byte var3, byte var4, byte[] var5) {
        this.HaveLe = false;
        this.CommandData = var5;
        this.cla = var1;
        this.ins = var2;
        this.P1 = var3;
        this.P2 = var4;
    }

    ModelAPDUCommand(byte var1, byte var2, byte var3, byte var4, byte[] var5, byte var6) {
        this.HaveLe = true;
        this.CommandData = var5;
        this.Le = var6;
        this.cla = var1;
        this.ins = var2;
        this.P1 = var3;
        this.P2 = var4;
    }

    byte[] ToArray() throws PaciException {
        byte[] var1 = new byte[]{this.cla, this.ins, this.P1, this.P2};
        byte[] var2;
        if (this.CommandData != null) {
            if (this.CommandData.length > 251) {
                throw new PaciException("Wrong APDU detected");
            }

            var2 = new byte[var1.length + 1 + this.CommandData.length];
            var2[4] = (byte)(this.CommandData.length + (this.HaveLe ? 1 : 0));
            System.arraycopy(var1, 0, var2, 0, var1.length);
            System.arraycopy(this.CommandData, 0, var2, 5, this.CommandData.length);
            var1 = var2;
        }

        if (this.HaveLe) {
            var2 = new byte[var1.length + 1];
            System.arraycopy(var1, 0, var2, 0, var1.length);
            var2[var2.length - 1] = this.Le;
            var1 = var2;
        }

        return var1;
    }
}
