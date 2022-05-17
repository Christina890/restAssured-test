import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class TestCases {

    Resource util = new Resource();
    String token= util.fetchConfigProperties("access_token");
    String baseurl = util.fetchConfigProperties("url");
    String password=util.fetchConfigProperties("password");
    String email=util.fetchConfigProperties("email");
    String username=util.fetchConfigProperties("username");
    String invalidPassword= util.fetchConfigProperties("invalidPassword");

    public TestCases() throws IOException {
    }
    public Response getLoginResponse(){
        Map<String,String> Credentials = new HashMap<String, String>();
        Credentials.put("password", password);
        Credentials.put("email", email);
        Response resp=RestAssured.given().auth().oauth2(token).body(Credentials).contentType("application/json").post(baseurl+ "/Login");
        return resp;
    }

    @Test
    void createUser(){
        Map<String,String> UserDetails = new HashMap<String, String>();
                UserDetails.put("email", email);
                UserDetails.put("password", password);
                UserDetails.put("username", username);
       Response body= RestAssured.given().auth().oauth2(token).body(UserDetails).contentType("application/json").post(baseurl+ "/Create");
        Assert.assertEquals(body.getStatusCode(), 200);
        System.out.println(body.getTime());
    }
  @Test
    void duplicateUserVerification(){
      Map<String,String> UserDetails = new HashMap<String, String>();
      UserDetails.put("email", email);
      UserDetails.put("password", password);
      UserDetails.put("username", username);
      Response body= RestAssured.given().auth().oauth2(token).body(UserDetails).contentType("application/json").post(baseurl + "/Create");
      String error = body.jsonPath().getString("detail");
      Assert.assertEquals(error.toString(), "email already exists");

  }
  @Test
    void successfulLogin(){
        Response body= getLoginResponse();
      String userId= body.jsonPath().getString("session.userId");
      Assert.assertEquals(userId.toString(), userId);
  }

  @Test
    void unauthorisedLogin() {
        Map<String, String> Credentials = new HashMap<String, String>();
        Credentials.put("password", invalidPassword);
        Credentials.put("email", email);
        Response body = RestAssured.given().auth().oauth2(token ).body(Credentials).contentType("application/json").post(baseurl+ "/Login");
        Assert.assertEquals(body.getStatusCode(), 401);
    }

    @Test
    void logout(){
        Response resp= getLoginResponse();
        String sessionId= resp.jsonPath().getString("session.id");
        Map<String, String> session = new HashMap<String, String>();
        session.put("sessionId", sessionId);
        Response body = RestAssured.given().auth().oauth2(token).body(session).contentType("application/json").post(baseurl + "/Logout");
        Assert.assertEquals(body.getStatusCode(), 200);
    }
    @Test (priority = 100)
    void deleteUser(){
        Response resp= getLoginResponse();
        String userId= resp.jsonPath().getString("session.userId");

        Map<String, String> session = new HashMap<String, String>();
        session.put("id", userId);
        Response body = RestAssured.given().auth().oauth2(token).body(session).contentType("application/json").post(baseurl + "/Delete");
        Assert.assertEquals(body.getStatusCode(), 200);
    }


}
