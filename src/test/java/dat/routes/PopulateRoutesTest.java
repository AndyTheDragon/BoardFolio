package dat.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.controllers.SecurityController;
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
    private ObjectMapper objectMapper = new ObjectMapper();
    private static GenericDAO genericDAO;

    private final String TEST_USER = "testuser";
    private final String TEST_PASSWORD = "password123";
    private final String TEST_ADMIN = "testadmin";

    private String adminToken;


    @BeforeAll
    static void setUpAll()
    {
        SecurityController securityController = new SecurityController(emf);
        genericDAO = new GenericDAO(emf);
        Routes routes = new Routes(securityController);

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
        adminToken = getAdminToken();

        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            // Clean up existing data
            em.createQuery("DELETE FROM UserAccount").executeUpdate();

            em.createNativeQuery("DELETE FROM game_genres").executeUpdate();
            em.createNativeQuery("DELETE FROM game_languages").executeUpdate();
            em.createNativeQuery("DELETE FROM game_collection").executeUpdate();
            em.createNativeQuery("DELETE FROM collection_game").executeUpdate();
            em.createNativeQuery("DELETE FROM useraccount_roles").executeUpdate();
            em.createNativeQuery("DELETE FROM useraccount_collection").executeUpdate();
            em.createNativeQuery("DELETE FROM game").executeUpdate();
            em.createNativeQuery("DELETE FROM collection").executeUpdate();
            em.createNativeQuery("DELETE FROM useraccount").executeUpdate();

            try {
                em.createNativeQuery("ALTER SEQUENCE game_gameid_seq RESTART WITH 1").executeUpdate();
            } catch (Exception ignored) {}


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
    }

    @Test
    void testPopulateDatabaseRoles()
    {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .post("/roles")
                .then()
                .statusCode(200);

        List<UserAccount> users =  genericDAO.getAll(UserAccount.class);

        assertNotNull(users);
    }

    //TODO change statusCode when endpoint does
    @Test
    void testPopulateDatabaseGames()
    {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .post("/games")
                .then()
                .statusCode(200);

        List<Game> games = genericDAO.getAll(Game.class);

        //TODO Check there is somthing in database
        assertNotNull(games);
    }

    //TODO change statusCode when endpoint does
    @Test
    void testUpdateDatabaseGames()
    {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .post("/games/sync")
                .then()
                .statusCode(200);
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
            throw new RuntimeException("Kunne ikke konvertere admin-bruger til JSON", e);
        }

        // TODO Change to match Andres way
        String token = given()
                .contentType("application/json")
                .body(adminJson)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().path("token");

        return token;
    }
}