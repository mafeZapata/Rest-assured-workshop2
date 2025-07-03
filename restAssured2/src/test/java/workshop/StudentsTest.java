package workshop;

import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.restassured.http.ContentType;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class StudentsTest extends UtilitiesTest {
    String createdStudentId;

    @BeforeTest
    public void SetEnvironmentAndAuthenticate() {

        setEnvironmentParameters();
        sessionToken = Login();
    }

    @Test
    public void testCreateStudentAndVerify() {

        String Name = "JuanManuel";
        String randomLetters = addRandomLetters(Name,5);
        String studentName = randomLetters ;

        JSONObject student = new JSONObject();
        student.put("name", studentName);
        student.put("lastname", "Pérez Bolaños");

        List<String> interests = Arrays.asList("Fútbol", "Rumiquiu", "Biking", "Cars", "Pets");
        student.put("interests", interests);


        Response postResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .contentType(ContentType.JSON)
                .body(student.toJSONString())
                .when()
                .post("/classes/Students")
                .then()
                .statusCode(201)
                .body("objectId", notNullValue())
                .extract().response();

        createdStudentId = postResponse.jsonPath().getString("objectId");
        System.out.println("Estudiante creado con ID: " + createdStudentId);


        Response getResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .when()
                .get("/classes/Students/" + createdStudentId)
                .then()
                .statusCode(200)
                .body("name", equalTo(studentName))
                .body("lastname", equalTo("Pérez Bolaños"))
                .body("interests", hasItems("Fútbol", "Rumiquiu", "Biking", "Cars", "Pets"))
                .extract().response();

        System.out.println("Consulta del estudiante individual:");
        System.out.println(getResponse.asPrettyString());


        Response listResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .when()
                .get("/classes/Students")
                .then()
                .statusCode(200)
                .body("results", not(empty()))
                .body("results.objectId", hasItem(createdStudentId))
                .extract().response();

        System.out.println("Listado de estudiantes:");
        System.out.println(listResponse.asPrettyString());
    }

    @Test
    public void testDuplicateStudentCreationNotAllowed() {
        String studentName = "clauditaunodostrescuatro";
        String studentLastname = "Pérez Bolaños";

        JSONObject duplicateStudent = new JSONObject();
        duplicateStudent.put("name", studentName);
        duplicateStudent.put("lastname", studentLastname);
        duplicateStudent.put("interests", Arrays.asList("Fútbol", "Rumiquiu"));

        Response duplicateResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .contentType(ContentType.JSON)
                .body(duplicateStudent.toJSONString())
                .when()
                .post("/classes/Students")
                .then()
                .statusCode(400)
                .body("error", containsString("A duplicate value for a field with unique values was provided"))
                .extract().response();

        System.out.println("Respuesta al intentar crear duplicado:");
        System.out.println(duplicateResponse.asPrettyString());
    }

    @Test
    public void testSpecialCharactersNotAllowedInName() {
        String invalidName = "J@ne#Doe!";
        String studentLastname = "Pérez Bolaños";

        JSONObject invalidStudent = new JSONObject();
        invalidStudent.put("name", invalidName);
        invalidStudent.put("lastname", studentLastname);
        invalidStudent.put("interests", Arrays.asList("Fútbol", "Ajedrez"));

        Response invalidResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .contentType(ContentType.JSON)
                .body(invalidStudent.toJSONString())
                .when()
                .post("/classes/Students")
                .then()
                .statusCode(400)
                .extract().response();

        System.out.println("Respuesta al enviar nombre con caracteres especiales:");
        System.out.println(invalidResponse.asPrettyString());
    }

    @Test
    public void testUpdateStudentInterests() {

        String Name = "Elena";
        String randomLetters = addRandomLetters(Name, 5);
        String studentName = randomLetters;
        String studentLastname = "La Ballena";


        JSONObject newStudent = new JSONObject();
        newStudent.put("name", studentName);
        newStudent.put("lastname", studentLastname);
        newStudent.put("interests", Arrays.asList("Natación", "Pintura"));

        Response createResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .contentType(ContentType.JSON)
                .body(newStudent.toJSONString())
                .when()
                .post("/classes/Students")
                .then()
                .statusCode(201)
                .body("objectId", notNullValue())
                .extract().response();

        String studentId = createResponse.jsonPath().getString("objectId");
        System.out.println("Estudiante creado con ID: " + studentId);


        List<String> newInterests = Arrays.asList("Astronomía", "Programación", "Lectura");


        JSONObject updatedData = new JSONObject();
        updatedData.put("name", studentName);
        updatedData.put("lastname", studentLastname);
        updatedData.put("interests", newInterests);

        Response updateResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .contentType(ContentType.JSON)
                .body(updatedData.toJSONString())
                .when()
                .put("/classes/Students/" + studentId)
                .then()
                .statusCode(200)
                .body("updatedAt", notNullValue())
                .extract().response();

        System.out.println("Respuesta de actualización:");
        System.out.println(updateResponse.asPrettyString());


        Response getUpdatedResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .when()
                .get("/classes/Students/" + studentId)
                .then()
                .statusCode(200)
                .body("name", equalTo(studentName))
                .body("lastname", equalTo(studentLastname))
                .body("interests", equalTo(newInterests))
                .extract().response();

        System.out.println("Datos después de la actualización:");
        System.out.println(getUpdatedResponse.asPrettyString());
    }

    @Test
    public void testCreateStudentWithoutAuthentication() {
        String Name = "Ana";
        String randomLetters = addRandomLetters(Name, 6);
        String studentName = randomLetters;

        JSONObject student = new JSONObject();
        student.put("name", studentName);
        student.put("lastname", "Ramírez Díaz");
        student.put("interests", Arrays.asList("Cine", "Música", "Viajar"));

        Response unauthenticatedResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                // NOT including "X-Parse-Session-Token"
                .contentType(ContentType.JSON)
                .body(student.toJSONString())
                .when()
                .post("/classes/Students")
                .then()
                .statusCode(404)
                .body("error", containsString("Permission denied, user needs to be authenticated"))
                .extract().response();

        System.out.println("Respuesta sin autenticación:");
        System.out.println(unauthenticatedResponse.asPrettyString());
    }


}
