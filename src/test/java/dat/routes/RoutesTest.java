package dat.routes;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

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
                .startServer(7080);

        RestAssured.baseURI = "http://localhost:7080/api";
    }

    // -------------------------------------
    // GET /list/{username}
    // -------------------------------------
    @Test
    void testGetGameListsForUser() {
        given()
                .when()
                .get("/list/testUser")
                .then()
                .statusCode(anyOf(is(200), is(204))); // endpoint findes og svarer
    }

    // -------------------------------------
    // POST /list/add
    // -------------------------------------
    @Test
    void testCreateGameList() {
        String json = """
                {
                    "name": "Test List",
                    "public": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/list/add")
                .then()
                .statusCode(anyOf(is(200), is(201))); // controller returnerer 200 normalt
    }

    // -------------------------------------
    // PUT /list/update/{listID}
    // -------------------------------------
    @Test
    void testUpdateList() {
        String json = """
                {
                    "name": "Updated List"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .put("/list/update/1")
                .then()
                .statusCode(anyOf(is(200), is(404))); // afhænger af om 1 findes
    }

    // -------------------------------------
    // DELETE /list/remove/{listID}
    // -------------------------------------
    @Test
    void testDeleteList() {
        given()
                .when()
                .delete("/list/remove/1")
                .then()
                .statusCode(anyOf(is(200), is(204))); // controller returnerer 200
    }

    // -------------------------------------
    // GET /list/list/{listID}  (nu FIXED)
    // -------------------------------------
    @Test
    void testGetGameListById() {
        given()
                .when()
                .get("/list/list/1") // rute matcher nu controlleren
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }
}