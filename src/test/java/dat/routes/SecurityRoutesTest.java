package dat.routes;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.controllers.SecurityController;
import dat.entities.UserAccount;
import dat.enums.Roles;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityRoutesTest
{

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private final Logger logger = LoggerFactory.getLogger(SecurityRoutesTest.class.getName());
    private final String TEST_USER = "testuser";
    private final String TEST_PASSWORD = "password123";
    private final String TEST_ADMIN = "testadmin";

    @BeforeAll
    static void setUpAll()
    {
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
            // Clean up existing data
            em.createNativeQuery("TRUNCATE TABLE gamelist, useraccount RESTART IDENTITY CASCADE").executeUpdate();


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
    void healtcheck_test()
    {
        given()
                .when()
                .get("/auth/healthcheck")
                .then()
                .statusCode(200)
                .body("msg", equalTo("API is up and running"));
    }

    @Test
    void testLogin_Success()
    {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", TEST_USER);
        loginRequest.put("password", TEST_PASSWORD);

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("username", equalTo(TEST_USER));
    }

    @Test
    void testLogin_WrongPassword()
    {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", TEST_USER);
        loginRequest.put("password", "wrongpassword");

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("message", containsString("Could not verify user"));
    }

    @Test
    void testLogin_UserNotFound()
    {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "nonexistentuser");
        loginRequest.put("password", TEST_PASSWORD);

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("message", containsString("Could not verify user"));
    }

    @Test
    void testRegister_Success()
    {
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", "newuser");
        registerRequest.put("password", "newpassword");

        given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("username", equalTo("newuser"));
    }

    @Test
    void testRegister_UserAlreadyExists()
    {

        int userCountBefore = countUsers();

        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", TEST_USER);
        registerRequest.put("password", TEST_PASSWORD);

        try
        {
            given()
                    .contentType(ContentType.JSON)
                    .body(registerRequest)
                    .when()
                    .post("/auth/register");
        } catch (Exception e)
        {
            // Ignore any exceptions - we expect this to fail
            logger.info("Expected exception: {}", e.getMessage());
        }


        int userCountAfter = countUsers();


        assertEquals(userCountBefore, userCountAfter,
                     "User count should not change when trying to register an existing user");
    }

    private int countUsers()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery("SELECT COUNT(u) FROM UserAccount u", Long.class).getSingleResult().intValue();
        }
    }

    @Test
    void testVerify_ValidToken()
    {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", TEST_USER);
        loginRequest.put("password", TEST_PASSWORD);

        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post("/auth/login");

        String token = loginResponse.jsonPath().getString("token");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/auth/verify")
                .then()
                .statusCode(200)
                .body("msg", equalTo("Token is valid"));
    }

    @Test
    void testVerify_InvalidToken()
    {
        given()
                .header("Authorization", "Bearer invalidtoken")
                .when()
                .get("/auth/verify")
                .then()
                .statusCode(401);
    }

    @Test
    void testVerify_NoToken()
    {
        given()
                .when()
                .get("/auth/verify")
                .then()
                .statusCode(401);
    }

    @Test
    void testTokenLifespan()
    {

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", TEST_USER);
        loginRequest.put("password", TEST_PASSWORD);

        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post("/auth/login");

        String token = loginResponse.jsonPath().getString("token");


        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/auth/tokenlifespan")
                .then()
                .statusCode(200)
                .body("msg", containsString("Token is valid until"))
                .body("expireTime", notNullValue())
                .body("secondsToLive", notNullValue());
    }
}
