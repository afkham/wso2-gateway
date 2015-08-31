/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and limitations under the License.
 */

package org.wso2.carbon.gateway.internal.transport.common.disruptor.handler;


import com.lmax.disruptor.EventHandler;
import org.wso2.carbon.gateway.internal.transport.common.disruptor.event.CarbonDisruptorEvent;

public  abstract class DisruptorEventHandler implements EventHandler<CarbonDisruptorEvent> {

    public abstract void onEvent(CarbonDisruptorEvent carbonDisruptorEvent, long l, boolean b) throws Exception;

    public boolean canProcess(int totalNumOfEventHandlers, int handlerId ,  int messageId){
        if(messageId > totalNumOfEventHandlers){
            if(handlerId == messageId % totalNumOfEventHandlers){
                return true;
            }
        }else {
            if(handlerId == totalNumOfEventHandlers % messageId){
                return true;
            }
        }
        return false;
    }
}
