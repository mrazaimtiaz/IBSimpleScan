package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.util.ArrayList;

public class Utilities {
    private static final char[] HEX_CHARACTERS = "0123456789ABCDEF".toCharArray();

    Utilities() {
    }

    public static String NodeValueToEF(String var0) {
        if (var0.matches("^[0-9a-fA-F]{2}")) {
            return var0;
        } else {
            return var0.matches("^[0-9a-fA-F]{1}") ? "0" + var0.charAt(var0.length() - 1) : "00";
        }
    }

    public static String bytesToHex(byte[] var0) {
        char[] var1 = new char[var0.length * 2];

        for(int var2 = 0; var2 < var0.length; ++var2) {
            int var3 = var0[var2] & 255;
            var1[var2 * 2] = HEX_CHARACTERS[var3 >>> 4];
            var1[var2 * 2 + 1] = HEX_CHARACTERS[var3 & 15];
        }

        return new String(var1);
    }

    public static String intToHex(int var0) {
        StringBuilder var1 = new StringBuilder();
        ArrayList var2 = new ArrayList();
        int var3 = var0;
        boolean var4 = true;

        int var5;
        do {
            if (var4) {
                var4 = false;
            } else {
                var3 >>>= 8;
            }

            var5 = var3 & 255;
            var2.add("" + HEX_CHARACTERS[var5 >>> 4] + HEX_CHARACTERS[var5 & 15]);
        } while(var3 > 255);

        for(var5 = var2.size(); var5 > 0; --var5) {
            var1.append((String)var2.get(var5 - 1));
        }

        return var1.toString();
    }

    public static long bytesToLong(byte[] var0) {
        long var1 = 0L;
        if (var0.length > 4) {
            return 0L;
        } else {
            long var3 = 255L;
            long var5 = 0L;

            for(int var7 = var0.length - 1; var7 >= 0; var5 += 8L) {
                var1 += (long)(var0[var7] << (int)var5) & var3;
                var3 <<= 8;
                --var7;
            }

            return var1;
        }
    }

    public static String bytesToHex(byte[] var0, int var1) {
        char[] var2 = new char[var0.length * 2];
        int var3 = var1 < var0.length ? var1 : var0.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var0[var4] & 255;
            var2[var4 * 2] = HEX_CHARACTERS[var5 >>> 4];
            var2[var4 * 2 + 1] = HEX_CHARACTERS[var5 & 15];
        }

        return new String(var2);
    }

    public static String bytesToHex(byte var0) {
        char[] var1 = new char[2];
        int var2 = var0 & 255;
        var1[0] = HEX_CHARACTERS[var2 >>> 4];
        var1[1] = HEX_CHARACTERS[var2 & 15];
        return new String(var1);
    }

    public static byte[] hexToByteArray(String var0) {
        ArrayList var1 = new ArrayList();
        int var2 = 0;
        if (var0.length() % 2 == 1) {
            var1.add("0" + var0.charAt(0));
            ++var2;
        }

        for(int var3 = var2; var3 < var0.length(); var3 += 2) {
            var1.add("" + var0.charAt(var3) + var0.charAt(var3 + 1));
        }

        byte[] var5 = new byte[var1.size()];

        for(int var4 = 0; var4 < var1.size(); ++var4) {
            var5[var4] = Integer.decode("0x" + (String)var1.get(var4)).byteValue();
        }

        return var5;
    }
}
