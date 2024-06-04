package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import android.util.Log;

import com.telpo.tps550.api.reader.SmartCardReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;


public abstract class PaciCardReaderAbstract implements DisposableInterface {
    SchemaContentAbstract schema;
    byte[] AID;
    byte[] GemAID;
    String AOID;
    ConcurrentHashMap<String, PACICardCommunicatorInterface> Cards = new ConcurrentHashMap();
    ConcurrentHashMap<String, ConcurrentHashMap<String, byte[]>> CachedFiles = new ConcurrentHashMap();
    ConcurrentHashMap<String, Integer> IsCached = new ConcurrentHashMap();
    String CetificateMatchString;
    boolean GetResponseIsAutomaticallyRecalled;
    boolean Disposed = false;
    ConcurrentHashMap<String, String> SelectedFile = new ConcurrentHashMap();
    ConcurrentHashMap<String, Integer> AIDSelected = new ConcurrentHashMap();
    ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> FileSizes = new ConcurrentHashMap();
    Class<?> CurrentImplementationType;
    String CardEFCertificatePath;
    ConcurrentHashMap<String, String> CertLocation = new ConcurrentHashMap();
    ArrayList<String> AllAOID;
    ArrayList<X509Certificate> CachedCertificates;
    String cachedSerialNumber;
    SmartCardReader reader;

    public PaciCardReaderAbstract(boolean var1, SmartCardReader mReader) {
        this.GetResponseIsAutomaticallyRecalled = var1;
 //       this.CurrentImplementationType = var2;
        this.CardEFCertificatePath = null;
        reader = mReader;
        this.AllAOID = new ArrayList();
        this.CachedCertificates = new ArrayList();
        this.cachedSerialNumber = null;
    }

    public void SetReaderCache(String var1, boolean var2) {
        if (!this.IsCached.containsKey(var1)) {
            this.IsCached.put(var1, var2 ? 1 : 0);
        } else {
            this.IsCached.put(var1, var2 ? 1 : 0);
        }

    }

    void SelectAIDAndFile(String var1, String var2) throws PaciException {
        if (!this.AIDSelected.containsKey(var1) || (Integer)this.AIDSelected.get(var1) == 0) {
            this.SelectAID(var1, true);
        }

        String var3 = (String)this.SelectedFile.get(var1);
        if (var3 == null || !var3.equalsIgnoreCase(var2)) {
            if (var2.startsWith("3f00")) {
                this.SelectFile(var1, var2);
            } else if (var3 != null && var2.startsWith(var3)) {
                this.SelectFile(var1, var2.substring(var3.length()));
            } else {
                this.SelectAID(var1, true);
                this.SelectFile(var1, var2);
            }
        }

    }

    public void DisconnectCard(String var1) {
        try {
            ((PACICardCommunicatorInterface)this.Cards.get(var1)).Disconnect();
        } catch (PaciException var3) {
        }

        this.FileSizes.remove(var1);
        this.SelectedFile.remove(var1);
        this.AIDSelected.remove(var1);
        this.IsCached.remove(var1);
        this.CachedFiles.remove(var1);
        this.CertLocation.remove(var1);
        this.CachedCertificates.clear();
        this.cachedSerialNumber = null;
    }

    byte[] GetDataFromCache(String var1, String var2, String var3, int var4) throws PaciException {
        ConcurrentHashMap var5 = (ConcurrentHashMap)this.CachedFiles.get(var1);
        if (var5.containsKey(var2)) {
            byte[] var6 = (byte[])var5.get(var2);
            ModelDataLocation var7 = this.ExtractLocationOf(var3);
            var7.Length = var4;
            byte[] var8 = new byte[var7.Length];
            System.arraycopy(var6, var7.Offset, var8, 0, var8.length);
            return var8;
        } else {
            throw new PaciException("Data was not found in cache");
        }
    }

    byte[] GetDataFromCache(String var1, String var2, String var3) throws PaciException {
        ConcurrentHashMap var4 = (ConcurrentHashMap)this.CachedFiles.get(var1);
        if (var4.containsKey(var2)) {
            byte[] var5 = (byte[])var4.get(var2);
            if (var3 == null) {
                return var5;
            } else {
                ModelDataLocation var6 = this.ExtractLocationOf(var3);
                byte[] var7 = new byte[var6.Length];
                System.arraycopy(var5, var6.Offset, var7, 0, var7.length);
                return var7;
            }
        } else {
            throw new PaciException("Data was not found in cache");
        }
    }

    String GetStringFromCache(String var1, String var2, String var3) throws UnsupportedEncodingException, PaciException {
        return (new String(this.GetDataFromCache(var1, var2, var3), "UTF-8")).trim();
    }

    public boolean IsHandlingReader(String var1) {
        return this.Cards.containsKey(var1);
    }

