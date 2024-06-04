package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extensions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class CRLVerifier {
    public static ConcurrentHashMap<String, String> crlDistTimeCached;
    public static ConcurrentHashMap<String, X509CRL> crlDistCached;

    CRLVerifier() {
    }

    public static void verifyCertificateCRLs(X509Certificate var0, Proxy var1) throws CertificateVerificationException {
        try {
            List var2 = getCrlDistributionPoints(var0);
            Iterator var3 = var2.iterator();

            String var4;
            X509CRL var5;
            do {
                if (!var3.hasNext()) {
                    return;
                }

                var4 = (String)var3.next();
                var5 = downloadCRL(var4, var1);
            } while(!var5.isRevoked(var0));

            throw new CertificateVerificationException("The certificate is revoked by CRL: " + var4);
        } catch (CertificateVerificationException var6) {
            throw var6;
        } catch (IOException var7) {
            throw new CertificateVerificationException("Connection error");
        } catch (Exception var8) {
            throw new CertificateVerificationException("Can not verify CRL for certificate: " + var0.getSubjectX500Principal());
        }
    }

    public static void verifyCertificateFromLocalCRL(X509Certificate var0, String var1) throws CertificateVerificationException {
        try {
            FileInputStream var2 = new FileInputStream(var1);
            CertificateFactory var3 = CertificateFactory.getInstance("X.509");
            X509CRL var4 = (X509CRL)var3.generateCRL(var2);
            if (var4.isRevoked(var0)) {
                throw new CertificateVerificationException("The certificate is revoked by the CRL");
            }
        } catch (CertificateVerificationException var5) {
            throw var5;
        } catch (FileNotFoundException var6) {
            throw new CertificateVerificationException("Certificate from path:\n" + var1 + "\nwas not found");
        } catch (Exception var7) {
            throw new CertificateVerificationException("Can not verify CRL for certificate: " + var0.getSubjectX500Principal());
        }
    }

    private static X509CRL downloadCRL(String var0, Proxy var1) throws IOException, CertificateException, CRLException, CertificateVerificationException {
        if (crlDistCached.containsKey(var0) && crlDistTimeCached.containsKey(var0) && Long.parseLong((String)crlDistTimeCached.get(var0)) + 3600000L > System.currentTimeMillis()) {
            System.out.println("GOT CACHED CRL " + var0);
            return (X509CRL)crlDistCached.get(var0);
        } else {
            X509CRL var2;
            if (!var0.startsWith("http://") && !var0.startsWith("https://") && !var0.startsWith("ftp://")) {
                if (var0.startsWith("ldap://")) {
                    var2 = downloadCRLFromLDAP(var0);
                    crlDistCached.put(var0, var2);
                    crlDistTimeCached.put(var0, System.currentTimeMillis() + "");
                    return var2;
                } else {
                    throw new CertificateVerificationException("Can not download CRL from certificate distribution point: " + var0);
                }
            } else {
                var2 = downloadCRLFromWeb(var0, var1);
                crlDistCached.put(var0, var2);
                crlDistTimeCached.put(var0, System.currentTimeMillis() + "");
                return var2;
            }
        }
    }

    private static X509CRL downloadCRLFromLDAP(String var0) throws CertificateException, CRLException, CertificateVerificationException {
        Hashtable var1 = new Hashtable();
        var1.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        var1.put("java.naming.provider.url", var0);
   //     InitialDirContext var2 = new InitialDirContext(var1);
    //    Attributes var3 = var2.getAttributes("");
      //  Attribute var4 = var3.get("certificateRevocationList;binary");
      /*  byte[] var5 = (byte[])((byte[])var4.get());
        if (var5 != null && var5.length != 0) {
            ByteArrayInputStream var6 = new ByteArrayInputStream(var5);
            CertificateFactory var7 = CertificateFactory.getInstance("X.509");
            X509CRL var8 = (X509CRL)var7.generateCRL(var6);
            return var8;
        } else {
            throw new CertificateVerificationException("Can not download CRL from: " + var0);
        }*/
        return null;
    }

    private static X509CRL downloadCRLFromWeb(String var0, Proxy var1) throws MalformedURLException, IOException, CertificateException, CRLException {
        URL var2 = new URL(var0);
        if (var1 == null) {
            var1 = Proxy.NO_PROXY;
        }

        InputStream var3 = var2.openConnection(var1).getInputStream();

        X509CRL var6;
        try {
            CertificateFactory var4 = CertificateFactory.getInstance("X.509");
            X509CRL var5 = (X509CRL)var4.generateCRL(var3);
            var6 = var5;
        } finally {
            var3.close();
        }

        return var6;
    }

    public static void downloadCRLFromWebsite(String var0, Proxy var1) throws MalformedURLException, IOException, CertificateException, CRLException {
        URL var2 = new URL(var0);
        if (var1 == null) {
            var1 = Proxy.NO_PROXY;
        }

        InputStream var3 = var2.openConnection(var1).getInputStream();
        FileOutputStream var5 = new FileOutputStream(new File("LatestCRL.crl"));
        byte[] var6 = new byte[var3.available()];

        int var4;
        while((var4 = var3.read(var6)) != -1) {
            var5.write(var6, 0, var4);
        }

        var5.flush();
        var5.close();
    }

    public static List<String> getCrlDistributionPoints(X509Certificate var0) throws CertificateParsingException, IOException {
        byte[] var1 = var0.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());
        if (var1 == null) {
            ArrayList var18 = new ArrayList();
            return var18;
        } else {
            ASN1InputStream var2 = new ASN1InputStream(new ByteArrayInputStream(var1));
            ASN1Primitive var3 = var2.readObject();
            DEROctetString var4 = (DEROctetString)var3;
            byte[] var5 = var4.getOctets();
            ASN1InputStream var6 = new ASN1InputStream(new ByteArrayInputStream(var5));
            ASN1Primitive var7 = var6.readObject();
            CRLDistPoint var8 = CRLDistPoint.getInstance(var7);
            ArrayList var9 = new ArrayList();
            DistributionPoint[] var10 = var8.getDistributionPoints();
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
                DistributionPoint var13 = var10[var12];
                DistributionPointName var14 = var13.getDistributionPoint();
                if (var14 != null && var14.getType() == 0) {
                    GeneralName[] var15 = GeneralNames.getInstance(var14.getName()).getNames();

                    for(int var16 = 0; var16 < var15.length; ++var16) {
                        if (var15[var16].getTagNo() == 6) {
                            String var17 = DERIA5String.getInstance(var15[var16].getName()).getString();
                            var9.add(var17);
                        }
                    }
                }
            }

            return var9;
        }
    }
}
