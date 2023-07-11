package org.example;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static util.Utilities.*;

public class WireMockTest {

    WireMockServer wireMockServer = new WireMockServer(options().port(8089)
            .extensions(new ResponseTemplateTransformer(true)));


    @BeforeTest
    public void setUp(){
        wireMockConfig().usingFilesUnderDirectory(System.getProperty("user.dir") + "/src/test/resources");
        wireMockServer.start();
    }

    @Test
    public void testLivePassThroughMock() throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException, CertificateException{

        System.out.println(System.getProperty("user.dir"));
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.useRelaxedHTTPSValidation();

        String requestBody = getRequestBody();

        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        Response response;


        int responseCode;

        try {
            response = request.body(requestBody)
                    .post("/api/mytest/User2")
                    .then().extract().response();
            System.out.println("Fetching Live response");
            responseCode = response.then().extract().statusCode();
            assertThat(responseCode, equalTo(200));
        }
        catch(Exception e){
            System.out.println("Fetching mocked response");
            RestAssured.baseURI = "http://localhost:8089";
            request = RestAssured.given();
            response = request.body(requestBody)
                    .post("/api/mytest/User2")
                    .then().extract().response();

            responseCode = response.then().extract().statusCode();
            assertThat(responseCode, equalTo(200));
        }

        System.out.println(response.asString());
    }



    @AfterTest
    public void tearDown(){
        wireMockServer.shutdown();
    }
}
