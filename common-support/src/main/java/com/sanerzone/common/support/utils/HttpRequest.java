package com.sanerzone.common.support.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanerzone.common.support.security.Digests;

public class HttpRequest
{
    public static Logger logger = LoggerFactory.getLogger(HttpRequest.class);
    
    public static int HTTP_READ_TIME_OUT = 120000;
    
    public static String sendGet(String url, String param, HashMap<String, String> header)
    {
            return sendGet(url, param, header, "UTF-8", HTTP_READ_TIME_OUT);
    }
    
    public static String sendPost(String url, String param, HashMap<String, String> header)
    {
        return sendFormPost(url, param, header, "UTF-8", HTTP_READ_TIME_OUT);
    }
    
    public static String sendFormPost(String postUrl, String postEntity, HashMap<String, String> postHeaders,
        String charset, int timeout)
    {
        return sendPost(postUrl, postEntity, postHeaders, charset, "application/x-www-form-urlencoded", timeout);
    }
    
    public static String sendPostJson(String url, String param, HashMap<String, String> header, int timeout)
    {
        return sendPost(url, param, header, "UTF-8", "application/json", timeout);
    }
    
    public static String sendTextPost(String postUrl, String postEntity, HashMap<String, String> postHeaders, String charset, int timeout)
    {
        return sendPost(postUrl, postEntity, postHeaders, charset, "text/plain;charset=" + charset, timeout);
    }
    
    public static String sendGet(String url, String param, HashMap<String, String> header, String charset, int timeout)
    {
        
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        StringBuilder sbStr = new StringBuilder();
        
        try
        {
            String urlNameString = url + "?" + param;
            URL postURL = new URL(urlNameString);
            httpURLConnection = (HttpURLConnection)postURL.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestProperty("Accept-Charset", charset);
            httpURLConnection.setConnectTimeout(200000);
            httpURLConnection.setReadTimeout(timeout);
            if (header != null)
            {
                for (String pKey : header.keySet())
                {
                    httpURLConnection.setRequestProperty(pKey, header.get(pKey));
                }
            }
            //      httpURLConnection.connect(); 
            if (httpURLConnection.getResponseCode() > 300)
            {
                logger.error("HTTP响应异常=>请求参数:{}, 状态码:{}", param, httpURLConnection.getResponseCode());
                return "SMS.ERROR.HTTP." + httpURLConnection.getResponseCode();
            }
            
//            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), charset));
            byte[] httpBody = IOUtils.toByteArray(httpURLConnection.getInputStream());
            sbStr.append(new String(httpBody, charset));
//            String line;
//            while ((line = bufferedReader.readLine()) != null)
//            {
//                sbStr.append(line);
//            }
        }
        catch (SocketTimeoutException e)
        {
            logger.error("POST请求超时,请求:" + url + ", 参数" + param, e);
            return "JYC.ERROR.HTTP.1408";
        }
        catch (Exception e)
        {
            logger.error("POST请求IO异常,请求:" + url + ", 参数" + param, e);
            return "JYC.ERROR.HTTP.1500";
        }
        finally
        {
            if (bufferedReader != null)
            {
                try
                {
                    bufferedReader.close();
                }
                catch (IOException e)
                {
                    logger.error("POST请求读取Buffer异常,请求:" + url + ", 参数" + param, e);
                    return "JYC.ERROR.HTTP.1501";
                }
            }
            if (httpURLConnection != null)
            {
                httpURLConnection.disconnect();
            }
        }
        
        return sbStr.toString();
        
    }
    
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     * @throws IOException 
     */
    public static String sendPost(String postUrl, String postEntity, Map<String, String> postHeaders, String charset,
        String contentType, int timeout)
    {
        
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        StringBuilder sbStr = new StringBuilder();
        
        try
        {
            URL postURL = new URL(postUrl);
            httpURLConnection = (HttpURLConnection)postURL.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestProperty("Content-Type", contentType);
            httpURLConnection.setConnectTimeout(200000);
            httpURLConnection.setReadTimeout(timeout);
            if (postHeaders != null)
            {
                for (String pKey : postHeaders.keySet())
                {
                    httpURLConnection.setRequestProperty(pKey, postHeaders.get(pKey));
                }
            }
            if (postEntity != null)
            {
                DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
                out.write(postEntity.getBytes(charset));
                out.flush();
                out.close(); // flush and close 
            }
            if (httpURLConnection.getResponseCode() > 300)
            {
                logger.error("HTTP响应异常=>请求参数:{}, 状态码:{}", postEntity, httpURLConnection.getResponseCode());
                return "JYC.ERROR.HTTP." + httpURLConnection.getResponseCode();
            }
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), charset));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                sbStr.append(line);
            }
        }
        catch (SocketTimeoutException se)
        {
            logger.error("POST请求超时,请求:" + postUrl + ", 参数" + postEntity, se);
            return "JYC.ERROR.HTTP.1408";
        }
        catch (Exception e)
        {
            logger.error("POST请求IO异常,请求:" + postUrl + ", 参数" + postEntity, e);
            return "JYC.ERROR.HTTP.1500";
        }
        finally
        {
            if (bufferedReader != null)
            {
                try
                {
                    bufferedReader.close();
                }
                catch (IOException e)
                {
                    logger.error("POST请求读取Buffer异常,请求:" + postUrl + ", 参数" + postEntity, e);
                    return "JYC.ERROR.HTTP.1501";
                }
            }
            if (httpURLConnection != null)
            {
                httpURLConnection.disconnect();
            }
        }
        return sbStr.toString();
    }
    
    // tools
    public static String md5(String input)
    {
        return Encodes.encodeHex(Digests.md5(input.getBytes()));
    }
    
    public static String sortParams(Map<String, Object> srcMap){
        TreeMap<String, Object> treeMap = new TreeMap<String, Object>(srcMap);
        Iterator<String> it = treeMap.keySet().iterator();
        StringBuilder destStr = new StringBuilder();
        while(it.hasNext()) {
            String key = it.next();
            destStr.append(key).append("=").append(srcMap.get(key));
        }
        return destStr.toString();
    }
    
    public static String getElementValue(Element element, String name, String defaultValue) {
        if (element == null)
            return defaultValue;

        Element c = element.element(name);
        if (c == null)
            return defaultValue;

        return c.getText();
    }    
    
}