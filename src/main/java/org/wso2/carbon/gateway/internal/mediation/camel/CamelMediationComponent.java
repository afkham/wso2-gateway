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

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

/**
 * Represents the component that manages {@link CamelMediationEndpoint}.
 */
public class CamelMediationComponent extends DefaultComponent {

    CamelMediationEngine engine;

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new CamelMediationEndpoint(uri, this, engine);
        setProperties(endpoint, parameters);
        return endpoint;
    }

    //when the camel component (wso2-gw) starts this method will be called initially
    @Override protected void doStart() throws Exception {
        super.doStart();
        //start netty transport from here get a CamelMediationEngine object and set it to engine.
        //this.engine = (CamelMediationEngine) new POCController().startPOCController();
    }
}