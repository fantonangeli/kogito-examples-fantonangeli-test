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
package org.acme.travels.springboot;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.tests.KogitoInfinispanSpringbootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Disabled("depends on https://petstore.swagger.io existing users")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoInfinispanSpringbootApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class UsersProcessTest {

    @Autowired
    @Qualifier("users")
    Process<? extends Model> usersProcess;

    @Test
    public void testExistingUser() {

        assertNotNull(usersProcess);

        Model m = usersProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", "test");
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = usersProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
        Model result = (Model) processInstance.variables();
        assertEquals(2, result.toMap().size());

        User user = (User) result.toMap().get("traveller");
        assertNotNull(user);

        assertEquals("test", user.getUsername());
        assertEquals("Test", user.getFirstName());
        assertEquals("Test", user.getLastName());
        assertEquals("test@test.com", user.getEmail());
    }

    @Test
    public void testNotExistingUser() {

        assertNotNull(usersProcess);

        Model m = usersProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", "notexisting");
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = usersProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
        Model result = (Model) processInstance.variables();
        assertEquals(2, result.toMap().size());

        User user = (User) result.toMap().get("traveller");
        assertNull(user);
    }
}
