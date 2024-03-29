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
package org.acme.travels;

import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.kie.kogito.testcontainers.quarkus.KeycloakQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
@QuarkusTestResource(KeycloakQuarkusTestResource.class)
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class ApprovalsRestIT {

    @QuarkusTestProperty(name = "quarkus.oidc.auth-server-url")
    private String keycloakUrl;

    @Test
    public void testStartApprovalUnauthorized() {

        given()
                .body("{\"traveller\" : {\"firstName\" : \"John\",\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\",\"nationality\" : \"American\",\"address\" : {\"street\" : \"main street\",\"city\" : \"Boston\",\"zipCode\" : \"10005\",\"country\" : \"US\"}}}")
                .contentType(ContentType.JSON)
                .when()
                .post("/approvals")
                .then()
                .statusCode(401);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testStartApprovalAuthorized() {

        // start new approval
        String id = given()
                .auth().oauth2(getAccessToken("mary"))
                .body("{\"traveller\" : {\"firstName\" : \"John\",\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\",\"nationality\" : \"American\",\"address\" : {\"street\" : \"main street\",\"city\" : \"Boston\",\"zipCode\" : \"10005\",\"country\" : \"US\"}}}")
                .contentType(ContentType.JSON)
                .when()
                .post("/approvals")
                .then()
                .statusCode(201)
                .body("id", notNullValue()).extract().path("id");
        // get all active approvals
        given()
                .auth().oauth2(getAccessToken("mary"))
                .accept(ContentType.JSON)
                .when()
                .get("/approvals")
                .then()
                .statusCode(200)
                .body("size()", is(1), "[0].id", is(id));

        // get just started approval
        given()
                .auth().oauth2(getAccessToken("mary"))
                .accept(ContentType.JSON)
                .when()
                .get("/approvals/" + id)
                .then()
                .statusCode(200)
                .body("id", is(id));

        // tasks assigned in just started approval

        String taskInfo = given()
                .auth()
                .oauth2(getAccessToken("mary"))
                .accept(ContentType.JSON)
                .when()
                .get("/approvals/" + id + "/tasks?user=admin&group=managers")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].name", is("firstLineApproval"))
                .extract()
                .path("[0].id");

        String payload = "{}";
        given()
                .auth().oauth2(getAccessToken("mary"))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .post("/approvals/" + id + "/firstLineApproval/" + taskInfo + "?user=mary&group=managers")
                .then()
                .statusCode(200)
                .body("id", is(id));

        // lastly abort the approval
        given()
                .auth().oauth2(getAccessToken("mary"))
                .accept(ContentType.JSON)
                .when()
                .delete("/approvals/" + id)
                .then()
                .statusCode(200)
                .body("id", is(id));
    }

    private String getAccessToken(String userName) {
        return given()
                .param("grant_type", "password")
                .param("username", userName)
                .param("password", userName)
                .param("client_id", "kogito-app")
                .param("client_secret", "secret")
                .when()
                .post(keycloakUrl + "/protocol/openid-connect/token")
                .as(AccessTokenResponse.class).getToken();
    }
}
