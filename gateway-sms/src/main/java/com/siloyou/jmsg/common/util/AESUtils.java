package com.siloyou.jmsg.common.util;


import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils
{
  public static byte[] encrypt(String content, String keyWord)
  {
    try
    {
      KeyGenerator kgen = KeyGenerator.getInstance("AES");
      SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
      secureRandom.setSeed(keyWord.getBytes());
      kgen.init(128, secureRandom);
      SecretKey secretKey = kgen.generateKey();
      byte[] enCodeFormat = secretKey.getEncoded();
      SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
      Cipher cipher = Cipher.getInstance("AES");
      byte[] byteContent = content.getBytes("utf-8");
      cipher.init(1, key);
      return cipher.doFinal(byteContent);
    }
    catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }
    catch (NoSuchPaddingException e)
    {
      e.printStackTrace();
    }
    catch (InvalidKeyException e)
    {
      e.printStackTrace();
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    catch (IllegalBlockSizeException e)
    {
      e.printStackTrace();
    }
    catch (BadPaddingException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static String encryptToHex(String content, String password)
  {
    return parseByte2HexStr(encrypt(content, password));
  }
  
  public static String encryptToBase64(String content, String password)
    throws Exception
  {
    return Base64Util.encode(encrypt(content, password));
  }
  
  public static byte[] decrypt(byte[] content, String keyWord)
  {
    try
    {
      KeyGenerator kgen = KeyGenerator.getInstance("AES");
      SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
      secureRandom.setSeed(keyWord.getBytes());
      kgen.init(128, secureRandom);
      SecretKey secretKey = kgen.generateKey();
      byte[] enCodeFormat = secretKey.getEncoded();
      SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(2, key);
      return cipher.doFinal(content);
    }
    catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }
    catch (NoSuchPaddingException e)
    {
      e.printStackTrace();
    }
    catch (InvalidKeyException e)
    {
      e.printStackTrace();
    }
    catch (IllegalBlockSizeException e)
    {
      e.printStackTrace();
    }
    catch (BadPaddingException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static byte[] decryptHex(String content, String keyWord)
  {
    return decrypt(parseHexStr2Byte(content), keyWord);
  }
  
  public static byte[] decrypt(String content, String keyWord)
    throws Exception
  {
    return decrypt(Base64Util.decode(content), keyWord);
  }
  
  public static String parseByte2HexStr(byte[] buf)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < buf.length; i++)
    {
      String hex = Integer.toHexString(buf[i] & 0xFF);
      if (hex.length() == 1) {
        hex = '0' + hex;
      }
      sb.append(hex.toUpperCase());
    }
    return sb.toString();
  }
  
  public static byte[] parseHexStr2Byte(String hexString)
  {
    if (hexString.length() < 1) {
      return null;
    }
    byte[] byteArray = new byte[hexString.length() / 2];
    for (int i = 0; i < hexString.length(); i += 2)
    {
      byte tmpByte = new Integer(Integer.parseInt(hexString.substring(i, i + 2), 16)).byteValue();
      byteArray[(i / 2)] = tmpByte;
    }
    return byteArray;
  }
}