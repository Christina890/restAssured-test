import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class TestCases {

    private String token;
    private String sessionId;
    {
        token = "YWExMjJkNTQtODU1ZC00YTZlLTk2YTEtYzAzNzY0N2VkZjE4";
    }

    public Response login(){
        Map<String,String> Credentials = new HashMap<String, String>();
        Credentials.put("password", "Password1*");
        Credentials.put("email", "apondi1@example.com");
        Response resp=RestAssured.given().auth().oauth2(token).body(Credentials).contentType("application/json").post("https://api.m3o.com/v1/user/Login");
        return resp;
    }

    public String getSessionId(){
        Response response = login();
        sessionId = response.jsonPath().get("session.id");
        return sessionId;

    }

    @Test
    void CreateUser(){
        Map<String,String> UserDetails = new HashMap<String, String>();
                UserDetails.put("email", "apondi2@example.com");
                //UserDetails.put("id", "user-3");
                UserDetails.put("password", "Password1*");
                UserDetails.put("username", "Apondi2");

       Response body= RestAssured.given().auth().oauth2(token).body(UserDetails).contentType("application/json").post("https://api.m3o.com/v1/user/Create");
        Assert.assertEquals(body.getStatusCode(), 200);
    }
  @Test
    void DuplicateUserVerification(){
      Map<String,String> UserDetails = new HashMap<String, String>();
      UserDetails.put("email", "apondi1@example.com");
      UserDetails.put("id", "user-3");
      UserDetails.put("password", "Password1*");
      UserDetails.put("username", "Apondi1");

      Response body= RestAssured.given().auth().oauth2(token).body(UserDetails).contentType("application/json").post("https://api.m3o.com/v1/user/Create");
      Assert.assertEquals(body.getStatusCode(), 400);

  }
  @Test
    void SuccessfulLogin(){
        Response body= login();
      String userId= body.jsonPath().getString("session.userId");
      Assert.assertEquals(userId.toString(), "user-3");
  }

  @Test
    void UnauthorisedSession() {
        Map<String, String> Credentials = new HashMap<String, String>();
        Credentials.put("password", "Password1*");
        Credentials.put("email", "apondi1@example.com");
        Response body = RestAssured.given().auth().oauth2(token + "x").body(Credentials).contentType("application/json").post("https://api.m3o.com/v1/user/Login");
        Assert.assertEquals(body.getStatusCode(), 401);

    }

    @Test
    void SuccessfulLogout(){
        Map<String,String> session = new HashMap<String,String>();{
            session.put("sessionId",sessionId);
        }
    }


}
