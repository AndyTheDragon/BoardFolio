package dat.routes;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.entities.GameList;
import dat.entities.UserAccount;
import dat.enums.Roles;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoutesTest {

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private final Logger logger = LoggerFactory.getLogger(RoutesTest.class.getName());

    private final String TEST_USER = "testuser";
    private Integer existingListId; // ID på listen vi opretter i @BeforeEach

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
                .startServer(7081); // anden port end security-testen, fx 7081

        RestAssured.baseURI = "http://localhost:7081/api";
    }

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // SLET rækkefølge: GameList først, så UserAccount (pga. FK user_id)
            em.createQuery("DELETE FROM GameList").executeUpdate();
            em.createQuery("DELETE FROM UserAccount").executeUpdate();

            em.getTransaction().commit();

            // Opret User + én GameList
            em.getTransaction().begin();

            UserAccount user = new UserAccount(TEST_USER, "password123");
            user.addRole(Roles.USER);

            GameList list = new GameList("My Test List");
            list.setPublic(true);
            list.setCreatedDate(LocalDateTime.now());
            list.setUser(user);
            user.getGameLists().add(list);

            em.persist(user); // cascader til GameList gennem relationen
            em.getTransaction().commit();

            // Gem ID til brug i tests
            existingListId = list.getListID();
            logger.info("Oprettet GameList med ID: {}", existingListId);
        }
    }

    // --------------------------------------------------
    // GET /list/{username}
    // -> skal returnere listen for brugeren
    // --------------------------------------------------
    @Test
    void getGameListsForUser_returnsListForUser() {
        given()
                .when()
                .get("/list/" + TEST_USER)
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].name", equalTo("My Test List"))
                .body("[0].listID", equalTo(existingListId));
    }

    // --------------------------------------------------
    // GET /list/list/{listID}
    // -> GameController.getGameListById, returnerer DTO
    // --------------------------------------------------
    @Test
    void getGameListById_returnsCorrectData() {
        given()
                .when()
                .get("/list/list/" + existingListId)
                .then()
                .statusCode(200)
                .body("listID", equalTo(existingListId))
                .body("name", equalTo("My Test List"))
                .body("public", is(true));
    }

    // --------------------------------------------------
    // POST /list/add
    // -> createGameList, skal skabe ny GameList i DB
    // --------------------------------------------------
    @Test
    void createGameList_createsNewRowInDatabase() {
        int beforeCount;
        try (EntityManager em = emf.createEntityManager()) {
            beforeCount = em.createQuery("SELECT COUNT(gl) FROM GameList gl", Long.class)
                    .getSingleResult().intValue();
        }

        String json = """
                {
                    "name": "New List",
                    "public": false
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/list/add")
                .then()
                .statusCode(200); // controller sætter ingen status -> default 200

        int afterCount;
        try (EntityManager em = emf.createEntityManager()) {
            afterCount = em.createQuery("SELECT COUNT(gl) FROM GameList gl", Long.class)
                    .getSingleResult().intValue();
        }

        assertEquals(beforeCount + 1, afterCount,
                "Der burde være én GameList mere efter POST /list/add");
    }

    // --------------------------------------------------
    // PUT /list/update/{listID}
    // -> updateList, skal ændre navn/public i DB
    // --------------------------------------------------
    @Test
    void updateList_changesNameInDatabase() {
        String json = """
                {
                    "name": "Updated Name",
                    "public": false
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .put("/list/update/" + existingListId)
                .then()
                .statusCode(200)
                .body(containsString("Game list updated"));

        // Tjek i databasen at navnet ER opdateret
        try (EntityManager em = emf.createEntityManager()) {
            GameList updated = em.find(GameList.class, existingListId);
            assertEquals("Updated Name", updated.getName());
            Assertions.assertFalse(updated.isPublic());
        }
    }

    // --------------------------------------------------
    // DELETE /list/remove/{listID}
    // -> deleteUserList, skal slette rækken
    // --------------------------------------------------
    @Test
    void deleteList_removesRowFromDatabase() {
        int beforeCount;
        try (EntityManager em = emf.createEntityManager()) {
            beforeCount = em.createQuery("SELECT COUNT(gl) FROM GameList gl", Long.class)
                    .getSingleResult().intValue();
        }

        given()
                .when()
                .delete("/list/remove/" + existingListId)
                .then()
                .statusCode(200); // controller sætter ingen status -> 200

        int afterCount;
        try (EntityManager em = emf.createEntityManager()) {
            afterCount = em.createQuery("SELECT COUNT(gl) FROM GameList gl", Long.class)
                    .getSingleResult().intValue();
        }

        assertEquals(beforeCount - 1, afterCount,
                "Der burde være én GameList mindre efter DELETE /list/remove/{id}");
    }
}