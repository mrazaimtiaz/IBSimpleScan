package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Arrays;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

public class CertificateVerifierOCSP {
    CertificateVerifierOCSP() {
    }

    private static OCSPReq generateOCSPRequest(X509Certificate var0, BigInteger var1) throws OCSPException, IOException, OperatorException, CertificateEncodingException {
        Security.addProvider(new BouncyCastleProvider());
        CertificateID var2 = new CertificateID((new JcaDigestCalculatorProviderBuilder()).build().get(CertificateID.HASH_SHA1), new JcaX509CertificateHolder(var0), var1);
        OCSPReqBuilder var3 = new OCSPReqBuilder();
        var3.addRequest(var2);
        ASN1ObjectIdentifier var4 = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.48.1.2");
        Extension var5 = new Extension(var4, false, new DEROctetString(new byte[]{1, 3, 6, 1, 5, 5, 7, 48, 1, 1}));
        var3.setRequestExtensions(new Extensions(new Extension[]{var5}));
        return var3.build();
    }

    private static byte[] PostData(String var0, byte[] var1, String var2, String var3, Proxy var4) throws MalformedURLException, IOException {
        URL var5 = new URL(var0);
        HttpURLConnection var6;
        if (var4 != null) {
            var6 = (HttpURLConnection)var5.openConnection(var4);
        } else {
            var6 = (HttpURLConnection)var5.openConnection();
        }

        var6.setRequestMethod("POST");
        var6.setRequestProperty("Content-Type", var2);
        var6.setRequestProperty("Content-Length", "" + var1.length);
        var6.setRequestProperty("Accept", var3);
        var6.setDoOutput(true);
        DataOutputStream var7 = new DataOutputStream(var6.getOutputStream());
        var7.write(var1);
        var7.flush();
        var7.close();
        InputStream var8 = var6.getInputStream();
     //   byte[] var9 = IOUtils.readFully(var8, -1, true);
        var8.close();
        return null;
    }

    private static CertificateStatusEnum ProcessOcspResponse(X509Certificate var0, X509Certificate var1, byte[] var2) throws IOException, OCSPException, OperatorCreationException, CertificateEncodingException {
        OCSPResp var3 = new OCSPResp(var2);
        switch(var3.getStatus()) {
            case 0:
                BasicOCSPResp var4 = (BasicOCSPResp)var3.getResponseObject();
                if (var4.getResponses().length == 1) {
                    SingleResp var5 = var4.getResponses()[0];
                    ValidateCertificateId(var1, var0, var5.getCertID());
                    CertificateStatus var6 = var5.getCertStatus();
                    if (var6 == CertificateStatus.GOOD) {
                        return CertificateStatusEnum.Good;
                    }

                    if (var6 instanceof RevokedStatus) {
                        return CertificateStatusEnum.Revoked;
                    }

                    return CertificateStatusEnum.Unknown;
                }

                return CertificateStatusEnum.Unknown;
            default:
                throw new OCSPException("Unknown response status " + var3.getStatus());
        }
    }

    private static void ValidateCertificateId(X509Certificate var0, X509Certificate var1, CertificateID var2) throws OCSPException, OperatorCreationException, CertificateEncodingException {
        CertificateID var3 = new CertificateID((new JcaDigestCalculatorProviderBuilder()).build().get(CertificateID.HASH_SHA1), new JcaX509CertificateHolder(var0), var1.getSerialNumber());
        if (!var3.getSerialNumber().equals(var2.getSerialNumber())) {
            throw new OCSPException("Invalid certificate ID in response");
        } else if (!Arrays.areEqual(var3.getIssuerNameHash(), var2.getIssuerNameHash())) {
            throw new OCSPException("Invalid certificate Issuer in response");
        }
    }

    public static X509Certificate GetCertificateFromResource(String var0) {
    //    try {
            InputStream var1 = CertificateVerifierOCSP.class.getClassLoader().getClass().getResourceAsStream(var0);
        /*    byte[] var2 = IOUtils.readFully(var1, -1, true);

            CertificateFactory var3;
            try {
                var3 = CertificateFactory.getInstance("X509");
            } catch (CertificateException var6) {
                return null;
            }

            ByteArrayInputStream var4 = new ByteArrayInputStream(var2);
            X509Certificate var5 = (X509Certificate)var3.generateCertificate(var4);
            return var5;*/
            return null;
    /*    } catch (CertificateException | IOException var7) {
            return null;
        }*/
    }

    private static ASN1Object GetExtensionValue(X509Certificate var0, String var1) throws IOException {
        byte[] var2 = var0.getExtensionValue(var1);
        if (var2 == null) {
            return null;
        } else {
            ASN1InputStream var3 = new ASN1InputStream(var2);
            ASN1OctetString var4 = (ASN1OctetString)var3.readObject();
            var3 = new ASN1InputStream(var4.getOctets());
            return var3.readObject();
        }
    }

    private static CertificateStatusEnum Query(X509Certificate var0, X509Certificate var1, Proxy var2) throws IOException, OCSPException, OperatorCreationException, CertificateEncodingException {
        String[] var3 = GetAuthorityInformationAccessOcspUrl(var0);
        if (var3 != null && var3.length != 0) {
            String var4 = var3[0];

            OCSPReq var5;
            try {
                var5 = generateOCSPRequest(var1, var0.getSerialNumber());
            } catch (CertificateEncodingException | OCSPException | OperatorException | IOException var9) {
                throw new OCSPException("Error in procesing OCSP request");
            }

            byte[] var6;
            try {
                var6 = PostData(var4, var5.getEncoded(), "application/ocsp-request", "application/ocsp-response", var2);
            } catch (IOException var8) {
                throw new OCSPException("Error in sending OCSP request");
            }

            return ProcessOcspResponse(var0, var1, var6);
        } else {
            throw new OCSPException("No OCSP url found in ee certificate.");
        }
    }

    private static String[] GetAuthorityInformationAccessOcspUrl(X509Certificate var0) throws IOException {
        ArrayList var1 = new ArrayList();
        ASN1Object var2 = GetExtensionValue(var0, X509Extensions.AuthorityInfoAccess.getId());
        if (var2 == null) {
            return null;
        } else {
            ASN1Sequence var3 = (ASN1Sequence)var2;
            Enumeration var4 = var3.getObjects();

            while(var4.hasMoreElements()) {
                ASN1Sequence var5 = (ASN1Sequence)var4.nextElement();
                ASN1ObjectIdentifier var6 = (ASN1ObjectIdentifier)var5.getObjectAt(0);
                if (var6.getId().equals("1.3.6.1.5.5.7.48.1")) {
                    ASN1TaggedObject var7 = (ASN1TaggedObject)var5.getObjectAt(1);
                    GeneralName var8 = GeneralName.getInstance(var7);
                    var1.add(DERIA5String.getInstance(var8.getName()).getString());
                }
            }

            return (String[])var1.toArray(new String[var1.size()]);
        }
    }

    public static boolean ValidateCertificateWithOCSP(X509Certificate var0, X509Certificate var1, Proxy var2) {
        try {
            if (!var0.getIssuerDN().equals(var1.getSubjectDN())) {
                return false;
            } else {
                var0.checkValidity();
                var0.verify(var1.getPublicKey());
                return Query(var0, var1, var2) == CertificateStatusEnum.Good;
            }
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException | CertificateException | OCSPException | OperatorCreationException | NullPointerException var4) {
            return false;
        }
    }

    private static enum CertificateStatusEnum {
        Good,
        Revoked,
        Unknown;

        private CertificateStatusEnum() {
        }
    }
}