    void SelectAID(String var1, boolean var2) throws PaciException {
        PACICardCommunicatorInterface var3 = (PACICardCommunicatorInterface)this.Cards.get(var1);
      /*  if (!var3.IsConnected()) {
            var3.Connect();
        }

        if (!var3.IsConnected()) {
            throw new PaciException("Requested card was not found");
        } else {*/
            if (var2) {
                reader.getATRString();
             //   var3.GetATR();
            }
            ModelAPDUResponse var4 = new ModelAPDUResponse(reader.transmit(this.AID));
            Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var4.ResponseData));

           // ModelAPDUResponse var4 = var3.SendAPDU(this.AID);
            if (var4.SW1 != 97 && (var4.SW1 != -112 || var4.SW2 != 0)) {
                var3.EndTransaction(0);
                throw new PaciException("Card internal application could not be selected");
            } else {
                if (this.AIDSelected.containsKey(var1)) {
                    this.AIDSelected.put(var1, 1);
                } else {
                    this.AIDSelected.put(var1, 1);
                }

            }
      //  }
    }

    ModelAPDUResponse SelectFileSpecific(String var1, String var2) throws PaciException {
        PACICardCommunicatorInterface var3 = (PACICardCommunicatorInterface)this.Cards.get(var1);
        ModelAPDUResponse var4 = null;
      //  if (!var3.IsConnected()) {
        //    var3.Connect();
     //   }

      //  if (!var3.IsConnected()) {
        //    throw new PaciException("Error in connecting the card");
      //  } else {
            ModelAPDUCommand var5 = null;
            byte[] var9;
            if (var2.equalsIgnoreCase("3f00")) {
                var9 = new byte[]{63, 0};
                var5 = new ModelAPDUCommand((byte)0, (byte)-92, (byte)0, (byte)12, var9);
                 var4 = new ModelAPDUResponse(reader.transmit(var5.ToArray()));
                Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var4.ResponseData));

             //   var4 = var3.SendAPDU(var5);
                if (var4.SW1 != 144 || var4.SW2 != 0) {
                    var3.EndTransaction(0);
                    throw new PaciException("Card internal file could not be selected");
                }

                this.setSelectedFile(var1, var2);
            } else if (var2.startsWith("3f00")) {
                String var7 = var2.substring(4);
                byte[] var8 = Utilities.hexToByteArray(var7);
                var5 = new ModelAPDUCommand((byte)0, (byte)-92, (byte)8, (byte)0, var8);
                var4 = new ModelAPDUResponse(reader.transmit(var5.ToArray()));
                Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var4.ResponseData));

              //  var4 = var3.SendAPDU(var5);
                if (var4.SW1 != 97 && (!this.GetResponseIsAutomaticallyRecalled || var4.SW1 != -112)) {
                    var3.EndTransaction(0);
                    throw new PaciException("Card internal file could not be selected");
                }

                this.setSelectedFile(var1, var2);
            } else {
                var9 = Utilities.hexToByteArray(var2);
                var5 = new ModelAPDUCommand((byte)0, (byte)-92, (byte)4, (byte)0, var9);
                var4 = new ModelAPDUResponse(reader.transmit(var5.ToArray()));
                Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var4.ResponseData));

               // var4 = var3.SendAPDU(var5);
                if (var4.SW1 != 97 && (!this.GetResponseIsAutomaticallyRecalled || var4.SW1 != -112)) {
                    var3.EndTransaction(0);
                    throw new PaciException("Card internal file could not be selected");
                }

                this.setSelectedFile(var1, var2);
            }

            if (var4.SW1 == 97 || this.GetResponseIsAutomaticallyRecalled && var4.SW1 == -112) {
                var5 = new ModelAPDUCommand((byte)0, (byte)-64, (byte)0, (byte)0, (byte[])null, var4.SW2);


                ModelAPDUResponse var10 = this.GetResponseIsAutomaticallyRecalled && var4.SW1 == -112 ? new ModelAPDUResponse(var4.ResponseData) : new ModelAPDUResponse(reader.transmit(var5.ToArray()));
                Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var10.ResponseData));

                if (var10.ResponseData[2] == -127) {
                    int var11 = var10.ResponseData[4] * 256 + var10.ResponseData[5];
                    if (this.FileSizes.containsKey(var1)) {
                        if (((ConcurrentHashMap)this.FileSizes.get(var1)).containsKey(var2)) {
                            ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var2, var11);
                        } else {
                            ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var2, var11);
                        }
                    } else {
                        this.FileSizes.put(var1, new ConcurrentHashMap());
                        ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var2, var11);
                    }
                }
            }

            return var4;
       // }
    }

    ModelAPDUResponse SelectFile(String var1, String var2) throws PaciException {
        return this.SelectFileSpecific(var1, var2);
    }

    ModelAPDUResponse SelectPKCSFile(String var1, String var2) throws PaciException {
        PACICardCommunicatorInterface var3 = (PACICardCommunicatorInterface)this.Cards.get(var1);
        ModelAPDUResponse var4 = null;
//        if (!var3.IsConnected()) {
  //          var3.Connect();
    //    }

       // if (!var3.IsConnected()) {
     //       throw new PaciException("Requested card was not found");
     //   } else {
            ModelAPDUCommand var5 = null;
            byte[] var9;
            if (var2.equalsIgnoreCase("3f00")) {
                var9 = new byte[]{63, 0};
                var5 = new ModelAPDUCommand((byte)0, (byte)-92, (byte)0, (byte)12, var9);
                 var4 = new ModelAPDUResponse(reader.transmit(var5.ToArray()));
                Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var4.ResponseData));

                //  var4 = var3.SendAPDU(var5);
                if (var4.SW1 != 144 || var4.SW2 != 0) {
                    var3.EndTransaction(0);
                    throw new PaciException("Card internal file could not be selected");
                }

                this.setSelectedFile(var1, var2);
            } else if (var2.startsWith("3f00")) {
                String var7 = var2.substring(4);
                byte[] var8 = Utilities.hexToByteArray(var7);
                var5 = new ModelAPDUCommand((byte)0, (byte)-92, (byte)8, (byte)0, var8);
                var4 = new ModelAPDUResponse(reader.transmit(var5.ToArray()));
                Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var4.ResponseData));

