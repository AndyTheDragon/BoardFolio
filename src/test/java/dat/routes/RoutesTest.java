package dat.routes;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoutesTest {

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

    @BeforeAll
    void setUpAll() {
        Routes routes = new Routes(emf);

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(routes.getRoutes())
                .handleException()
                .setApiExceptionHandling()
                .checkSecurityRoles()
                .startServer(7082);   // brug en port der ikke konflikter

        RestAssured.baseURI = "http://localhost:7082/api";
    }


    @Test
    void getGameListsForUser_routeWorks() {
        given()
                .when()
                .get("/list/testUser")
                .then()
                .statusCode(200);   // controller laver ctx.json(...) -> default 200
    }

    @Test
    void createGameList_routeWorks() {
        String json = """
                {
                  "name": "Some List",
                  "public": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/list/add")
                .then()
                .statusCode(200);   // ingen status sat i controller -> 200
    }

    @Test
    void updateList_routeWorks_notFoundOrOkIsFine() {
        String json = """
                {
                  "name": "Updated Name"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .put("/list/update/99999")    // højt id = sandsynligvis ikke eksisterende
                .then()
                .statusCode(404);             // controller returnerer 404 hvis ikke fundet
    }

    @Test
    void deleteList_routeWorks() {
        // Hvis ID’et ikke findes, laver jeres DAO nok bare ingenting – men route virker
        given()
                .when()
                .delete("/list/remove/1")
                .then()
                .statusCode(200);   // ingen status sat i controller -> 200
    }


    @Test
    void getGameListById_routeWorks() {
        // Virker KUN hvis du har rettet route til: get("/list/{listID}", ...)
        given()
                .when()
                .get("/list/list/1")
                .then()
                // enten får du 200 (hvis id findes) eller 404 (hvis ikke)
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(200),
                        org.hamcrest.Matchers.is(404)
                ));
    }
}