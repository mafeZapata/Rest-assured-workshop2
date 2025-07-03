package workshop;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CommentsTest extends UtilitiesTest{

    @BeforeTest
    public void SetEnvironmentAndAuthenticate() {

        sessionToken = Login();
    }

    @Test
    public void testAddCommentToStudentAndVerifyInList() {

        String baseName = "Carlos";
        String studentName = addRandomLetters(baseName, 5);
        String studentLastname = "López Díaz";

        JSONObject student = new JSONObject();
        student.put("name", studentName);
        student.put("lastname", studentLastname);
        student.put("interests", Arrays.asList("Música", "Dibujo"));

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

        String commentText = "Comentario de prueba";

        JSONObject comment = new JSONObject();
        comment.put("studentId", studentId);
        comment.put("comment", commentText);

        Response commentResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .contentType(ContentType.JSON)
                .body(comment.toJSONString())
                .when()
                .post("/classes/Comments")
                .then()
                .statusCode(201)
                .body("objectId", notNullValue())
                .body("createdAt", notNullValue())
                .extract().response();

        System.out.println("Comentario creado:");
        System.out.println(commentResponse.asPrettyString());


        Response getCommentsResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .when()
                .get("/classes/Comments")
                .then()
                .statusCode(200)
                .body("results.comment", hasItem(commentText))
                .extract().response();

        System.out.println("Comentarios existentes:");
        System.out.println(getCommentsResponse.asPrettyString());
    }

    @Test
    public void testDeleteCommentAndVerifyNotInList() {

        String baseName = "Ana";
        String studentName = addRandomLetters(baseName, 5);
        String studentLastname = "López Díaz";

        JSONObject student = new JSONObject();
        student.put("name", studentName);
        student.put("lastname", studentLastname);
        student.put("interests", Arrays.asList("Arte", "Deporte"));

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
                .extract().response();

        String studentId = studentResponse.jsonPath().getString("objectId");


        JSONObject commentData = new JSONObject();
        commentData.put("studentId", studentId);
        commentData.put("comment", "Comentario para eliminar");

        Response postResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .contentType(ContentType.JSON)
                .body(commentData.toJSONString())
                .when()
                .post("/classes/Comments")
                .then()
                .statusCode(201)
                .extract().response();

        String commentId = postResponse.jsonPath().getString("objectId");


        given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .when()
                .delete("/classes/Comments/" + commentId)
                .then()
                .statusCode(200);


        Response getResponse = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .when()
                .get("/classes/Comments")
                .then()
                .statusCode(200)
                .extract().response();

        List<String> commentIds = getResponse.jsonPath().getList("results.objectId");
        Assert.assertFalse(commentIds.contains(commentId), "El comentario eliminado aún está presente en la lista.");
    }

    @Test
    public void testCommentAssociatedToStudent() {

        String baseName = "Vicky";
        String studentName = addRandomLetters(baseName, 4);
        String studentLastname = "Ramírez Lopez";

        JSONObject student = new JSONObject();
        student.put("name", studentName);
        student.put("lastname", studentLastname);
        student.put("interests", Arrays.asList("Historia", "Ciencia"));

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

        String commentText = "Este estudiante muestra gran interés en historia.";
        JSONObject comment = new JSONObject();
        comment.put("studentId", studentId);
        comment.put("comment", commentText);

        given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .contentType(ContentType.JSON)
                .body(comment.toJSONString())
                .when()
                .post("/classes/Comments")
                .then()
                .statusCode(201)
                .body("objectId", notNullValue());

        Response getStudent = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .header("X-Parse-Session-Token", sessionToken)
                .when()
                .get("/classes/Students/" + studentId)
                .then()
                .statusCode(200)
                .extract().response();

        String responseBody = getStudent.asString();
        System.out.println("Respuesta del estudiante con comentarios:\n" + responseBody);

        List<String> commentsList = getStudent.jsonPath().getList("comments");
        Assert.assertNotNull(commentsList, "El campo 'comments' no está presente.");
        boolean containsComment = commentsList.contains(commentText);
        Assert.assertTrue(containsComment, "El comentario no está asociado al estudiante.");
    }



}
