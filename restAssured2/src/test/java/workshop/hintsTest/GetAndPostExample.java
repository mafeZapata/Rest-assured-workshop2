package workshop.hintsTest;

import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;
import io.restassured.*;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
public class GetAndPostExample {

    @Test
    public void testGet() {
        System.out.println("Running test");
        RestAssured.baseURI = "https://reqres.in/api";

        when().
                get("/users?page=2").
                then().
                statusCode(200).
                body("data.size()", is(6)).
                body("data.first_name", hasItems("George", "Rachel"));

    };

    @Test
    public void testPost() {

        JSONObject request = new JSONObject();

        request.put("name", "Ernesto Perez");
        request.put("job", "QA Automation");

        RestAssured.baseURI =  "https://reqres.in/api";

        given().
                header("Content-Type", "application/json").
                contentType(ContentType.JSON).
                body(request.toJSONString()).
                when().
                post("/users").
                then().
                statusCode(201).
                log().all();
        System.out.println(request.toJSONString());
    }
    //Ejercicio pokeapi Get
     @Test
     public void testGetPokemon() {
         RestAssured.baseURI = "https://pokeapi.co/api/v2";
         String pokemonName = "";
         Response response =
                 when().
                         get("/pokemon/" + pokemonName).
                         then().
                         statusCode(200).
                         extract().response();
         System.out.println(response.asPrettyString());
     }


}
