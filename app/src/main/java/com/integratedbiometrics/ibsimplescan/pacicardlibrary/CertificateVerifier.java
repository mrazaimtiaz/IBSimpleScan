package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


class CertificateVerifier {
    CertificateVerifier() {
    }

    public static PKIXCertPathBuilderResult verifyCertificate(X509Certificate var0, Set<X509Certificate> var1, Proxy var2) throws CertificateVerificationException {
        try {
            if (isSelfSigned(var0)) {
                throw new CertificateVerificationException("The certificate is self-signed.");
            } else {
                HashSet var3 = new HashSet();
                HashSet var4 = new HashSet();
                Iterator var5 = var1.iterator();

                while(var5.hasNext()) {
                    X509Certificate var6 = (X509Certificate)var5.next();
                    if (isSelfSigned(var6)) {
                        var3.add(var6);
                    } else {
                        var4.add(var6);
                    }
                }

                PKIXCertPathBuilderResult var10 = verifyCertificate(var0, var3, (Set)var4);
                CRLVerifier.verifyCertificateCRLs(var0, var2);
                return var10;
            }
        } catch (CertPathBuilderException var7) {
            throw new CertificateVerificationException("Error building certification path: " + var0.getSubjectX500Principal(), var7);
        } catch (CertificateVerificationException var8) {
            throw var8;
        } catch (Exception var9) {
            throw new CertificateVerificationException("Error verifying the certificate: " + var0.getSubjectX500Principal(), var9);
        }
    }

    public static boolean isSelfSigned(X509Certificate var0) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            PublicKey var1 = var0.getPublicKey();
            var0.verify(var1);
            return true;
        } catch (SignatureException var2) {
            return false;
        } catch (InvalidKeyException var3) {
            return false;
        }
    }

    private static PKIXCertPathBuilderResult verifyCertificate(X509Certificate var0, Set<X509Certificate> var1, Set<X509Certificate> var2) throws GeneralSecurityException {
        X509CertSelector var3 = new X509CertSelector();
        var3.setCertificate(var0);
        HashSet var4 = new HashSet();
        Iterator var5 = var1.iterator();

        while(var5.hasNext()) {
            X509Certificate var6 = (X509Certificate)var5.next();
            var4.add(new TrustAnchor(var6, (byte[])null));
        }

        PKIXBuilderParameters var9 = new PKIXBuilderParameters(var4, var3);
        var9.setRevocationEnabled(false);
        CertStore var10 = CertStore.getInstance("Collection", new CollectionCertStoreParameters(var2), "BC");
        var9.addCertStore(var10);
        CertPathBuilder var7 = CertPathBuilder.getInstance("PKIX", "BC");
        PKIXCertPathBuilderResult var8 = (PKIXCertPathBuilderResult)var7.build(var9);
        return var8;
    }
}

