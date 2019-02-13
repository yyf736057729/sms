package com.sanerzone.smscenter.common.tools;

import org.apache.commons.codec.binary.Base64;

public final class Base64Helper {



    public static String encode(byte[] binaryData) {
        return Base64.encodeBase64String(binaryData);
    }

    public static byte[] decode(String encoded) {
        return Base64.decodeBase64(encoded);
    }
}
