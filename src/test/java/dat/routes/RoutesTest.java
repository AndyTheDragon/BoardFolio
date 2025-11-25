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
class RoutesTest
{

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

    @BeforeAll
    void startServer()
    {

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
    void StartFreshData()
    {
        try (EntityManager em = emf.createEntityManager())
        {

            em.getTransaction().begin();

            em.createQuery("DELETE FROM GameList").executeUpdate();
            em.createQuery("DELETE FROM UserAccount").executeUpdate();

            UserAccount user = new UserAccount("testUser", "1234");
            user.setMyCollection(null);

            GameList list = new GameList("Oprettet Liste");
            list.setUser(user);
            user.getGameLists().add(list);

            em.persist(user);
            em.persist(list);

            em.getTransaction().commit();
        }
    }

    @Test
    void getGameListsForUser_realDataTest()
    {
        given()
                .when()
                .get("/list/user/testUser")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].name", equalTo("Oprettet Liste"));
    }

    @Test
    void createGameList()
    {

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

        try (EntityManager em = emf.createEntityManager())
        {
            long count = em.createQuery("SELECT COUNT(gl) FROM GameList gl", Long.class)
                           .getSingleResult();
            Assertions.assertEquals(2, count);
        }
    }


    @Test
    void updateList()
    {

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

        try (EntityManager em = emf.createEntityManager())
        {
            GameList gl = em.find(GameList.class, id);
            Assertions.assertEquals("Opdateret Liste", gl.getName());
        }
    }

    @Test
    void updateList_notFound()
    {

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


    @Test
    void deleteList()
    {

        int id = getDefaultListId();

        given()
                .when()
                .delete("/list/remove/" + id)
                .then()
                .statusCode(200);

        try (EntityManager em = emf.createEntityManager())
        {
            Assertions.assertNull(em.find(GameList.class, id));
        }
    }


    @Test
    void getGameListById()
    {

        int id = getDefaultListId();

        given()
                .when()
                .get("/list/list/" + id)
                .then()
                .statusCode(200)
                .body("listID", equalTo(id))
                .body("name", equalTo("Oprettet Liste"));
    }

    @Test
    void getGameListById_notFound()
    {
        given()
                .when()
                .get("/list/list/999999")
                .then()
                .statusCode(404);
    }

    private int getDefaultListId()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery(
                             "SELECT gl.listID FROM GameList gl WHERE gl.name='Oprettet Liste'",
                             Integer.class)
                     .getSingleResult();
        }
    }
}