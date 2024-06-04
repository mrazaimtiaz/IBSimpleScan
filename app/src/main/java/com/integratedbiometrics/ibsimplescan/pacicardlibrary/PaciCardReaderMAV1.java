package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import android.util.Log;

import com.telpo.tps550.api.reader.SmartCardReader;

import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


public class PaciCardReaderMAV1 extends PaciCardReaderAbstract {

    SmartCardReader reader;
    public PaciCardReaderMAV1(boolean var1,SmartCardReader mReader) {
        super(var1,mReader);
        this.AID = DataConstants.MAV1_AID;
        this.reader = mReader;
        super.GemAID = DataConstants.MAV1_GEM_AID;
        this.schema = new SchemaContentMAV1("");
        this.CetificateMatchString = "\\bThe Public Authority for Civil Information ID\\b";
        this.AOID = "3f0050005003";
        this.AllAOID.add(this.AOID);
    }

    public void Dispose() {
        if (!this.Disposed) {
        }

        this.Disposed = true;
    }

    ModelAPDUResponse SelectFile(String var1, String var2) throws PaciException {
        PACICardCommunicatorInterface var3 = (PACICardCommunicatorInterface)this.Cards.get(var1);
        ModelAPDUResponse var4 = null;
        if (!var3.IsConnected()) {
            var3.Connect();
        }

        if (!var3.IsConnected()) {
            throw new PaciException("Requested card was not found");
        } else {
            ModelAPDUCommand var5 = null;
            String var6 = var2;
            byte[] var7;
            ModelAPDUResponse var8;
            if (var2.equalsIgnoreCase("3f00")) {
                var7 = new byte[]{63, 0};
                var5 = new ModelAPDUCommand((byte)0, (byte)-92, (byte)0, (byte)12, var7);
                byte[] var4Temp = reader.transmit(var5.ToArray());
                var4 = new ModelAPDUResponse(var4Temp);
              //  var4 = var3.SendAPDU(var5);
                if (var4.SW1 != 144 || var4.SW2 != 0) {
                    var3.EndTransaction(0);
                    throw new PaciException("Card internal file could not be selected");
                }

                this.setSelectedFile(var1, var2);
            } else {
                if (var2.startsWith("3f00")) {
                    var7 = new byte[]{63, 0};
                    var5 = new ModelAPDUCommand((byte)0, (byte)-92, (byte)0, (byte)12, var7);

                    byte[] var4Temp = reader.transmit(var5.ToArray());
                    var4 = new ModelAPDUResponse(var4Temp);
                   // var4 = var3.SendAPDU(var5);
                    if (var4.SW1 != -112 || var4.SW2 != 0) {
                        throw new PaciException("Card internal file could not be selected");
                    }
                }

                var7 = Utilities.hexToByteArray(var2.substring(var2.length() - 4));
                var8 = null;

                for(int var9 = 4; var9 < var6.length() - 4; var9 += 4) {
                    byte[] var11 = Utilities.hexToByteArray(var6.substring(var9, var9 + 4));
                    var5 = new ModelAPDUCommand((byte)0, (byte)-92, (byte)1, (byte)12, var11);

                    byte[] var4Temp = reader.transmit(var5.ToArray());
                    var4 = new ModelAPDUResponse(var4Temp);

                   // var4 = var3.SendAPDU(var5);
                    if (!var4.ResponseOK()) {
                        throw new PaciException("Card internal file could not be selected");
                    }
                }

                var5 = new ModelAPDUCommand((byte)0, (byte)-92, (byte)2, (byte)0, var7);
              //  var4 = var3.SendAPDU(var5);

                byte[] var4Temp = reader.transmit(var5.ToArray());
                var4 = new ModelAPDUResponse(var4Temp);

                if (var4.SW1 != 97 && (var4.SW1 != -112 || var4.SW2 != 0)) {
                    throw new PaciException("Card internal file could not be selected");
                }

                this.setSelectedFile(var1, (String)this.SelectedFile.get(var1) + var6);
            }

            short var10 = 0;
            if (var4.SW1 != 97 && (!this.GetResponseIsAutomaticallyRecalled || var4.SW1 != -112)) {
                if (var6.substring(var6.length() - 4).equals("0201")) {
                    var10 = 9;
                } else if (var6.substring(var6.length() - 4).equals("0202")) {
                    var10 = 611;
                } else if (var6.substring(var6.length() - 4).equals("0203")) {
                    var10 = 346;
                } else if (var6.substring(var6.length() - 4).equals("0204")) {
                    var10 = 4138;
                }

                if (var10 != 0) {
                    if (this.FileSizes.containsKey(var1)) {
                        ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var6, Integer.valueOf(var10));
                    } else {
                        this.FileSizes.put(var1, new ConcurrentHashMap());
                        ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var6, Integer.valueOf(var10));
                    }
                }
            } else {
                var5 = new ModelAPDUCommand((byte)0, (byte)-64, (byte)0, (byte)0, (byte[])null, var4.SW2);

              //  var8 = this.GetResponseIsAutomaticallyRecalled && var4.SW1 == -112 ? new ModelAPDUResponse(var4.ResponseData) : var3.SendAPDU(var5);
               var8 = this.GetResponseIsAutomaticallyRecalled && var4.SW1 == -112 ? new ModelAPDUResponse(var4.ResponseData) : new ModelAPDUResponse(reader.transmit(var5.ToArray()));

                if (var8.ResponseData[0] == -123) {
                    int var12 = var8.ResponseData[8] * 256 + var8.ResponseData[9];
                    if (this.FileSizes.containsKey(var1)) {
                        if (((ConcurrentHashMap)this.FileSizes.get(var1)).containsKey(var6)) {
                            ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var6, var12);
                        } else {
                            ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var6, var12);
                        }
                    } else {
                        this.FileSizes.put(var1, new ConcurrentHashMap());
                        ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var6, var12);
                    }
                }
            }

            return var4;
        }
    }

    byte[] ReadSpecificObject(String var1, ModelDataLocation var2) throws PaciException {
        PACICardCommunicatorInterface var3 = (PACICardCommunicatorInterface)this.Cards.get(var1);
        if (!var3.IsConnected()) {
            var3.Connect();
        }

        this.SelectAIDAndFile(var1, var2.EfPath);
        int var4 = var2.Offset / 4;
        int var5 = var2.Offset % 4;
        int var6 = var2.Length;
        byte[] var7;
        if (var6 + var5 < 252) {
            var7 = this.ReadBinary(var1, var4, var6 + var5);
            byte[] var12 = new byte[var6];
            System.arraycopy(var7, var5, var12, 0, var6);
            return var12;
        } else {
            var7 = new byte[var6];
            int var8 = var6 + var5;
            byte[] var9 = new byte[var8];

            int var10;
            byte[] var11;
            for(var10 = 0; var10 < var8; var10 += 252) {
                if (var10 + 252 >= var8) {
                    var11 = this.ReadBinary(var1, (var4 + var10) / 4, var8 - var10);
                    System.arraycopy(var11, 0, var9, var10, var11.length);
                    var10 += var11.length;
                    break;
                }

                System.arraycopy(this.ReadBinary(var1, (var4 + var10) / 4, 252), 0, var9, var10, 252);
            }

            System.arraycopy(var9, var5, var7, 0, var6);
            if (var10 == var8) {
                return var7;
            } else {
                var11 = new byte[var10];
                System.arraycopy(var7, 0, var11, 0, var11.length);
                return var11;
            }
        }
    }

    public PACICardProperties GetCardProperties(String var1) throws PaciException {
        PACICardProperties var2 = new PACICardProperties();
        var2.setCardEdition(1);
        X509Certificate var3 = this.FindCardCertificate(var1);
        if (var3 != null) {
            RSAPublicKey var4 = (RSAPublicKey)var3.getPublicKey();
            var2.setKeySize(var4.getModulus().bitLength());
        } else {
            var2.setKeySize(0);
        }

        int var6 = this.PINAttemptsStatus(var1);
        if (var6 <= 0) {
            var2.setCardLocked(true);
            var2.setAvailablePinAttempts(0);
        } else {
            var2.setCardLocked(false);
            var2.setAvailablePinAttempts(var6);
        }

        X509Certificate var5 = this.FindDigitalSignatureCertificate(var1);
        if (var5 != null) {
            var2.setDigitalSignatureAvailable(true);
        }

        return var2;
    }

    public int PINAttemptsStatus(String var1) throws PaciException {
        try {
        //    PACICardCommunicatorInterface var2 = (PACICardCommunicatorInterface)this.Cards.get(var1);
//            if (!var2.IsConnected()) {
 //               var2.Connect();
   //         }

       //     if (!var2.IsConnected()) {
      //          throw new PaciException("Requested card was not found");
      //      } else {
            //    var2.BeginTransaction();

                byte[] var4Temp = reader.transmit(DataConstants.MAV1_GEM_AID);
                ModelAPDUResponse var3 = new ModelAPDUResponse(var4Temp);
              //  ModelAPDUResponse var3 = var2.SendAPDU(DataConstants.MAV1_GEM_AID);
            Log.d("TAG", "PINAttemptsStatus: response " + var3.SW1 + "  " + var3.SW2);
                if (!var3.ResponseOK()) {
                //    var2.EndTransaction(0);
                    throw new PaciException("Card internal application could not be selected");
                } else {
                    byte[] var4 = new byte[]{0, 32, 0, -127, 0};

                    byte[] var3Temp = reader.transmit(var4);
                     var3 = new ModelAPDUResponse(var3Temp);
                  //  var3 = var2.SendAPDU(var4);
                    if (var3.SW1 != 99) {
                   //     var2.EndTransaction(0);
                        throw new PaciException("Card internal file could not be selected");
                    } else {
                  //      var2.EndTransaction(0);
                        return var3.SW2 & 15;
                    }
                }
           // }
        } catch (PaciException var5) {
            throw new PaciException("Card internal file could not be selected");
        }
    }

    public ModelDataLocation[] GetAllCardCertificateLocationAndOffset(String var1) throws PaciException, UnsupportedEncodingException {
        ArrayList var2 = new ArrayList();
        Iterator var3 = this.AllAOID.iterator();

        while(var3.hasNext()) {
            String var4 = (String)var3.next();

            try {
                ModelDataLocation[] var5 = this.GetCardCertificateLocationAndOffset(var1, var4);
                if (var5 != null) {
                    HashSet var6 = new HashSet(Arrays.asList(var5));
                    var2.addAll(var6);
                }
            } catch (PaciException var7) {
            }
        }

        return (ModelDataLocation[])var2.toArray(new ModelDataLocation[var2.size()]);
    }
}
