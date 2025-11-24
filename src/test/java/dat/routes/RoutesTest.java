package dat.routes;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.entities.GameList;
import dat.entities.UserAccount;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoutesTest {

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

    @BeforeAll
    void startServer() {

        Routes routes = new Routes(emf);

        ApplicationConfig.getInstance()
                .initiateServer()
                .setRoute(routes.getRoutes())
                .handleException()
                .setApiExceptionHandling()
                .checkSecurityRoles()
                .startServer(7082);

        RestAssured.baseURI = "http://localhost:7082/api";
    }

    @BeforeEach
    void seedFreshData() {
        try (EntityManager em = emf.createEntityManager()) {

            em.getTransaction().begin();

            // Ryd databasen før hver test
            em.createQuery("DELETE FROM GameList").executeUpdate();
            em.createQuery("DELETE FROM UserAccount").executeUpdate();

            // Opret en testbruger
            UserAccount user = new UserAccount("testUser", "1234");
            user.setMyCollection(null);

            // Opret en default liste brugeren ejer
            GameList list = new GameList("Oprettet Liste");
            list.setUser(user);
            user.getGameLists().add(list);

            em.persist(user);
            em.persist(list);

            em.getTransaction().commit();
        }
    }

    // -------------------------------------------------------------------------
    // TEST — GET /list/user/{username}
    // -------------------------------------------------------------------------
    @Test
    void getGameListsForUser_realDataTest() {
        given()
                .when()
                // ÆNDRING: tilføj "/list" foran /user
                .get("/list/user/testUser")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].name", equalTo("Oprettet Liste"));
    }

    // -------------------------------------------------------------------------
    // TEST — POST /list/add
    // -------------------------------------------------------------------------
    @Test
    void createGameList_realDataTest() {

        String json = """
                {
                  "name": "Ny Liste",
                  "public": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/list/add")
                .then()
                .statusCode(200);

        // Validér at der nu findes 2 lister
        try (EntityManager em = emf.createEntityManager()) {
            long count = em.createQuery("SELECT COUNT(gl) FROM GameList gl", Long.class)
                    .getSingleResult();
            Assertions.assertEquals(2, count);
        }
    }

    // -------------------------------------------------------------------------
    // TEST — PUT /list/update/{id}
    // -------------------------------------------------------------------------
    @Test
    void updateList_realDataTest() {

        int id = getDefaultListId();

        String json = """
                {
                  "name": "Opdateret Liste",
                  "public": false,
                  "customList": []
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .put("/list/update/" + id)
                .then()
                .statusCode(200)
                .body(containsString("Game list updated"));

        // Validér databasen
        try (EntityManager em = emf.createEntityManager()) {
            GameList gl = em.find(GameList.class, id);
            Assertions.assertEquals("Opdateret Liste", gl.getName());
        }
    }

    @Test
    void updateList_notFound_test() {

        String json = """
                { "name" : "Ignored" }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .put("/list/update/99999")
                .then()
                .statusCode(404)
                .body("message", equalTo("Game list not found"));
    }

    // -------------------------------------------------------------------------
    // TEST — DELETE /list/remove/{id}
    // -------------------------------------------------------------------------
    @Test
    void deleteList_realDataTest() {

        int id = getDefaultListId();

        given()
                .when()
                .delete("/list/remove/" + id)
                .then()
                .statusCode(200);

        try (EntityManager em = emf.createEntityManager()) {
            Assertions.assertNull(em.find(GameList.class, id));
        }
    }

    // -------------------------------------------------------------------------
    // TEST — GET /list/list/{id}
    // -------------------------------------------------------------------------
    @Test
    void getGameListById_realDataTest() {

        int id = getDefaultListId();

        given()
                .when()
                // ÆNDRING: route er path("list", ...) + get("/list/{listID}")
                .get("/list/list/" + id)
                .then()
                .statusCode(200)
                .body("listID", equalTo(id))
                .body("name", equalTo("Oprettet Liste"));
    }

    @Test
    void getGameListById_notFound() {
        given()
                .when()
                // samme ændring her
                .get("/list/list/999999")
                .then()
                .statusCode(404);
    }

    // Hjælpemetode – find ID på listen "Oprettet Liste"
    private int getDefaultListId() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT gl.listID FROM GameList gl WHERE gl.name='Oprettet Liste'",
                            Integer.class)
                    .getSingleResult();
        }
    }
}