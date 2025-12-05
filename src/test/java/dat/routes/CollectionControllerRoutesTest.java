package dat.routes;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.entities.Game;
import dat.entities.GameList;
import dat.entities.UserAccount;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CollectionControllerRoutesTest {

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

    @BeforeAll
    void setupAll() {

        Routes routes = new Routes(emf);

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(routes.getRoutes())
                .handleException()
                .setApiExceptionHandling()
                .checkSecurityRoles()
                .startServer(7073);

        RestAssured.baseURI = "http://localhost:7073/api";
    }

    @BeforeEach
    void setupTest() {

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        em.createNativeQuery(
                "TRUNCATE TABLE custom_list, game_genres, useraccount_roles, gamelist, game, useraccount RESTART IDENTITY CASCADE"
        ).executeUpdate();

        UserAccount user = new UserAccount("bob", "password");

        GameList collection = user.getMyCollection();
        collection.setUser(user);

        em.persist(collection);
        em.persist(user);

        em.getTransaction().commit();
        em.close();
    }

    @Test
    void testGetEmptyCollection() {

        given()
                .when()
                .get("/collection/get?username=bob")
                .then()
                .statusCode(200)
                .body("", hasSize(0))
                .log().all();
    }

    @Test
    void testAddToCollection() {

        long gameId = insertGame("Catan");

        given()
                .when()
                .put("/collection/add?username=bob&gameId=" + gameId)
                .then()
                .statusCode(200)
                .body("", hasSize(1))
                .body("[0].title", equalTo("Catan"))
                .log().all();
    }

    @Test
    void testGetCollectionWithGames() {

        long id1 = insertGame("Catan");
        long id2 = insertGame("Terraforming Mars");

        addGame("bob", id1);
        addGame("bob", id2);

        given()
                .when()
                .get("/collection/get?username=bob")
                .then()
                .statusCode(200)
                .body("", hasSize(2))
                .body("[0].title", anyOf(equalTo("Catan"), equalTo("Terraforming Mars")))
                .log().all();
    }

    @Test
    void testRemoveFromCollection() {

        long gameId = insertGame("Azul");

        // Add game first
        addGame("bob", gameId);

        given()
                .when()
                .delete("/collection/remove?username=bob&gameId=" + gameId)
                .then()
                .statusCode(200)
                .body("", hasSize(0))
                .log().all();
    }

    private long insertGame(String title) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Game game = Game.builder()
                .title(title)
                .description("Test game")
                .minNoOfPlayers(1)
                .maxNoOfPlayers(4)
                .minAge(10)
                .releaseYear(2000)
                .imageURL("img")
                .thumbnailURL("thumb")
                .build();

        em.persist(game);
        em.getTransaction().commit();
        em.close();

        return game.getGameId();
    }

    private void addGame(String username, long gameId) {
        given().when().put("/collection/add?username=" + username + "&gameId=" + gameId);
    }
}
