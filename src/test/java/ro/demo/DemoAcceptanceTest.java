package ro.demo;

/**
 * Created by sconstantinescu on 09.06.2016.
 */

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.config.JsonPathConfig;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.PostConstruct;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestApplication.class})
@WebAppConfiguration
@IntegrationTest({
        "spring.config.location=classpath:/test.yml,file:test.properties",
        "server.port=0", "management.port=0", "flyway.enabled=false",
        "spring.cloud.config.enabled=false", "spring.cloud.discovery.enabled=false",
        "spring.cloud.consul.enabled=false", "spring.cloud.consul.config.watch.enabled=false"
})
public class DemoAcceptanceTest {

    @Value("${local.server.port}")
    protected int port;

    @PostConstruct
    public void startup(){
        RestAssured.port=port;
    }

    @Test
    public void testGetPersonSuccess() throws ParseException {
        //String dateStr=
        given().when().get("/person/178").then()
                .statusCode(HttpStatus.OK.value())
                .body("cnp", Matchers.equalTo("178"));
//                .extract().path("date");

//        Date date=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateStr);
//        Assert.assertTrue("older date expected",new Date().after(date));
//        WireMockServer mock=new WireMockServer(9991);
//        mock.start();
//        WireMock.configureFor(9991);
//        String jsonBody="";
//        WireMock.stubFor(WireMock.get(urlEqualTo("testip")).withHeader("Content-Type", containing(MediaType.APPLICATION_JSON_VALUE))
//                .withRequestBody(containing("180")).willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody(jsonBody)));
//
//        given().when().get("/ip").then()
//                .statusCode(HttpStatus.OK.value())
//                .body("cnp", Matchers.equalTo("178"));
//
    }

}