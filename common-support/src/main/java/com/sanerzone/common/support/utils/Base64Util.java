
package com.sanerzone.common.support.utils;

import org.apache.commons.codec.binary.Base64;

public final class Base64Util {



    public static String encode(byte[] binaryData) {
        return Base64.encodeBase64String(binaryData);
    }

    public static byte[] decode(String encoded) {
        return Base64.decodeBase64(encoded);
    }
}
