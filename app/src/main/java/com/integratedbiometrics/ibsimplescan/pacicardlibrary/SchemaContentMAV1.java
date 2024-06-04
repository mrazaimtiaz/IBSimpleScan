package com.integratedbiometrics.ibsimplescan.pacicardlibrary;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;


final class SchemaContentMAV1 extends SchemaContentAbstract {
    public SchemaContentMAV1(String var1) {
        super(var1);
    }

    public void ReadFile() {
        try {
            InputStream var1 = this.getClass().getClassLoader().getResourceAsStream("Schema/PACICardSchema111005711.xsd");
            DocumentBuilderFactory var2 = DocumentBuilderFactory.newInstance();
            DocumentBuilder var3 = var2.newDocumentBuilder();
            Document var4 = var3.parse(var1);
            var4.getDocumentElement().normalize();
            XPath var5 = XPathFactory.newInstance().newXPath();
            String var6 = "/schema/File/element";
            NodeList var7 = (NodeList)var5.compile(var6).evaluate(var4, XPathConstants.NODESET);
            int var8 = var7.getLength();

            for(int var9 = 0; var9 < var8; ++var9) {
                Node var10 = var7.item(var9);
                NamedNodeMap var11 = var10.getAttributes();
                Node var12 = var11.getNamedItem("name");
                Node var13 = var11.getNamedItem("index");
                Node var14 = var11.getNamedItem("byteSize");
                Node var15 = var11.getNamedItem("file");
                ConcurrentHashMap var16 = new ConcurrentHashMap();
                var16.put("FileName", "3f00020002" + Utilities.NodeValueToEF(var15.getNodeValue()));
                String var17 = var14.getNodeValue();
                String var18 = var13.getNodeValue();
                if (var17.matches("[0-9]+")) {
                    var16.put("Length", Integer.parseInt(var17));
                } else {
                    var16.put("Length", 0);
                }

                if (var18.matches("[0-9]+")) {
                    var16.put("Offset", Integer.parseInt(var18));
                } else {
                    var16.put("Offset", 0);
                }

                this.Info.put(var12.getNodeValue(), var16);
            }
        } catch (Exception var19) {
        }

    }

    public void ReadFile(String var1) {
        try {
            InputStream var2 = this.getClass().getClassLoader().getResourceAsStream("Schema/PACICardSchema" + var1 + ".xsd");
            DocumentBuilderFactory var3 = DocumentBuilderFactory.newInstance();
            DocumentBuilder var4 = var3.newDocumentBuilder();
            Document var5 = var4.parse(var2);
            var5.getDocumentElement().normalize();
            XPath var6 = XPathFactory.newInstance().newXPath();
            String var7 = "/schema/File/element";
            NodeList var8 = (NodeList)var6.compile(var7).evaluate(var5, XPathConstants.NODESET);
            int var9 = var8.getLength();

            for(int var10 = 0; var10 < var9; ++var10) {
                Node var11 = var8.item(var10);
                NamedNodeMap var12 = var11.getAttributes();
                Node var13 = var12.getNamedItem("name");
                Node var14 = var12.getNamedItem("index");
                Node var15 = var12.getNamedItem("byteSize");
                Node var16 = var12.getNamedItem("file");
                ConcurrentHashMap var17 = new ConcurrentHashMap();
                var17.put("FileName", "3f00020002" + Utilities.NodeValueToEF(var16.getNodeValue()));
                String var18 = var15.getNodeValue();
                String var19 = var14.getNodeValue();
                if (var18.matches("[0-9]+")) {
                    var17.put("Length", Integer.parseInt(var18));
                } else {
                    var17.put("Length", 0);
                }

                if (var19.matches("[0-9]+")) {
                    var17.put("Offset", Integer.parseInt(var19));
                } else {
                    var17.put("Offset", 0);
                }

                this.Info.put(var13.getNodeValue(), var17);
            }
        } catch (Exception var20) {
            this.Info.clear();
            this.ReadFile();
        }

    }
}

