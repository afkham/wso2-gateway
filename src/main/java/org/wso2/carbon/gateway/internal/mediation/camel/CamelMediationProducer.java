/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.wso2.carbon.gateway.internal.mediation.camel;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultAsyncProducer;
import org.apache.camel.util.ObjectHelper;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * The CamelMediation producer handle the request and response with the backend.
 */
public class CamelMediationProducer extends DefaultAsyncProducer {

    private static Logger log = Logger.getLogger(CamelMediationProducer.class);

    private CamelMediationEngine engine;
    private String host;
    private int port;
    private String uri;
    private CarbonCamelMessageUtil carbonCamelMessageUtil;

    public CamelMediationProducer(CamelMediationEndpoint endpoint, CamelMediationEngine engine) {
        super(endpoint);
        this.engine = engine;
        try {
            URL url = new URL(ObjectHelper.after(getEndpoint().getEndpointKey(), "://"));
            host = url.getHost();
            port = url.getPort();
            uri = url.getPath();
            carbonCamelMessageUtil = endpoint.getCarbonCamelMessageUtil();
        } catch (MalformedURLException e) {
            log.error("Could not generate endpoint url for : " + getEndpoint().getEndpointKey());
        }
    }

    public boolean process(Exchange exchange, AsyncCallback callback) {
        //change the header parameters according to the routed endpoint url
        carbonCamelMessageUtil.setCarbonHeadersToBackendRequest(exchange, host, port, uri);
        //setCarbonHeaders(exchange);
        engine.getSender().send(exchange.getIn().getBody(CarbonMessageImpl.class),
                                new NettyHttpBackEndCallback(exchange, callback));
        return false;
    }

    private void setCarbonHeaders(Exchange exchange) {

        CarbonMessageImpl request = (CarbonMessageImpl) exchange.getIn().getBody();
        Map<String, Object> headers = exchange.getIn().getHeaders();

        //TODO change
        if (request != null) {
            request.setHost(host);
            request.setPort(port);
            request.setURI(uri);
            if (port != 80) {
                headers.put("Host", host + ":" + port);
            } else {
                headers.put("Host", host);
            }
            request.setProperty(Constants.TRANSPORT_HEADERS, headers);
        }
    }

    @Override public Endpoint getEndpoint() {
        return super.getEndpoint();
    }

    private class NettyHttpBackEndCallback implements CarbonCallback {
        private final Exchange exchange;
        private final AsyncCallback callback;

        public NettyHttpBackEndCallback(Exchange exchange, AsyncCallback callback) {
            this.exchange = exchange;
            this.callback = callback;
        }

        //This will be called when the backend response arrived
        @Override public void done(CarbonMessage cMsg) {
            if (cMsg != null) {
                Map<String, Object> transportHeaders =
                        (Map<String, Object>) cMsg.getProperty(Constants.TRANSPORT_HEADERS);
                if (transportHeaders != null) {
                    carbonCamelMessageUtil.setCamelHeadersToBackendResponse(exchange, transportHeaders);
                    //exchange.getOut().setHeaders(transportHeaders);
                    exchange.getOut().setBody(cMsg);
                } else {
                    log.warn("Backend response : Received empty headers in carbon message...");
                }
            } else {
                log.warn("Backend response not received for request...");
            }
            callback.done(false);
        }
    }
}
