
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.jayway.jsonpath.DocumentContext;
import io.restassured.RestAssured;
import com.jayway.jsonpath.*;


import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import io.restassured.specification.ProxySpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

public class RestAssuredTest {

    WireMockServer wireMockServer = new WireMockServer(options().port(8089)
            .extensions(new ResponseTemplateTransformer(true)));


    @BeforeTest
    public void setUp(){
        wireMockConfig().usingFilesUnderDirectory(System.getProperty("use.dir") + "/src/test/resources");
        wireMockServer.start();
    }
//        wireMockServer.shutdown();

    @Test
    public void tesMock() throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException, CertificateException{
        System.out.println(System.getProperty("user.dir"));
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
        RequestSpecification request = RestAssured.given();

//        KeyStore keystore = null;
//        SSLConfig config;
//        keystore = KeyStore.getInstance("pkcs12");
//        keystore.load(new FileInputStream(""),"abc".toCharArray());
//        SSLSocketFactory ssl= new SSLSocketFactory(keystore,"abc");
//
//        config = new SSLConfig().with().sslSocketFactory(ssl).and().allowAllHostnames();
//        RestAssured.config = RestAssured.config().sslConfig(config);
//        RestAssured.given().when().get("/path").then();

        request.header("Content-Type", "application/json");
        Response response = request.body("{ \"userName\":\"TOOLSQA-Test\", \"password\":\"Test@@123\"}")
                            .post("/Account/v1/GenerateToken")
                            .then().extract().response();

//
//        Response response1 = request.multiPart(new File(""))
//                .post("/uploadFile")
//                .then().extract().response();

        String jsonString = response.asString();
        DocumentContext docCtx = JsonPath.parse(jsonString);
        String token = docCtx.read("$..token").toString();

        System.out.println("Token:"+token);

        docCtx = JsonPath.parse(jsonString).set("$..token","abc");
        jsonString = docCtx.jsonString();

        System.out.println(jsonString);
//.proxy(new ProxySpecification("localhost",8080,"http").withAuth("",""))
        Response responseText = request.get("/BookStore/v1/Books");
        int  responseCode = responseText.then().assertThat().body("books",hasSize(8))
                .assertThat().body("books[0].isbn",equalTo("9781449325862")).extract().statusCode();

        assertThat(io.restassured.path.json.JsonPath.from(responseText.asString()).get("books[0].isbn"), Matchers.<Object>equalTo("9781449325862"));
        Map<String,Object> validationMap = new HashMap();
        validationMap.put("responseCode",200);
        for(HashMap.Entry<String,Object> set: validationMap.entrySet() ){
            responseText.then().assertThat().statusCode(Integer.parseInt(validationMap.get("responseCode").toString()));
        }

        try {
            assertThat(responseCode, equalTo(200));
        }
        catch(AssertionError e){
            System.out.println(e.toString());
        }



        jsonString = responseText.asString();
        docCtx = JsonPath.parse(jsonString);
        String bookId = docCtx.read("$..books[0].isbn").toString();

        System.out.println("responseText:"+bookId);

    }

    @AfterTest
    public void tearDown(){
        wireMockServer.shutdown();
    }
}
