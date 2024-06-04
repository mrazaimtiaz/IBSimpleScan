package com.integratedbiometrics.ibsimplescan.pacicardlibrary;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

public class DataConstants {
    public static final String LATEST_CRL_URL = "http://crl.paci.gov.kw/GovernmentofKuwaitPACICIVILID/LatestCRL.crl";
    public static final String CERT_MATCH_STRING = "\\bThe Public Authority for Civil Information ID\\b";
    public static final String MAV1_ATR_REGEX = "3B6A00008065A2014{1}(2|5|7|8){1}013D72D643";
    public static final String MAV3_ATR_REGEX = "3B7F960000803180655{1}(2|5|7|8){1}850300EF124140829000";
    public static final byte[] MAV1_GEM_AID = new byte[]{0, -92, 4, 0, 12, -96, 0, 0, 0, 24, 12, 0, 0, 1, 99, 66, 0};
    public static final byte[] MAV1_AID = new byte[]{0, -92, 4, 0, 16, -96, 0, 0, 0, 24, 48, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0};
    public static final byte[] MAV3_GEM_AID = new byte[]{0, -92, 4, 0, 12, -96, 0, 0, 0, 24, 64, 0, 0, 1, 99, 66, 0};
    public static final byte[] MAV3_AID = new byte[]{0, -92, 4, 0, 12, -96, 0, 0, 0, 24, 64, 0, 0, 1, 99, 66, 0};
    public static final byte[] MAV3_GEM_AID_CONTACTLESS = new byte[0];
    public static final byte[] MAV3_AID_CONTACTLESS = new byte[]{-96, 0, 0, 3, -105, 67, 73, 68, 95, 1, 0};
    public static final byte[] GET_EF_LENGTH_COMMAND_DATA = new byte[]{0, -64, 0, 0};
    public static final String MAV1_AOID = "3f0050005003";
    public static final String MAV3_AOID_PUBLIC_SLOT = "3f0050005003";
    public static final String MAV3_AOID_PACI_SLOT = "3f0051005003";
    public static final String MAV3_AOID_CARD_CERT_SLOT = "3f0052005003";
    public static final String MAV3_EF_CERT_PATH = "3f005200B000";
    public static final String CARD_CONTENT_FILE_PATH = "3f000200";
    public static final String CARD_CONTENT_DF = "02";
    public static final String SCHEMA_PATH = "Schema/";
    public static final String SCHEMA_NAME = "PACICardSchema";
    public static final String SCHEMA_EXTENTION = ".xsd";
    public static final String SCHEMA_DEFAULT_VERSION = "111005711";
    public static final String SCHEMA_DEFAULT_FULL_PATH = "Schema/PACICardSchema111005711.xsd";
    public static final String SCHEMA_EXPRESSION = "/schema/File/element";
    public static final String SCHEMA_ATTRIBUTE_NAME = "name";
    public static final String SCHEMA_ATTRIBUTE_INDEX = "index";
    public static final String SCHEMA_ATTRIBUTE_BYTE_SIZE = "byteSize";
    public static final String SCHEMA_ATTRIBUTE_FILE = "file";
    public static final String SCHEMA_ATTRIBUTE_TYPE = "type";
    public static final String SCHEMA_VALUE_TYPE_STRING = "xs:string";
    public static final String SCHEMA_VALUE_TYPE_UNSIGNED_SHORT = "xs:unsignedShort";
    public static final String SCHEMA_VALUE_TYPE_BASE_64_BINARY = "xs:base64Binary";
    public static final int SERIAL_NUMBER_BYTE_LENGTH = 5;
    private static final String CERT_PATH = "RootCert/";
    public static final String CERT_OCSP_PATH = "RootCert/PACIIDIssuanceCA.cer";
    public static final String OCSP_SIGNING = "1.3.6.1.5.5.7.3.9";
    public static final String OCSP_OID = "1.3.6.1.5.5.7.48.1";

    DataConstants() {
    }
}

