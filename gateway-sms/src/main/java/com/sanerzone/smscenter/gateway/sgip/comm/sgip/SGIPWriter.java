// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-7-16 19:21:47
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   SGIPWriter.java

package com.sanerzone.smscenter.gateway.sgip.comm.sgip;

import java.io.IOException;
import java.io.OutputStream;

import com.sanerzone.smscenter.gateway.sgip.comm.PMessage;
import com.sanerzone.smscenter.gateway.sgip.comm.PWriter;
import com.sanerzone.smscenter.gateway.sgip.comm.sgip.message.SGIPMessage;

public class SGIPWriter extends PWriter
{

    public SGIPWriter(OutputStream out)
    {
        this.out = out;
    }

    public void write(PMessage message)
        throws Exception
    {
        SGIPMessage msg = (SGIPMessage)message;
        out.write(msg.getBytes());
    }

    public void writeHeartbeat()
        throws IOException
    {
    }

    protected OutputStream out;
}