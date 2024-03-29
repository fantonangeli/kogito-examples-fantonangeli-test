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
package org.acme.travel;

import java.io.IOException;

import org.kie.kogito.event.EventMarshaller;
import org.kie.kogito.event.EventUnmarshaller;
import org.kie.kogito.event.avro.AvroEventMarshaller;
import org.kie.kogito.event.avro.AvroEventUnmarshaller;
import org.kie.kogito.event.avro.AvroIO;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class AvroMarshallerProducer {

    private AvroIO avroUtils;

    @PostConstruct
    void init() throws IOException {
        avroUtils = new AvroIO();
    }

    @Produces
    EventMarshaller<byte[]> getAvroMarshaller() {
        return new AvroEventMarshaller(avroUtils);
    }

    @Produces
    EventUnmarshaller<byte[]> getAvroUnmarshaller() {
        return new AvroEventUnmarshaller(avroUtils);
    }

    // publish as bean for testing
    @Produces
    AvroIO getAvroUtils() {
        return avroUtils;
    }

}
