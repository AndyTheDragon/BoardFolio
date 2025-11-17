package dat.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.dao.GenericDAO;
import dat.entities.Game;
import dat.entities.UserAccount;
import dat.enums.Roles;
import dk.bugelhartmann.UserDTO;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

class PopulateRoutesTest
{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static GenericDAO genericDAO;

    private final String TEST_USER = "testuser";
    private final String TEST_PASSWORD = "password123";
    private final String TEST_ADMIN = "testadmin";

    private String adminToken;

    @BeforeAll
    static void setUpAll()
    {
        genericDAO = new GenericDAO(emf);
        Routes routes = new Routes(emf);

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(routes.getRoutes())
                .handleException()
                .setApiExceptionHandling()
                .checkSecurityRoles()
                .startServer(7079);

        RestAssured.baseURI = "http://localhost:7079/api";
    }

    @BeforeEach
    void setUp()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();

            // Clean up game-related and user-related tables
            em.createNativeQuery("DELETE FROM game_genres").executeUpdate();
            em.createNativeQuery("DELETE FROM game_languages").executeUpdate();
            em.createNativeQuery("DELETE FROM game_collection").executeUpdate();
            em.createNativeQuery("DELETE FROM collection_game").executeUpdate();
            em.createNativeQuery("DELETE FROM useraccount_roles").executeUpdate();
            em.createNativeQuery("DELETE FROM useraccount_collection").executeUpdate();
            em.createNativeQuery("DELETE FROM game").executeUpdate();
            em.createNativeQuery("DELETE FROM collection").executeUpdate();
            em.createNativeQuery("DELETE FROM useraccount").executeUpdate();

            em.createNativeQuery("ALTER SEQUENCE game_gameid_seq RESTART WITH 1").executeUpdate();

            // Create test user with user role
            UserAccount testUserAccount = new UserAccount(TEST_USER, TEST_PASSWORD);
            testUserAccount.addRole(Roles.USER);
            em.persist(testUserAccount);

            // Create test admin with admin role
            UserAccount testAdmin = new UserAccount(TEST_ADMIN, TEST_PASSWORD);
            testAdmin.addRole(Roles.USER);
            testAdmin.addRole(Roles.ADMIN);
            em.persist(testAdmin);

            em.getTransaction().commit();
        }

        adminToken = getAdminToken(); // ✅ after admin exists
    }

    // No more /roles test, because you don't have a /roles route in Routes
    // @Test
    // void testPopulateDatabaseRoles() { ... }

    @Test
    void testPopulateDatabaseGames()
    {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .post("/populate/games")         // ✅ correct route
                .then()
                .statusCode(200);

        List<Game> games = genericDAO.getAll(Game.class);

        assertNotNull(games);
        assertFalse(games.isEmpty(), "Expected games to be populated in the database");
    }

    @Test
    void testPopulateDevDatabaseGames()
    {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .post("/populate/games/dev")    // ✅ matches Routes
                .then()
                .statusCode(200);
        // Add DB assertions here if DevPopulator adds data
    }

    private String getAdminToken()
    {
        UserDTO adminUser = new UserDTO(TEST_ADMIN, TEST_PASSWORD);

        String adminJson;
        try
        {
            adminJson = objectMapper.writeValueAsString(adminUser);
        } catch (JsonProcessingException e)
        {
            throw new RuntimeException("Could not convert admin user to JSON", e);
        }

        return given()
                .contentType("application/json")
                .body(adminJson)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
