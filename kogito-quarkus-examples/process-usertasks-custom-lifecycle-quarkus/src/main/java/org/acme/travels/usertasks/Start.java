/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.acme.travels.usertasks;

import java.util.Arrays;
import java.util.List;

import org.jbpm.process.instance.impl.humantask.phases.Claim;
import org.jbpm.process.instance.impl.humantask.phases.Release;
import org.jbpm.process.instance.impl.workitem.Active;
import org.kie.kogito.process.workitem.LifeCyclePhase;

/**
 * Start life cycle phase that applies to any human task.
 * It will set the status to "Started"
 *
 * It can transition from
 * <ul>
 * <li>Active</li>
 * <li>Claim</li>
 * <li>Release</li>
 * </ul>
 * 
 */
public class Start implements LifeCyclePhase {

    public static final String ID = "start";
    public static final String STATUS = "Started";

    private List<String> allowedTransitions = Arrays.asList(Active.ID, Claim.ID, Release.ID);

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String status() {
        return STATUS;
    }

    @Override
    public boolean isTerminating() {
        return false;
    }

    @Override
    public boolean canTransition(LifeCyclePhase phase) {
        return allowedTransitions.contains(phase.id());
    }
}
