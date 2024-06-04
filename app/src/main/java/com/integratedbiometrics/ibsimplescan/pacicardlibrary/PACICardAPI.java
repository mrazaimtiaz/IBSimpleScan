package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import android.content.Context;

import com.telpo.tps550.api.reader.SmartCardReader;

import java.io.InputStream;
import java.net.Proxy;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public final class PACICardAPI extends CardAndReaderEventsAbstract {
    PaciCardReaderAbstract MAV1CardReaders;
    PaciCardReaderAbstract MAV3CardReaders;
    String[] ReadersNames;
    ConcurrentHashMap<String, String> ConnectedCardATRs;
    CardAndReaderEventsAbstract EventsHandler;
    boolean AlwaysCached;
    ArrayList<PaciEventHandler> MyEvent;
    ConcurrentHashMap<String, PaciCardReaderAbstract> ReaderHandler;
    X509Certificate SigningRoot;
    X509Certificate PolicyRoot;
    X509Certificate KuwaitRoot;
    List CertList;
    CertPathBuilder PathBuilder;
    CertStore store;
    KeyStore keyStore;

    Context mContext;

    private String DataTypeToString(DataType var1) {
        switch(var1) {
            case CivilID:
                return "CIVIL-NO";
            case ArabicTitle:
                return "A-TITLE";
            case FirstArabicName:
                return "ARABIC-NAME-1";
            case SecondArabicName:
                return "ARABIC-NAME-2";
            case ThirdArabicName:
                return "ARABIC-NAME-3";
            case FamilyArabicName:
                return "ARABIC-NAME-4";
            case FullArabicName:
                return "ArabicName";
            case FirstEnglishName:
                return "LATIN-NAME-1";
            case SecondEnglishName:
                return "LATIN-NAME-2";
            case ThirdEnglishName:
                return "LATIN-NAME-3";
            case FamilyEnglishName:
                return "LATIN-NAME-4";
            case FullEnglishName:
                return "EnglishName";
            case NationalityCodeLatin:
                return "NATIONALITY-LATIN-ALPHA-CODE";
            case NationalityArabicText:
                return "NATIONALITY-ARABIC-TEXT";
            case DateOfBirth:
                return "BIRTH-DATE";
            case CardIssueDate:
                return "CARD-ISSUE-DATE";
            case CardExpiryDate:
                return "CARD-EXPIRY-DATE";
            case CardSerialNumber:
                return "CARD-SERIAL-NO";
            case CardDocumentNumber:
                return "DOCUMENT-NO";
            case MOIReference:
                return "MOI-REFERENCE";
            case MOIReferenceIndic:
                return "MOI-REFERENCE-INDIC";
            case AdditionalFields1:
                return "ADDITIONAL-F-1";
            case AdditionalFields2:
                return "ADDITIONAL-F-2";
            case BlockNumber:
                return "BLOCK-NO";
            case EnglishSexCode:
                return "SEX-LATIN-TEXT";
            case ArabicSexText:
                return "SEX-ARABIC-TEXT";
            case StreetName:
                return "STREET-NAME";
            case District:
                return "DESTRICT";
            case UnitType:
                return "UNIT-TYPE";
            case UnitNo:
                return "UNIT-NO";
            case BuildingPlotNumber:
                return "BUILDING-PLOT-NO";
            case FloorNo:
                return "FLOOR-NO";
            case AddressUniqueCode:
                return "ADDRESS-UNIQUE-KEY";
            case BloodType:
                return "BLOOD-TYPE";
            case GurdianCivilID:
                return "GUARDIAN-CIVIL-ID-NO";
            case Telephone1:
                return "TEL-1";
            case Telephone2:
                return "TEL-2";
            case EmailAddress:
                return "E-MAIL-ADDRESS";
            default:
                return null;
        }
    }

    protected void finalize() throws Throwable {
        this.EventsHandler.RemoveHandler(this);
        this.EventsHandler = null;
        super.finalize();
    }

    public PACICardAPI(Context context,SmartCardReader reader) throws PaciException {
        this.Initialize(true,context,reader);
    }

    public PACICardAPI(boolean var1,Context context,SmartCardReader reader) throws PaciException {
        this.Initialize(var1,context,reader);
    }

    void Initialize(boolean var1, Context context, SmartCardReader reader) throws PaciException {
        System.setProperty("sun.security.smartcardio.t0GetResponse", "true");
        System.setProperty("sun.security.smartcardio.t1GetResponse", "true");
        this.MyEvent = new ArrayList();
        this.MAV1CardReaders = new PaciCardReaderMAV1("true".equals(System.getProperty("sun.security.smartcardio.t0GetResponse", "true")),reader);
        this.MAV3CardReaders = new PaciCardReaderMAV3("true".equals(System.getProperty("sun.security.smartcardio.t0GetResponse", "true")), reader);
        this.EventsHandler = new CardAndReaderEventsSmartCardIO(context);
        this.ConnectedCardATRs = new ConcurrentHashMap();
        this.EventsHandler.AddEventHandler(this);
        this.AlwaysCached = var1;
        this.ReaderHandler = new ConcurrentHashMap();
        CRLVerifier.crlDistCached = new ConcurrentHashMap();
        CRLVerifier.crlDistTimeCached = new ConcurrentHashMap();


    }

    public String getATR(int var1) throws PaciException {
        return (String)this.ConnectedCardATRs.get(this.ReadersNames[var1]);
    }

    void CardDisconnectionEvent(int var1) {
        try {
            this.ConnectedCardATRs.remove(this.ReadersNames[var1]);

            for(int var2 = 0; var2 < this.MyEvent.size(); ++var2) {
                PaciEventHandler var3 = (PaciEventHandler)this.MyEvent.get(var2);
                if (var3 != null) {
                    var3.CardDisconnectionEvent(var1);
                }
            }

            if (this.MAV3CardReaders.IsHandlingReader(this.ReadersNames[var1])) {
                this.MAV3CardReaders.DisconnectCard(this.ReadersNames[var1]);
                this.MAV3CardReaders.setSelectedFile(this.ReadersNames[var1], (String)null);
            } else {
                this.MAV1CardReaders.DisconnectCard(this.ReadersNames[var1]);
                this.MAV1CardReaders.setSelectedFile(this.ReadersNames[var1], (String)null);
            }
        } catch (Exception var4) {
        }

    }

    void CardConnectionEvent(int var1, byte[] var2) {
        String var3 = Utilities.bytesToHex(var2);
        if (!this.ConnectedCardATRs.containsKey(this.ReadersNames[var1])) {
            this.ConnectedCardATRs.put(this.ReadersNames[var1], var3);
        }

        String var4;
        Iterator var5;
        PaciEventHandler var6;
        if (var3.matches("3B6A00008065A2014{1}(2|5|7|8){1}013D72D643")) {
            var4 = this.ReadersNames[var1];
            if (!this.MAV1CardReaders.IsHandlingReader(var4)) {
                if (this.MAV3CardReaders.IsHandlingReader(var4)) {
                    this.MAV3CardReaders.RemoveReader(var4);
                }

                this.MAV1CardReaders.AddReader(var4);
                this.ReaderHandler.put(var4, this.MAV1CardReaders);
            }

            var5 = this.MyEvent.iterator();

            while(var5.hasNext()) {
                var6 = (PaciEventHandler)var5.next();
                if (var6 != null) {
                    var6.CardConnectionEvent(var1);
                }
            }
        } else if (var3.matches("3B7F960000803180655{1}(2|5|7|8){1}850300EF124140829000")) {
            var4 = this.ReadersNames[var1];
            if (!this.MAV3CardReaders.IsHandlingReader(var4)) {
                if (this.MAV1CardReaders.IsHandlingReader(var4)) {
                    this.MAV1CardReaders.RemoveReader(var4);
                }

                this.MAV3CardReaders.AddReader(var4);
                this.ReaderHandler.put(var4, this.MAV3CardReaders);
            }

            var5 = this.MyEvent.iterator();

            while(var5.hasNext()) {
                var6 = (PaciEventHandler)var5.next();
                if (var6 != null) {
                    var6.CardConnectionEvent(var1);
                }
            }
        }

    }

    void ReaderChangeEvent(String[] var1) {
        this.ReadersNames = var1;
        this.MAV1CardReaders.ClearReaders();
        this.MAV3CardReaders.ClearReaders();
        this.ReaderHandler.clear();

        for(int var2 = 0; var2 < var1.length; ++var2) {
            this.MAV1CardReaders.AddReader(this.ReadersNames[var2]);
            this.ReaderHandler.put(this.ReadersNames[var2], this.MAV1CardReaders);
        }

        Iterator var4 = this.MyEvent.iterator();

        while(var4.hasNext()) {
            PaciEventHandler var3 = (PaciEventHandler)var4.next();
            if (var3 != null) {
                var3.ReaderChangeEvent();
            }
        }

    }

    public String[] GetReaders() {
        return this.ReadersNames;
    }

    public int GetNumberOfReaders() {
        return this.ReadersNames.length;
    }

    /** @deprecated */
    public void SetPaciEventHandler(PaciEventHandler var1) {
        this.AddEventListener(var1);
    }

    public void AddEventListener(PaciEventHandler var1) {
        this.MyEvent.add(var1);
    }

    public void RemoveEventListener(PaciEventHandler var1) throws PaciException {
        try {
            this.MyEvent.remove(var1);
        } catch (Exception var3) {
            throw new PaciException("Could not remove listener");
        }
    }

    public String getNationalty_Latin_Text(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "NATIONALITY-LATIN-ALPHA-CODE");
        }
    }

    public String getApplication_Version(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "APPLICATION-VERSION");
        }
    }

    public String getCivil_ID(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "CIVIL-NO");
        }
    }

    public String getA_TITLE(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "A-TITLE");
        }
    }

    public String getArabicName_1(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "ARABIC-NAME-1");
        }
    }

    public String getArabicName_2(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "ARABIC-NAME-2");
        }
    }

    public String getArabicName_3(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "ARABIC-NAME-3");
        }
    }

    public String getArabicName_4(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "ARABIC-NAME-4");
        }
    }

    public String getEnglishName_1(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "LATIN-NAME-1");
        }
    }

    public String getEnglishName_2(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            String var4 = var3.GetData(this.ReadersNames[var1], "LATIN-NAME-4");
            if (this.containsDigit(var4)) {
                var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
                var3.SetReaderCache(this.ReadersNames[var1], var2);
                String var5 = var3.GetData(this.ReadersNames[var1], "LATIN-NAME-2");
                String[] var6 = var5.split("[^\\w']");
                return var6.length > 0 ? var6[0] : "";
            } else {
                var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
                var3.SetReaderCache(this.ReadersNames[var1], var2);
                return var3.GetData(this.ReadersNames[var1], "LATIN-NAME-2");
            }
        }
    }

    public String getEnglishName_3(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            String var4 = var3.GetData(this.ReadersNames[var1], "LATIN-NAME-4");
            if (this.containsDigit(var4)) {
                var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
                var3.SetReaderCache(this.ReadersNames[var1], var2);
                String var5 = var3.GetData(this.ReadersNames[var1], "LATIN-NAME-2");
                String[] var6 = var5.split("[^\\w']");
                return var6.length > 1 ? var6[1] : "";
            } else {
                var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
                var3.SetReaderCache(this.ReadersNames[var1], var2);
                return var3.GetData(this.ReadersNames[var1], "LATIN-NAME-3");
            }
        }
    }

    public String getEnglishName_4(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            String var4 = var3.GetData(this.ReadersNames[var1], "LATIN-NAME-4");
            if (this.containsDigit(var4)) {
                var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
                var3.SetReaderCache(this.ReadersNames[var1], var2);
                return var3.GetData(this.ReadersNames[var1], "LATIN-NAME-3");
            } else {
                return var4;
            }
        }
    }

    public String getPassport(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            String var4 = var3.GetData(this.ReadersNames[var1], "LATIN-NAME-4");
            return this.containsDigit(var4) ? var4 : "";
        }
    }

    private final boolean containsDigit(String var1) {
        boolean var2 = false;
        if (var1 != null && !var1.isEmpty()) {
            char[] var3 = var1.toCharArray();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                char var6 = var3[var5];
                if (var2 = Character.isDigit(var6)) {
                    break;
                }
            }
        }

        return var2;
    }

    public String getArabic_Name(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "ARABIC-NAME-1") + " " + var3.GetData(this.ReadersNames[var1], "ARABIC-NAME-2") + " " + var3.GetData(this.ReadersNames[var1], "ARABIC-NAME-3") + " " + var3.GetData(this.ReadersNames[var1], "ARABIC-NAME-4");
        }
    }

    public String getEnglish_Name(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            StringBuilder var4 = new StringBuilder();
            var4.append(this.getEnglishName_1(var1, var2)).append(" ");
            var4.append(this.getEnglishName_2(var1, var2)).append(" ");
            var4.append(this.getEnglishName_3(var1, var2)).append(" ");
            var4.append(this.getEnglishName_4(var1, var2));
            return var4.toString();
        }
    }

    public String getSex_Arabic_Text(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "SEX-ARABIC-TEXT");
        }
    }

    public String getSex_Latin_Text(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "SEX-LATIN-TEXT");
        }
    }

    public String getNationalty_Arabic_Text(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "NATIONALITY-ARABIC-TEXT");
        }
    }

    public String getBirth_Date(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "BIRTH-DATE");
        }
    }

    public String getCard_Issue_Date(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "CARD-ISSUE-DATE");
        }
    }

    public String getCard_Expiry_Date(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "CARD-EXPIRY-DATE");
        }
    }

    public String getDocument_No(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "DOCUMENT-NO");
        }
    }

    public String getCard_Serial_No(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetSerialNumber(this.ReadersNames[var1], var2);
        }
    }

    public String getMOI_Reference(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "MOI-REFERENCE");
        }
    }

    public String getMOI_Refernce_Indic(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "MOI-REFERENCE-INDIC");
        }
    }

    public String getAddress_Unique_Key(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "ADDRESS-UNIQUE-KEY");
        }
    }

    public String getDestrict(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "DESTRICT");
        }
    }

    public String getBlock_No(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "BLOCK-NO");
        }
    }

    public String getStreet_Name(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "STREET-NAME");
        }
    }

    public String getBuilding_Plot_No(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "BUILDING-PLOT-NO");
        }
    }

    public String getUnit_type(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "UNIT-TYPE");
        }
    }

    public String getUnit_No(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "UNIT-NO");
        }
    }

    public String getFloor_No(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "FLOOR-NO");
        }
    }

    public String getBlood_Type(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "BLOOD-TYPE");
        }
    }

    public String getGuardian_Civil_ID_No(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "GUARDIAN-CIVIL-ID-NO");
        }
    }

    public String getTel_1(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "TEL-1");
        }
    }

    public String getTel_2(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "TEL-2");
        }
    }

    public String getEmail_Address(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "E-MAIL-ADDRESS");
        }
    }

    public String getAdditional_F1(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "ADDITIONAL-F-1");
        }
    }

    public String getAdditional_F2(int var1, boolean var2) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            var3.SetReaderCache(this.ReadersNames[var1], var2);
            return var3.GetData(this.ReadersNames[var1], "ADDITIONAL-F-2");
        }
    }

    public String ReadCardInfo(int var1, DataType var2, boolean var3) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            String var4 = this.ReadersNames[var1];
            PaciCardReaderAbstract var5 = (PaciCardReaderAbstract)this.ReaderHandler.get(var4);
            var5.SetReaderCache(var4, var3);
            if (var2 == null) {
                throw new PaciException("Unknown data was requested");
            } else {
                String var6 = this.DataTypeToString(var2);
                if (var6 == null) {
                    throw new PaciException("Unknown data was requested");
                } else {
                    switch(var2) {
                        case FullArabicName:
                            return var5.GetData(var4, "ARABIC-NAME-1") + " " + var5.GetData(var4, "ARABIC-NAME-2") + " " + var5.GetData(var4, "ARABIC-NAME-3") + " " + var5.GetData(var4, "ARABIC-NAME-4");
                        case FullEnglishName:
                            StringBuilder var7 = new StringBuilder();
                            var7.append(this.getEnglishName_1(var1, var3)).append(" ");
                            var7.append(this.getEnglishName_2(var1, var3)).append(" ");
                            var7.append(this.getEnglishName_3(var1, var3)).append(" ");
                            var7.append(this.getEnglishName_4(var1, var3));
                            return var7.toString();
                        case CardSerialNumber:
                            return var5.GetSerialNumber(var4, var3);
                        default:
                            return var5.GetData(var4, var6);
                    }
                }
            }
        }
    }

    public byte[] ReadPhoto(int var1) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Wrong Reader Index", 136);
        } else {
            String var2 = this.ReadersNames[var1];
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(var2);
            var3.SetReaderCache(var2, true);
            byte[] var4 = var3.GetBinaryData(var2, "PHOTO-LENGTH");
            int var5 = 0;
            byte[] var6 = var4;
            int var7 = var4.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                byte var9 = var6[var8];
                var5 <<= 8;
                if (var9 >= 0) {
                    var5 += var9;
                } else {
                    var5 += 256 + var9;
                }
            }

            return var3.GetBinaryData(var2, "PHOTO", var5);
        }
    }

    public X509Certificate GetCardCertificate(int var1) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            String var2 = this.ReadersNames[var1];
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(var2);
            X509Certificate var4 = var3.FindCardCertificate(var2);
            return var4;
        }
    }

    public X509Certificate GetDigitalSignatureCertificate(int var1) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            String var2 = this.ReadersNames[var1];
            PaciCardReaderAbstract var3 = (PaciCardReaderAbstract)this.ReaderHandler.get(var2);
            X509Certificate var4 = var3.FindDigitalSignatureCertificate(var2);
            return var4;
        }
    }

    private X509Certificate GetCertificateFromResource(String var1) throws PaciException {
        CertificateFactory var2;
        try {
            var2 = CertificateFactory.getInstance("X509");
        } catch (CertificateException var6) {
            throw new PaciException("Error in generating the certificate");
        }

        InputStream var3 = this.getClass().getClassLoader().getResourceAsStream(var1);

        try {
            X509Certificate var4 = (X509Certificate)var2.generateCertificate(var3);
            return var4;
        } catch (CertificateException var5) {
            throw new PaciException("Error in generating the certificate");
        }
    }

    public PACICardProperties GetCardProperties(int var1) throws PaciException {
        if (var1 > this.ReadersNames.length - 1) {
            throw new PaciException("Reader index is out of range");
        } else {
            PaciCardReaderAbstract var2 = (PaciCardReaderAbstract)this.ReaderHandler.get(this.ReadersNames[var1]);
            PACICardProperties var3 = var2.GetCardProperties(this.ReadersNames[var1]);
            return var3;
        }
    }

    public void DownloadCRL(Proxy var1) {
        try {
            CRLVerifier.downloadCRLFromWebsite("http://crl.paci.gov.kw/GovernmentofKuwaitPACICIVILID/LatestCRL.crl", var1);
        } catch (Exception var3) {
        }

    }

    public boolean ValidateCardCertificateWithOCSP(int var1, Proxy var2) {
        try {
            X509Certificate var3 = this.GetCardCertificate(var1);
            X509Certificate var4 = this.GetCertificateFromResource("RootCert/PACIIDIssuanceCA.cer");
            return CertificateVerifierOCSP.ValidateCertificateWithOCSP(var3, var4, var2);
        } catch (PaciException var5) {
            return false;
        }
    }

    public boolean ValidateDigitalSignatureCertificateWithOCSP(int var1, Proxy var2) {
        try {
            X509Certificate var3 = this.GetDigitalSignatureCertificate(var1);
            X509Certificate var4 = this.GetCertificateFromResource("RootCert/PACIIDIssuanceCA.cer");
            return CertificateVerifierOCSP.ValidateCertificateWithOCSP(var3, var4, var2);
        } catch (PaciException var5) {
            return false;
        }
    }

    public boolean ValidateCardCertificateWithCRL(int var1, boolean var2, String var3, Proxy var4) throws PaciException {
        X509Certificate var5 = this.GetCardCertificate(var1);
        X509Certificate var6 = this.GetCertificateFromResource("RootCert/PACIIDIssuanceCA.cer");

        try {
            if (!var5.getIssuerDN().equals(var6.getSubjectDN())) {
                return false;
            } else {
                var5.checkValidity();
                var5.verify(var6.getPublicKey());
                if (var2) {
                    CRLVerifier.verifyCertificateCRLs(var5, var4);
                } else {
                    CRLVerifier.verifyCertificateFromLocalCRL(var5, var3);
                }

                return true;
            }
        } catch (NullPointerException | CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException | CertificateVerificationException var8) {
            return false;
        }
    }

    public boolean ValidateDigitalSignatureCertificateWithCRL(int var1, boolean var2, String var3, Proxy var4) throws PaciException {
        X509Certificate var5 = this.GetDigitalSignatureCertificate(var1);
        X509Certificate var6 = this.GetCertificateFromResource("RootCert/PACIIDIssuanceCA.cer");

        try {
            if (!var5.getIssuerDN().equals(var6.getSubjectDN())) {
                return false;
            } else {
                var5.checkValidity();
                var5.verify(var6.getPublicKey());
                if (var2) {
                    CRLVerifier.verifyCertificateCRLs(var5, var4);
                } else {
                    CRLVerifier.verifyCertificateFromLocalCRL(var5, var3);
                }

                return true;
            }
        } catch (NullPointerException | CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException | CertificateVerificationException var8) {
            return false;
        }
    }

    public static enum DataType {
        CivilID,
        ArabicTitle,
        FirstArabicName,
        SecondArabicName,
        ThirdArabicName,
        FamilyArabicName,
        FullArabicName,
        FirstEnglishName,
        SecondEnglishName,
        ThirdEnglishName,
        FamilyEnglishName,
        FullEnglishName,
        NationalityCodeLatin,
        NationalityArabicText,
        DateOfBirth,
        CardIssueDate,
        CardExpiryDate,
        CardSerialNumber,
        CardDocumentNumber,
        MOIReference,
        MOIReferenceIndic,
        AdditionalFields1,
        AdditionalFields2,
        EnglishSexCode,
        ArabicSexText,
        StreetName,
        BlockNumber,
        District,
        UnitType,
        UnitNo,
        BuildingPlotNumber,
        FloorNo,
        AddressUniqueCode,
        BloodType,
        GurdianCivilID,
        Telephone1,
        Telephone2,
        EmailAddress;

        private DataType() {
        }
    }
}
