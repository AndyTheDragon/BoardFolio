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
            em.createNativeQuery(
                    "TRUNCATE TABLE custom_list, game_genres, useraccount_roles, gamelist, game, useraccount RESTART IDENTITY CASCADE"
            ).executeUpdate();

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

        adminToken = getAdminToken();
    }

//TODO look at how we populate deployed app ,with roles

//    @Test
//    void testPopulateDatabaseRoles()
//    {
//        given()
//                .header("Authorization", "Bearer " + adminToken)
//                .when()
//                .post("/populate/roles")
//                .then()
//                .statusCode(200);
//
//        List<UserAccount> users = genericDAO.getAll(UserAccount.class);
//
//
//        assertEquals(Roles.USER ,users.get(0).getRoles());
//        assertNotNull(users);
//        adminToken = getAdminToken();
//    }

    //TODO When API Key is up check if works
//    @Test
//    void testPopulateDatabaseGames()
//    {
//        given()
//                .header("Authorization", "Bearer " + adminToken)
//                .when()
//                .post("/populate/games")
//                .then()
//                .statusCode(200);
//
//        List<Game> games = genericDAO.getAll(Game.class);
//
//        assertNotNull(games);
//        assertFalse(games.isEmpty(), "Expected games to be populated in the database");
//    }

    @Test
    void testPopulateDevDatabaseGames() {
        // Trigger API
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .post("/populate/games/dev")
                .then()
                .statusCode(201);

        List<Game> games = genericDAO.getAll(Game.class);

        assertNotNull(games, "Games list should not be null");
        assertFalse(games.isEmpty(), "Expected games to be populated in the database");

        Game firstGame = games.get(0);
        assertNotNull(firstGame.getTitle(), "First game should have a title");
        assertNotNull(firstGame.getGenres(), "First game should have genres");
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
