package com.zx.sms.codec.cmpp.msg;

import java.io.Serializable;

/**
 * 
 * @author huzorro(huzorro@gmail.com)
 *
 */
public interface Header extends Serializable {
    public void setHeadLength(long length);
    public long getHeadLength();
    public void setPacketLength(long length);
    public long getPacketLength();
    public void setBodyLength(long length);
    public long getBodyLength();
    public void setCommandId(long commandId);
    public long getCommandId();
    public void setSequenceId(long transitionId);
    public long getSequenceId();
    public long getNodeId();
    public void setNodeId(long nodeId);
}
