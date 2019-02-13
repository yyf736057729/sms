package com.zx.sms.codec.smpp.msg;

/*
 * #%L
 * ch-smpp
 * %%
 * Copyright (C) 2009 - 2015 Cloudhopper by Twitter
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.zx.sms.codec.smpp.SmppConstants;

/**
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class BindTransceiver extends BaseBind<BindTransceiverResp> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7788689674672351061L;

	public BindTransceiver() {
        super(SmppConstants.CMD_ID_BIND_TRANSCEIVER, "bind_transceiver");
    }

    @Override
    public BindTransceiverResp createResponse() {
        BindTransceiverResp resp = new BindTransceiverResp();
        resp.setSequenceNumber(this.getSequenceNumber());
        return resp;
    }

    @Override
    public Class<BindTransceiverResp> getResponseClass() {
        return BindTransceiverResp.class;
    }
    
}