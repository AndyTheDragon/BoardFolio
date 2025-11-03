package dat.routes;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.controllers.GameController;
import dat.controllers.SecurityController;
import dat.entities.Game;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class BoardGameRouteTest {
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private final Logger logger = LoggerFactory.getLogger(BoardGameRouteTest.class.getName());


    @BeforeAll
    static void setupAll() {
        SecurityController securityController = new SecurityController(emf);
        GameController gameController = new GameController(emf);
        Routes routes = new Routes(securityController, gameController);


        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(routes.getRoutes())
                .handleException()
                .setApiExceptionHandling()
                .checkSecurityRoles()
                .startServer(7071);
        RestAssured.baseURI = "http://localhost:7071/api";
    }

    @BeforeEach
    void setupTest() {
        try (EntityManager em = emf.createEntityManager()) {

            em.getTransaction().begin();
            em.createQuery("delete from Game").executeUpdate();

            Game game = Game.builder()
                    .title("Catan")
                    .description("Trade, build, and settle the island of Catan in this classic board game.")
                    .minNoOfPlayers(3)
                    .maxNoOfPlayers(4)
                    .minAge(10)
                    .maxAge(99)
                    .releaseYear(1995)
                    .languages(List.of("English", "German", "French"))
                    .genre(dat.enums.Genre.STRATEGY)
                    .build();
            em.persist(game);
            em.getTransaction().commit();
        }
    }

    @Test
    void getAllBoardGames() {
        given()
                .when()
                .get("/boardgames")
                .then()
                .statusCode(200)
                .body("", hasSize(1))
                .body("[0].title", equalTo("Catan"))
                .log().all();
    }
}



