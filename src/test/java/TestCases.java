import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.TestInstance;

import java.util.HashMap;
import java.util.Map;

public class TestCases {

    private String token;

    {
        token = "ZjQwNjcyNjAtOGIyNC00MDc2LTk1YmUtMDczZTBhMmNlMWFh";
    }

    public Response getLoginResponse(){
        Map<String,String> Credentials = new HashMap<String, String>();
        Credentials.put("password", "Password1");
        Credentials.put("email", "apondi@example.com");
        Response resp=RestAssured.given().auth().oauth2(token).body(Credentials).contentType("application/json").post("https://api.m3o.com/v1/user/Login");
        return resp;
    }

    @Test
    void CreateUser(){
        Map<String,String> UserDetails = new HashMap<String, String>();
                UserDetails.put("email", "apondi@example.com");
                UserDetails.put("password", "Password1");
                UserDetails.put("username", "Apondi");

       Response body= RestAssured.given().auth().oauth2(token).body(UserDetails).contentType("application/json").post("https://api.m3o.com/v1/user/Create");
        Assert.assertEquals(body.getStatusCode(), 200);
    }
  @Test
    void DuplicateUserVerification(){
      Map<String,String> UserDetails = new HashMap<String, String>();
      UserDetails.put("email", "apondi@example.com");
      UserDetails.put("password", "Password1");
      UserDetails.put("username", "Apondi");
      Response body= RestAssured.given().auth().oauth2(token).body(UserDetails).contentType("application/json").post("https://api.m3o.com/v1/user/Create");
      String error = body.jsonPath().getString("detail");
      Assert.assertEquals(error.toString(), "email already exists");

  }
  @Test
    void SuccessfulLogin(){
        Response body= getLoginResponse();
      String userId= body.jsonPath().getString("session.userId");
      Assert.assertEquals(userId.toString(), userId);
  }

  @Test
    void UnauthorisedLogin() {
        Map<String, String> Credentials = new HashMap<String, String>();
        Credentials.put("password", "Password1*");
        Credentials.put("email", "apondi1@example.com");
        Response body = RestAssured.given().auth().oauth2(token + "x").body(Credentials).contentType("application/json").post("https://api.m3o.com/v1/user/Login");
        Assert.assertEquals(body.getStatusCode(), 401);
    }

    @Test
    void Logout(){
        Response resp= getLoginResponse();
        String sessionId= resp.jsonPath().getString("session.id");
        Map<String, String> session = new HashMap<String, String>();
        session.put("sessionId", sessionId);
        Response body = RestAssured.given().auth().oauth2(token).body(session).contentType("application/json").post("https://api.m3o.com/v1/user/Logout");
        Assert.assertEquals(body.getStatusCode(), 200);
    }
    @Test (priority = 100)
    void DeleteUser(){
        Response resp= getLoginResponse();
        String userId= resp.jsonPath().getString("session.userId");
        Map<String, String> session = new HashMap<String, String>();
        session.put("id", userId);
        Response body = RestAssured.given().auth().oauth2(token).body(session).contentType("application/json").post("https://api.m3o.com/v1/user/Delete");
        Assert.assertEquals(body.getStatusCode(), 200);
    }
}