//                var4 = var3.SendAPDU(var5);
                if (var4.SW1 != 97 && (!this.GetResponseIsAutomaticallyRecalled || var4.SW1 != -112)) {
                    var3.EndTransaction(0);
                    throw new PaciException("Card internal file could not be selected");
                }

                this.setSelectedFile(var1, var2);
            } else {
                var9 = Utilities.hexToByteArray(var2);
                var5 = new ModelAPDUCommand((byte)0, (byte)-92, (byte)4, (byte)0, var9);
                var4 = new ModelAPDUResponse(reader.transmit(var5.ToArray()));
                Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var4.ResponseData));

                //var4 = var3.SendAPDU(var5);
                if (var4.SW1 != 97 && (!this.GetResponseIsAutomaticallyRecalled || var4.SW1 != -112)) {
                    var3.EndTransaction(0);
                    throw new PaciException("Card internal file could not be selected");
                }

                this.setSelectedFile(var1, var2);
            }

            if (var4.SW1 == 97 || this.GetResponseIsAutomaticallyRecalled && var4.SW1 == -112) {
                var5 = new ModelAPDUCommand((byte)0, (byte)-64, (byte)0, (byte)0, (byte[])null, var4.SW2);


                ModelAPDUResponse var10 = this.GetResponseIsAutomaticallyRecalled && var4.SW1 == -112 ? new ModelAPDUResponse(var4.ResponseData) : new ModelAPDUResponse(reader.transmit(var5.ToArray()));
                Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var10.ResponseData));

                if (var10.ResponseData[2] == -127) {
                    int var11 = var10.ResponseData[4] * 256 + var10.ResponseData[5];
                    if (this.FileSizes.containsKey(var1)) {
                        if (((ConcurrentHashMap)this.FileSizes.get(var1)).containsKey(var2)) {
                            ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var2, var11);
                        } else {
                            ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var2, var11);
                        }
                    } else {
                        this.FileSizes.put(var1, new ConcurrentHashMap());
                        ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var2, var11);
                    }
                }
            }

            return var4;
      //  }
    }

    public void setSelectedFile(String var1, String var2) {
        this.SelectedFile.put(var1, var2);
    }

    protected void finalize() throws Throwable {
        this.Dispose();
        super.finalize();
    }

    ModelDataLocation ExtractLocationOf(String var1) throws PaciException {
        ModelDataLocation var2 = new ModelDataLocation();
        ConcurrentHashMap var3 = this.schema.ReadSpecificData(var1);
        Log.d("TAG", "ExtractLocationOf: $-" + var1);
        Log.d("TAG", "ExtractLocationOf: $s" + this.schema);
        Log.d("TAG", "ExtractLocationOf: $" + var3);
        var2.EfPath = (String)var3.get("FileName");
        var2.Length = (Integer)var3.get("Length");
        var2.Offset = (Integer)var3.get("Offset");
        return var2;
    }

    abstract byte[] ReadSpecificObject(String var1, ModelDataLocation var2) throws PaciException;

    byte[] ReadBinary(String var1, int var2, int var3) throws PaciException {
        if (var2 > 65535) {
            throw new PaciException("Offset will overflow");
        } else if (var3 >= 256) {
            throw new PaciException("File size is over the limit");
     //   } else if (!this.Cards.containsKey(var1)) {
       //     throw new PaciException("Requested reader was not found");
        } else {
         //   PACICardCommunicatorInterface var4 = (PACICardCommunicatorInterface)this.Cards.get(var1);
            byte var5 = (byte)(var2 / 256);
            byte var6 = (byte)(var2 % 256);
            byte var7 = (byte)var3;
            boolean var8 = true;
            boolean var9 = false;
            ModelAPDUCommand var10 = new ModelAPDUCommand((byte)0, (byte)-80, var5, var6, (byte[])null, var7);

            while(var8) {
                ModelAPDUResponse var11 = new ModelAPDUResponse(reader.transmit(var10.ToArray()));
                Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var11.ResponseData));

                // ModelAPDUResponse var11 = var4.SendAPDU(var10);
                switch(var11.SW1) {
                    case -112:
                        return var11.ResponseData;
                    case 98:
                        if (var11.SW2 == -126) {
                            return var11.ResponseData;
                        }

                        if (var11.SW2 == -127) {
                            return var11.ResponseData;
                        }
                    case 103:
                        throw new PaciException("There is a problem with the response bytes");
                    case 105:
                        if (var11.SW2 == -127) {
                            throw new PaciException("There is a problem with the response bytes");
                        }

                        if (var11.SW2 == -126) {
                            throw new PaciException("There is a problem with the response bytes");
                        }

                        if (var11.SW2 == -122) {
                            throw new PaciException("There is a problem with the response bytes");
                        }

                        throw new PaciException("There is a problem with the response bytes");
                    case 106:
                        if (var11.SW2 == -127) {
                            throw new PaciException("There is a problem with the response bytes");
                        }

                        if (var11.SW2 == -126) {
                            throw new PaciException("There is a problem with the response bytes");
                        }

                        throw new PaciException("There is a problem with the response bytes");
                    case 107:
                        if (var11.SW2 == 0) {
                            throw new PaciException("There is a problem with the response bytes");
                        }

                        throw new PaciException("There is a problem with the response bytes");
                    case 108:
                        if (var9 || var11.SW2 == 0) {
                            throw new PaciException("There is a problem with the response bytes");
                        }

                        var10.Le = var11.SW2;
                        var9 = true;
                        break;
                    default:
                        throw new PaciException("There is a problem with the response bytes");
                }
            }

            throw new PaciException("Unknown file was requested");
        }
    }

    public SchemaContentAbstract getSchema() {
        return this.schema;
    }

    public void setSchema(SchemaContentAbstract var1) {
        this.schema = var1;
    }

    public byte[] GetBinaryData(String var1, String var2, int var3) throws PaciException {
        ModelDataLocation var4 = this.ExtractLocationOf(var2);
        var4.Length = var3;
        Integer var5 = (Integer)this.IsCached.get(var1);
        if (var5 != null && var5 == 1 && this.CachedFiles.containsKey(var1) && this.CachedFiles.get(var1) != null) {
            try {
                return this.GetDataFromCache(var1, var4.EfPath, var2, var3);
            } catch (Exception var8) {
                this.FillCache(var1, var4.EfPath);
                return this.GetDataFromCache(var1, var4.EfPath, var2, var3);
            }
        } else if (var5 != null && var5 == 1) {
            this.FillCache(var1, var4.EfPath);
            return this.GetDataFromCache(var1, var4.EfPath, var2);
        } else {
            PACICardCommunicatorInterface var6 = (PACICardCommunicatorInterface)this.Cards.get(var1);
            if (!var6.IsConnected()) {
                var6.Connect();
            }

            if (!var6.IsConnected()) {
                throw new PaciException("Requested card was not found");
            } else {
                var6.BeginTransaction();
                byte[] var7 = this.ReadSpecificObject(var1, var4);
                this.SelectedFile.remove(var1);
                this.AIDSelected.remove(var1);
                var6.EndTransaction(0);
                if (var7 == null) {
                    throw new PaciException("There is no data in the requested location");
                } else {
                    return var7;
                }
            }
        }
    }

    public byte[] GetBinaryData(String var1, String var2) throws PaciException {
        try {
            ModelDataLocation var3 = this.ExtractLocationOf(var2);
            Integer var4 = (Integer)this.IsCached.get(var1);
            if (var4 != null && var4 == 1 && this.CachedFiles.containsKey(var1) && this.CachedFiles.get(var1) != null) {
                try {
                    return this.GetDataFromCache(var1, var3.EfPath, var2);
                } catch (Exception var7) {
                    this.FillCache(var1, var3.EfPath);
                    return this.GetDataFromCache(var1, var3.EfPath, var2);
                }
            } else if (var4 != null && var4 == 1) {
                this.FillCache(var1, var3.EfPath);
                return this.GetDataFromCache(var1, var3.EfPath, var2);
            } else {
                PACICardCommunicatorInterface var5 = (PACICardCommunicatorInterface)this.Cards.get(var1);
                if (!var5.IsConnected()) {
                    var5.Connect();
                }

                if (!var5.IsConnected()) {
                    throw new PaciException("Requested card was not found");
                } else {
                    var5.BeginTransaction();
                    byte[] var6 = this.ReadSpecificObject(var1, var3);
                    this.SelectedFile.remove(var1);
                    this.AIDSelected.remove(var1);
                    var5.EndTransaction(0);
                    if (var6 == null) {
                        throw new PaciException("There is no data in the requested location");
                    } else {
                        return var6;
                    }
                }
            }
        } catch (PaciException var8) {
            throw var8;
        } catch (Exception var9) {
            throw new PaciException("There is no data in the requested location");
        }
    }

    public String GetData(String var1, String var2) throws PaciException {
        try {
            ModelDataLocation var3 = this.ExtractLocationOf(var2);
            Log.d("TAG", "GetData: " + var3.EfPath + " " + var3);
            Integer var4 = (Integer)this.IsCached.get(var1);
            if (var4 != null && var4 == 1 && this.CachedFiles.containsKey(var1) && this.CachedFiles.get(var1) != null) {
                try {
                    return this.GetStringFromCache(var1, var3.EfPath, var2);
                } catch (PaciException var7) {
                    this.FillCache(var1, var3.EfPath);
                    return this.GetStringFromCache(var1, var3.EfPath, var2);
                }
            } else if (var4 != null && var4 == 1) {
                this.FillCache(var1, var3.EfPath);
                return this.GetStringFromCache(var1, var3.EfPath, var2);
            } else {
                PACICardCommunicatorInterface var5 = (PACICardCommunicatorInterface)this.Cards.get(var1);
              /*  if (!var5.IsConnected()) {
                    var5.Connect();
                }

                if (!var5.IsConnected()) {
                    throw new PaciException("Requested card was not found");
                } else {*/
                 //   var5.BeginTransaction();
                    byte[] var6 = this.ReadSpecificObject(var1, var3);
                    this.SelectedFile.remove(var1);
                    this.AIDSelected.remove(var1);
                   // var5.EndTransaction(0);
                    if (var6 == null) {
                        throw new PaciException("There is no data in the requested location");
                    } else {
                        return (new String(var6, "UTF-8")).trim();
                    }
              //  }
            }
        } catch (UnsupportedEncodingException var8) {
            throw new PaciException("The requested encoding is not supported");
        }
    }

    public String GetSerialNumber(String var1, boolean var2) throws PaciException {
        if (var2 && this.cachedSerialNumber != null) {
            return this.cachedSerialNumber;
        } else {
          /*  PACICardCommunicatorInterface var3 = (PACICardCommunicatorInterface)this.Cards.get(var1);
            if (!var3.IsConnected()) {
                var3.Connect();
            }

            if (!var3.IsConnected()) {
                throw new PaciException("Requested card was not found");
            } else {*/
                byte[] var4 = new byte[]{0, -92, 4, 0};
                byte[] var5 = new byte[]{0, -54, 1, 1, 13};

                String var8;
            //    try {
                   // var3.BeginTransaction();
                    new ModelAPDUResponse(reader.transmit(var4));
                   // var3.SendAPDU(var4);
                    ModelAPDUResponse var6 = new ModelAPDUResponse(reader.transmit(var5));
            Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var6.ResponseData));

            //  ModelAPDUResponse var6 = var3.SendAPDU(var5);
                  //  var3.EndTransaction(0);
                    String var7 = Utilities.bytesToHex(var6.ResponseData, 5);
                    var7 = var7.trim();
                    if (var2) {
                        this.cachedSerialNumber = var7;
                    }

                    var8 = var7;
               // } catch (PaciException var12) {
                //    throw var12;
                //} finally {
                 //  var3.EndTransaction(0);
             //   }

                return var8;
           // }
        }
    }

    public abstract PACICardProperties GetCardProperties(String var1) throws PaciException;

    public abstract int PINAttemptsStatus(String var1) throws PaciException;

    int GetFileSize(String var1, String var2) {
        try {
            return (Integer)((ConcurrentHashMap)this.FileSizes.get(var1)).get(var2);
        } catch (Exception var4) {
            return 0;
        }
    }

    public boolean FillCache(String var1, String var2) throws PaciException {
     /*   PACICardCommunicatorInterface var3 = (PACICardCommunicatorInterface)this.Cards.get(var1);
        if (!var3.IsConnected()) {
            var3.Connect();
        }

        if (!var3.IsConnected()) {
            throw new PaciException("Requested card was not found");
        } else {*/
         //   var3.BeginTransaction();
            this.SelectAID(var1, false);
            this.SelectFile(var1, var2);
            int var4 = this.GetFileSize(var1, var2);
            ModelDataLocation var5 = new ModelDataLocation();
            var5.EfPath = (String)this.SelectedFile.get(var1);
            var5.Length = var4;
            var5.Offset = 0;
            byte[] var6 = this.ReadSpecificObject(var1, var5);

         /*   try {
              //  var3.EndTransaction(0);
            } catch (PaciException var8) {
            }*/

            if (!this.CachedFiles.containsKey(var1)) {
                this.CachedFiles.put(var1, new ConcurrentHashMap());
            }

            if (!((ConcurrentHashMap)this.CachedFiles.get(var1)).containsKey(this.SelectedFile)) {
                ((ConcurrentHashMap)this.CachedFiles.get(var1)).put(var2, var6);
            }

            return true;
      //  }
    }

    public boolean RemoveReader(String var1) {
        ((DisposableInterface)this.Cards.get(var1)).Dispose();
        this.Cards.remove(var1);
        return true;
    }

    public void ClearReaders() {
        this.Cards.clear();
    }

    public boolean AddReader(String var1) {
        return true;
  /*      try {
            this.Cards.put(var1, (PACICardCommunicatorInterface)this.CurrentImplementationType.getConstructor(String.class).newInstance(var1));

        } catch (IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException var3) {
            return false;
        }*/
    }

    public byte[] ReadCardCertificate(String var1, ModelDataLocation var2) throws PaciException {
        try {
            PACICardCommunicatorInterface var3 = (PACICardCommunicatorInterface)this.Cards.get(var1);
            var3.BeginTransaction();
            ModelAPDUResponse var12 = new ModelAPDUResponse(reader.transmit(this.GemAID));
            Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var12.ResponseData));

          //  ModelAPDUResponse var12 = var3.SendAPDU(this.GemAID);
            if (!var12.ResponseOK()) {
                throw new PaciException("Card internal application could not be selected");
            } else {
                ModelAPDUResponse var5 = this.SelectCertFile(var1, var2.EfPath);
                if (!var5.ResponseOK()) {
                    return null;
                } else {
                    if (var2.Length == 0) {
                        var2.Length = (Integer)((ConcurrentHashMap)this.FileSizes.get(var1)).get(var2.EfPath);
                    }

                    byte[] var6 = new byte[var2.Length];
                    int var7 = 252;

                    for(int var8 = 0; var8 < var2.Length; var8 += var7) {
                        if (var7 + var8 > var2.Length) {
                            var7 = var2.Length - var8;
                        }

                        byte[] var9 = this.ReadBinary(var1, var8 + var2.Offset, var7);
                        if (var9.length == 0) {
                            break;
                        }

                        System.arraycopy(var9, 0, var6, var8, var7);
                    }

                    var3.EndTransaction(0);
                    if (this.CertLocation.containsKey(var1)) {
                        this.CertLocation.put(var1, var2.EfPath);
                    } else {
                        this.CertLocation.put(var1, var2.EfPath);
                    }

                    if (!this.CachedFiles.containsKey(var1)) {
                        this.CachedFiles.put(var1, new ConcurrentHashMap());
                    }

                    if (!((ConcurrentHashMap)this.CachedFiles.get(var1)).containsKey(var2.EfPath)) {
                        ((ConcurrentHashMap)this.CachedFiles.get(var1)).put(var2.EfPath, var6);
                    } else {
                        ((ConcurrentHashMap)this.CachedFiles.get(var1)).put(var2.EfPath, var6);
                    }

                    return var6;
                }
            }
        } catch (PaciException var11) {
            try {
                PACICardCommunicatorInterface var4 = (PACICardCommunicatorInterface)this.Cards.get(var1);
                var4.EndTransaction(0);
            } catch (PaciException var10) {
            }

            return null;
        }
    }

    ModelDataLocation[] GetCardCertificateLocationAndOffset(String var1, String var2) throws PaciException, UnsupportedEncodingException {
        ArrayList var3 = new ArrayList();
        this.SelectPKCSFile(var1, var2);
        int var4 = (Integer)((ConcurrentHashMap)this.FileSizes.get(var1)).get(var2);
        byte[] var5 = new byte[var4];
        int var7;
        if (var4 < 252) {
            var5 = this.ReadBinary(var1, 0, var4);
        } else {
            for(int var6 = 0; var6 < var4; var6 += var7) {
                var7 = 252;
                if (var6 + 252 > var4) {
                    var7 = var4 % 252;
                }

                byte[] var8 = this.ReadBinary(var1, 0, var7);
                System.arraycopy(var8, 0, var5, var6, var8.length);
            }
        }

        if (var5.length > 0 && var5.length <= 10240) {
            ArrayList var25 = new ArrayList();

            int var26;
            for(var7 = 0; var7 < var5.length; var7 += var26) {
                var26 = (var5[var7 + 1] & 255) + 2;
                if (var26 <= 2) {
                    break;
                }

                if (var26 - 2 >= 128) {
                    int var9 = var26 - 128 - 2;
                    var26 = 0;

                    for(int var10 = var9; var10 > 0; --var10) {
                        var26 += (var5[var7 + 1 + var10] & 255) << (var9 - var10) * 8;
                    }

                    var26 += 2 + var9;
                }

                byte[] var27 = new byte[var26];
                if (var26 + var7 > var5.length) {
                    break;
                }

                System.arraycopy(var5, var7, var27, 0, var26);
                var25.add(var27);
            }

            boolean var29 = false;
            boolean var28 = false;
            Object var30 = null;

            for(int var11 = 0; var11 < var25.size(); ++var11) {
                int var12 = 4;
                int var13;
                if ((((byte[])var25.get(var11))[1] & 255) > 128) {
                    var13 = ((byte[])var25.get(var11))[3 + (((byte[])var25.get(var11))[1] & 255) - 128] & 255;
                    var12 = 4 + (((byte[])var25.get(var11))[1] & 255) - 128 + (var13 > 128 ? var13 - 128 : 0);
                }

                if ((((byte[])var25.get(var11))[var12] & 255) == 12) {
                    var13 = 1;
                    int var14 = ((byte[])var25.get(var11))[var12 + var13] & 255;
                    int var16;
                    if (var14 > 128) {
                        var13 = var14 - 128;
                        var14 = 0;

                        for(int var15 = var13; var15 > 0; --var15) {
                            var16 = ((byte[])var25.get(var11))[3 + (((byte[])var25.get(var11))[1] & 255) - 128] & 255;
                            var12 = 4 + (((byte[])var25.get(var11))[1] & 255) - 128 + (var16 > 128 ? var16 - 128 : 0);
                        }
                    }

                    byte[] var31 = new byte[var14];
                    System.arraycopy(var25.get(var11), var12 + var13 + 1, var31, 0, var14);
                    new String(var31, "UTF8");
                    var26 = var11;
                    var28 = true;
                    var16 = 3;
                    byte[] var17 = null;
                    if (var28) {
                        if ((((byte[])var25.get(var11))[var16 - 2] & 255) >= 128) {
                            var16 += (((byte[])var25.get(var11))[var16 - 2] & 255) - 128;
                        }

                        int var18 = ((byte[])var25.get(var11))[var16] & 255;
                        if (var18 >= 128) {
                            int var19 = var18 - 128;
                            var18 = 0;

                            for(int var20 = 0; var20 < var19; ++var20) {
                                ++var16;
                                var18 += (((byte[])var25.get(var26))[var16] & 255) * 256 * var20;
                            }
                        }

                        byte var33;
                        try {
                            var16 += (((byte[])var25.get(var26))[var16] & 255) + 2;
                            var16 += (((byte[])var25.get(var26))[var16] & 255) + 2;
                            var33 = ((byte[])var25.get(var26))[var16 - 1];
                        } catch (ArrayIndexOutOfBoundsException | NullPointerException var24) {
                            continue;
                        }

                        if (var33 == -95 && (((byte[])var25.get(var26))[var16] & 255) < 128 && (((byte[])var25.get(var26))[var16 + 1] & 255) == 48) {
                            var17 = new byte[((byte[])var25.get(var26))[var16 + 2] & 255];
                            System.arraycopy(var25.get(var26), var16 + 3, var17, 0, var17.length);
                        }
                    }

                    if (var17 != null) {
                        byte var32 = 3;
                        byte[] var34 = new byte[var17[var32] & 255];
                        System.arraycopy(var17, var32 + 1, var34, 0, var34.length);
                        var16 = var32 + (var17[var32] & 255) + 2;
                        byte[] var35;
                        if (var16 >= var17.length) {
                            var35 = new byte[]{0};
                        } else {
                            var35 = new byte[var17[var16] & 255];
                            System.arraycopy(var17, var16 + 1, var35, 0, var35.length);
                        }

                        byte[] var36;
                        if (var16 >= var17.length) {
                            var36 = new byte[]{0};
                        } else {
                            var16 += (var17[var16] & 255) + 2;
                            var36 = new byte[var17[var16] & 255];
                            System.arraycopy(var17, var16 + 1, var36, 0, var36.length);
                        }

                        if (var34[0] != 63 && var34[1] != 0) {
                            byte[] var22;
                            if (var34.length == 2) {
                                byte[] var21 = new byte[2];
                                System.arraycopy(var34, 0, var21, 0, 2);
                                var34 = new byte[6];
                                System.arraycopy(var21, 0, var34, 4, 2);
                                var22 = new byte[]{63, 0, 81, 0};
                                System.arraycopy(var22, 0, var34, 0, 4);
                            } else {
                                int var37 = var34.length;
                                var22 = new byte[2];
                                System.arraycopy(var34, 0, var22, 0, 2);
                                var34 = new byte[var37 + 2];
                                System.arraycopy(var22, 0, var34, var37 - 2, 2);
                                byte[] var23 = new byte[]{63, 0};
                                System.arraycopy(var23, 0, var34, 0, 2);
                            }
                        }

                        ModelDataLocation var38 = new ModelDataLocation();
                        var38.EfPath = Utilities.bytesToHex(var34);
                        var38.Offset = (int)Utilities.bytesToLong(var35);
                        var38.Length = (int)Utilities.bytesToLong(var36);
                        var3.add(var38);
                    }
                }
            }

            return (ModelDataLocation[])var3.toArray(new ModelDataLocation[var3.size()]);
        } else {
            return null;
        }
    }

    public abstract ModelDataLocation[] GetAllCardCertificateLocationAndOffset(String var1) throws PaciException, UnsupportedEncodingException;

    public X509Certificate[] GetAllCertificate(String var1, boolean var2) throws PaciException {
        if (var2 && this.CachedCertificates != null && this.CachedCertificates.size() > 0) {
            return (X509Certificate[])this.CachedCertificates.toArray(new X509Certificate[this.CachedCertificates.size()]);
        } else {
            PACICardCommunicatorInterface var3 = (PACICardCommunicatorInterface)this.Cards.get(var1);
            if (!var3.IsConnected()) {
                var3.Connect();
            }

            if (!var3.IsConnected()) {
                throw new PaciException("Requested card was not found");
            } else {
                var3.BeginTransaction();
                ModelAPDUResponse var4 = new ModelAPDUResponse(reader.transmit(this.GemAID));
                Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var4.ResponseData));

                //ModelAPDUResponse var4 = var3.SendAPDU(this.GemAID);
                if (var4.SW1 != 97 && (var4.SW1 != -112 || var4.SW2 != 0)) {
                    var3.EndTransaction(0);
                    throw new PaciException("Card internal application could not be selected");
                } else {
                    ArrayList var5 = new ArrayList();
                    ModelDataLocation[] var6 = null;

                    try {
                        var6 = this.GetAllCardCertificateLocationAndOffset(var1);
                    } catch (UnsupportedEncodingException var28) {
                        var3.EndTransaction(0);
                        return null;
                    }

                    var3.EndTransaction(0);
                    CertificateFactory var7 = null;

                    try {
                        var7 = CertificateFactory.getInstance("X509");
                    } catch (CertificateException var27) {
                        return null;
                    }

                    ModelDataLocation[] var8 = var6;
                    int var9 = var6.length;

                    for(int var10 = 0; var10 < var9; ++var10) {
                        ModelDataLocation var11 = var8[var10];
                        byte[] var12 = this.ReadCardCertificate(var1, var11);
                        if (var12 != null) {
                            ByteArrayInputStream var13 = new ByteArrayInputStream(var12);

                            try {
                                X509Certificate var14 = (X509Certificate)var7.generateCertificate(var13);
                                var5.add(var14);
                            } catch (CertificateException var25) {
                            } finally {
                                try {
                                    var13.close();
                                } catch (IOException var24) {
                                }

                            }
                        }
                    }

                    if (var2) {
                        if (this.CachedCertificates == null) {
                            this.CachedCertificates = new ArrayList();
                        }

                        this.CachedCertificates.clear();
                        this.CachedCertificates = var5;
                    }

                    return (X509Certificate[])var5.toArray(new X509Certificate[var5.size()]);
                }
            }
        }
    }

    public X509Certificate FindDigitalSignatureCertificate(String var1) {
        X509Certificate[] var2;
        try {
            var2 = this.GetAllCertificate(var1, true);
        } catch (PaciException | NullPointerException var8) {
            return null;
        }

        X509Certificate[] var3 = var2;
        int var4 = var2.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            X509Certificate var6 = var3[var5];
            boolean[] var7 = var6.getKeyUsage();
            if (var7 != null && var7[1]) {
                return var6;
            }
        }

        return null;
    }

    public X509Certificate FindCardCertificate(String var1) {
        X509Certificate[] var2;
        try {
            if (var1 == null) {
            }

            var2 = this.GetAllCertificate(var1, true);
        } catch (PaciException | NullPointerException var8) {
            return null;
        }

        X509Certificate[] var3 = var2;
        int var4 = var2.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            X509Certificate var6 = var3[var5];
            boolean[] var7 = var6.getKeyUsage();
            if (var7 != null && var7[0] && !var7[1]) {
                return var6;
            }
        }

        return null;
    }

    private ModelAPDUResponse SelectCertFile(String var1, String var2) throws PaciException {
        PACICardCommunicatorInterface var3 = (PACICardCommunicatorInterface)this.Cards.get(var1);
        ModelAPDUResponse var4 = null;
        if (!var3.IsConnected()) {
            var3.Connect();
        }

        if (!var3.IsConnected()) {
            throw new PaciException("Requested card was not found");
        } else {
            ModelAPDUCommand var5 = null;
            String var7 = var2.substring(4);
            byte[] var8 = Utilities.hexToByteArray(var7);
            var5 = new ModelAPDUCommand((byte)0, (byte)-92, (byte)8, (byte)0, var8);
             var4 = new ModelAPDUResponse(reader.transmit(var5.ToArray()));
            Log.d("TAG", "SelectCertFile: bytetoarray " + Arrays.toString(var4.ResponseData));
            //var4 = var3.SendAPDU(var5);
            if (var4.SW1 == 97 || this.GetResponseIsAutomaticallyRecalled && var4.SW1 == -112) {
                this.setSelectedFile(var1, var2);
                if (var4.SW1 == 97 || this.GetResponseIsAutomaticallyRecalled && var4.SW1 == -112) {
                    var5 = new ModelAPDUCommand((byte)0, (byte)-64, (byte)0, (byte)0, (byte[])null, var4.SW2);

                    ModelAPDUResponse var9 = this.GetResponseIsAutomaticallyRecalled && var4.SW1 == -112 ? new ModelAPDUResponse(var4.ResponseData) :  new ModelAPDUResponse(reader.transmit(var5.ToArray()));
                    Log.d("TAG", "SelectCertFile:  bytetoarray " + Arrays.toString(var9.ResponseData));
                    if (var9.ResponseData[2] == -127) {
                        int var10 = var9.ResponseData[4] * 256 + var9.ResponseData[5];
                        if (this.FileSizes.containsKey(var1)) {
                            if (((ConcurrentHashMap)this.FileSizes.get(var1)).containsKey(var2)) {
                                ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var2, var10);
                            } else {
                                ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var2, var10);
                            }
                        } else {
                            this.FileSizes.put(var1, new ConcurrentHashMap());
                            ((ConcurrentHashMap)this.FileSizes.get(var1)).put(var2, var10);
                        }
                    }
                }

                return var4;
            } else {
                var3.EndTransaction(0);
                throw new PaciException("Card internal file could not be selected");
            }
        }
    }
}
