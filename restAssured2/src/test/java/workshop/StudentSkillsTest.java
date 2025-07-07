package workshop;

import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.restassured.http.ContentType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class StudentSkillsTest extends UtilitiesTest{

    @BeforeSuite
    public void SetEnvironmentAndAuthenticate() {

        setEnvironmentParameters();
        sessionToken = Login();
    }
    @Test
    public void testAddSkillToStudentAndVerify() {
        String baseName = "Louis";
        String studentName = addRandomLetters(baseName, 5);
        String studentLastname = "Martínez Rivera";

        JSONObject student = new JSONObject();
        student.put("name", studentName);
        student.put("lastname", studentLastname);
        student.put("interests", Arrays.asList("Lectura", "Tecnología"));

        Response studentResponse = given()
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

        String studentId = studentResponse.jsonPath().getString("objectId");
        System.out.println("Estudiante creado con ID: " + studentId);

        String existingSkillId = "Z3NyhPqOQT";

        JSONObject studentSkill = new JSONObject();
        studentSkill.put("studentId", studentId);
        studentSkill.put("skillId", existingSkillId);

        System.out.println("Cuerpo enviado a /classes/StudentSkills:");
        System.out.println(studentSkill.toJSONString());

        Response skillResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .contentType(ContentType.JSON)
                .body(studentSkill.toJSONString())
                .when()
                .post("/classes/StudentSkills")
                .then()
                .statusCode(201)
                .body("objectId", notNullValue())
                .body("createdAt", notNullValue())
                .extract().response();

        System.out.println("Respuesta al asociar habilidad:");
        System.out.println(skillResponse.asPrettyString());

        Response getStudentResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .when()
                .get("/classes/Students/" + studentId)
                .then()
                .statusCode(200)
                .body("skillNames", notNullValue())
                .extract().response();

        System.out.println("Estudiante con habilidades asociadas:");
        System.out.println(getStudentResponse.asPrettyString());
    }

    @Test
    public void testDuplicateSkillAdditionToStudent() {
        String baseName = "Luciana";
        String studentName = addRandomLetters(baseName, 4);
        String studentLastname = "Pérez Gómez";

        JSONObject student = new JSONObject();
        student.put("name", studentName);
        student.put("lastname", studentLastname);
        student.put("interests", Arrays.asList("Arte", "Programación"));

        Response studentResponse = given()
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

        String studentId = studentResponse.jsonPath().getString("objectId");
        String existingSkillId = "Z3NyhPqOQT";

        JSONObject skillAssociation = new JSONObject();
        skillAssociation.put("studentId", studentId);
        skillAssociation.put("skillId", existingSkillId);

        Response firstAssociation = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .contentType(ContentType.JSON)
                .body(skillAssociation.toJSONString())
                .when()
                .post("/classes/StudentSkills")
                .then()
                .statusCode(201)
                .body("objectId", notNullValue())
                .body("createdAt", notNullValue())
                .extract().response();

        System.out.println("Primera asociación realizada correctamente:");
        System.out.println(firstAssociation.asPrettyString());

        Response duplicateResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .contentType(ContentType.JSON)
                .body(skillAssociation.toJSONString())
                .when()
                .post("/classes/StudentSkills")
                .then()
                .statusCode(400)
                .body("error", containsString("A duplicate value for a field with unique values was provided"))
                .extract().response();

        System.out.println("Respuesta al intentar asociar habilidad duplicada:");
        System.out.println(duplicateResponse.asPrettyString());
    }

    @Test
    public void testAssociateSkillWithoutAuthentication() {

        String studentId = "TCqIFrGkDF";
        String skillId = "Z3NyhPqOQT";

        JSONObject requestBody = new JSONObject();
        requestBody.put("studentId", studentId);
        requestBody.put("skillId", skillId);

        Response response = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .contentType(ContentType.JSON)
                .body(requestBody.toJSONString())
                .when()
                .post("/classes/StudentSkills")
                .then()
                .statusCode(404)
                .extract().response();

        System.out.println("Respuesta sin autenticación:");
        System.out.println(response.asPrettyString());

        String errorMessage = response.jsonPath().getString("error");
        Assert.assertTrue(errorMessage.toLowerCase().contains("autenticado") ||
                        errorMessage.toLowerCase().contains("auth"),
                "El mensaje de error no indica que se requiere autenticación.");
    }



}
