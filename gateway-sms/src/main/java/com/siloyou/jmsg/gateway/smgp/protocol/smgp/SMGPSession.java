package com.siloyou.jmsg.gateway.smgp.protocol.smgp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siloyou.jmsg.gateway.smgp.protocol.DefaultMoListener;
import com.siloyou.jmsg.gateway.smgp.protocol.Message;
import com.siloyou.jmsg.gateway.smgp.protocol.MoListener;
import com.siloyou.jmsg.gateway.smgp.protocol.Session;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPActiveTestMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPActiveTestRespMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPBaseMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPDeliverMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPDeliverRespMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPExitMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPExitRespMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPLoginMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPLoginRespMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPSubmitMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPSubmitRespMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.util.MD5;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.util.SequenceGenerator;

/**
 * 登陆会话
 * User: gaodi.gao
 * Date: 14-6-18
 * Time: 下午4:15
 * To change this template use File | Settings | File Templates.
 */
public class SMGPSession implements Session {

    private static final Logger log = LoggerFactory.getLogger(SMGPSession.class);
    
    private MoListener moListener = new DefaultMoListener();
    
    private SMGPConnection connection;
    private String sessionId;
    private boolean authenticated;
    private Object lock = new Object();
    
    private String                          id;
    private Map<String, String> args;
    private boolean							gatewaySign = false;

    public SMGPSession(SMGPConnection connection, boolean authenticated){
        super();
        this.connection = connection;
        this.sessionId = java.util.UUID.randomUUID().toString();
        this.authenticated = authenticated;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void submit(String content, String spNumber, String userNumber){
        SMGPSubmitMessage submit = new SMGPSubmitMessage();
        submit.setSrcTermId(spNumber);
        submit.setDestTermIdArray(new String[] { userNumber });
        submit.setMsgFmt((byte) 8);

        byte[] bContent = null;
        try {
            bContent = content.getBytes("iso-10646-ucs-2");
        } catch (UnsupportedEncodingException e) {}

        if (bContent != null && bContent.length <= 140) {
            submit.setBMsgContent(bContent);
            submit.setMsgFmt((byte) 8);
            submit.setNeedReport((byte) 1);
            submit.setServiceId("");
            submit.setAtTime("");
            submit.setNeedReport((byte) 1);
            submit.setSequenceNumber(SequenceGenerator.nextSequence());
            send(submit);
        }
    }

    @Override
    public void heartbeat(){
        if(isAuthenticated()) {
            SMGPActiveTestMessage activeTest=new SMGPActiveTestMessage();
            activeTest.setSequenceNumber(SequenceGenerator.nextSequence());
            send(activeTest);
        }
    }

    @Override
    public boolean authenticate() {

        SMGPLoginMessage loginMsg=new SMGPLoginMessage();
        loginMsg.setClientId(connection.getClientId());
        loginMsg.setLoginMode(connection.getLoginMode());
        loginMsg.setVersion(connection.getVersion());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
        String tmp=dateFormat.format(calendar.getTime());
        loginMsg.setTimestamp(Integer.parseInt(tmp));
        loginMsg.setClientAuth(MD5.md5(connection.getClientId(), connection.getPassword(), tmp));
        loginMsg.setSequenceNumber(SequenceGenerator.nextSequence());
        send(loginMsg);
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ex){
                setAuthenticated(false);
            }
        }
        return isAuthenticated();
    }

    @Override
    public void close() throws IOException {
        //保存数据
        if(isAuthenticated() ) {
            SMGPExitMessage exit = new SMGPExitMessage();
            exit.setSequenceNumber(SequenceGenerator.nextSequence());
            send(exit);
            synchronized (lock) {
                try {
                    lock.wait(6000);
                } catch (InterruptedException ex){
                    setAuthenticated(false);
                }
            }
        }
        connection.close();
    }

    @Override
    public void send(Message message){
        connection.send(message);
    }

    @Override
    public void process(Message message) throws IOException {
        if(message instanceof SMGPBaseMessage){
            SMGPBaseMessage baseMsg = (SMGPBaseMessage)message;
            if(isAuthenticated()){
                if (baseMsg instanceof SMGPActiveTestMessage) {
                    process((SMGPActiveTestMessage)baseMsg);
                } else if (baseMsg instanceof SMGPActiveTestRespMessage) {
                    // do nothing
                } else if(baseMsg instanceof SMGPExitRespMessage){
                    process((SMGPExitRespMessage)baseMsg);
                } else if(message instanceof SMGPSubmitRespMessage) {
                    process((SMGPSubmitRespMessage)message);
                } else if(message instanceof SMGPDeliverMessage) {
                    process((SMGPDeliverMessage)message);
                }
            } else if(baseMsg instanceof SMGPLoginRespMessage){
                process((SMGPLoginRespMessage)baseMsg);
            } else {
                throw new IOException("the first packet was not SMGPBindRespMessage:" + baseMsg);
            }
        }
    }

    private void process(SMGPActiveTestMessage msg) throws IOException {
        SMGPActiveTestRespMessage resp = new SMGPActiveTestRespMessage();
        resp.setSequenceNumber(msg.getSequenceNumber());
        send(resp);
    }

    private void process(SMGPLoginRespMessage rsp) throws IOException {
        synchronized (lock) {
            if(rsp.getStatus()==0){
                setAuthenticated(true);
                log.info("smgp login success host=" + connection.getHost() + ",port=" + connection.getPort() + ",clientId=" + connection.getClientId());
            } else {
                setAuthenticated(false);
                log.error("smgp login failure, host=" + connection.getHost() + ",port=" + connection.getPort() + ",clientId=" + connection.getClientId() + ",status=" + rsp.getStatus());
            }
            lock.notifyAll();
        }
    }

    private void process(SMGPExitRespMessage msg) throws IOException {
    	moListener.onTerminate(args);
        synchronized (lock) {
            setAuthenticated(false);
            lock.notifyAll();
        }
        log.info("smgp exist success host=" + connection.getHost() + ",port=" + connection.getPort() + ",clientId=" + connection.getClientId());
    }

    private void process(SMGPSubmitRespMessage rsp) throws IOException {
    	moListener.onSubmitResp(rsp, args);
//        switch (rsp.getStatus())   {
//            case 0:{   //发送成功
//
//            } break;
//            case 103:{  //平台流控,发送速度过快
//
//            } break;
//            default: break;
//        }
    }

    private void process(SMGPDeliverMessage msg) throws IOException {
    	moListener.onDeliver(msg, args);
    	
        SMGPDeliverRespMessage rsp = new SMGPDeliverRespMessage();
        rsp.setSequenceNumber(msg.getSequenceNumber());
        rsp.setMsgId(msg.getMsgId());
        rsp.setStatus(0);
        send(rsp);
    }

    private void setAuthenticated(boolean value) {
        this.authenticated = value;
    }

	public void setMoListener(MoListener moListener) {
		this.moListener = moListener;
	}

	public Map<String, String> getArgs() {
		return args;
	}
	
	public boolean isGatewaySign() {
		return gatewaySign;
	}

	public void init(String id, boolean gatewaySign, Map<String, String> args) {
		this.args = args;
		this.id = id;
		this.gatewaySign = gatewaySign;
	}
	
	public String toString() {
        return new StringBuilder().append("SMGP-gateway-").append(String.valueOf(this.id)).toString();
    }
	
}

