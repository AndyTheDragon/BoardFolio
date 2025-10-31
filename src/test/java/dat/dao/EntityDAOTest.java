package dat.dao;

import dat.config.HibernateConfig;
import dat.entities.GameList;
import dat.entities.Game;
import dat.enums.Genre;
import dat.enums.Languages;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EntityDAOTest
{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static final GenericDAO genericDAO = new GenericDAO(emf);
    private Game testGame;

    @BeforeEach
    void setUp()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            // Clean up existing data
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

            // Create test game
            String testTitle = "Test Title";
            String testDescription = "Test decription";
            int testMinNoOfPlayers = 2;
            int testMaxNoOfPlayers = 4;
            int testMinAge = 10;
            int testMaxAge = 99;
            int testReleaseYear = 2000;
            Languages lang1 = Languages.ENGLISH;
            Languages lang2 = Languages.DANISH;

            List<Languages> testLanguages = new ArrayList<>();
            testLanguages.add(lang1);
            testLanguages.add(lang2);

            Set<Genre> testGenre = new HashSet<>();
            Genre genre1 = Genre.ADVENTURE;
            testGenre.add(genre1);

            Set<GameList> testGameLists = new HashSet<>();
            GameList testGameList = new GameList("TestCollectionName");
            testGameLists.add(testGameList);

            testGame = new Game(testTitle, testDescription, testMinNoOfPlayers, testMaxNoOfPlayers, testMinAge,
                                testMaxAge, testReleaseYear, testLanguages, testGameLists, testGenre);

            testGameList.addGame(testGame);

            em.persist(testGameList);
            em.persist(testGame);

            em.getTransaction().commit();
        }
    }

    @Test
    void getGame()
    {
        // Arrange
        // Act
        Game result = genericDAO.getById(Game.class, 1);

        // Assert
        assertNotNull(result);
        assertEquals(testGame.getTitle(), result.getTitle());
        assertEquals(testGame.getMinNoOfPlayers(), result.getMinNoOfPlayers());
    }

    @Test
    void creatGame()
    {
        // Arrange
        // Create test game
        String testTitle2 = "Test Title 2";
        String testDescription2 = "Test decription 2";
        int testMinNoOfPlayers2 = 2;
        int testMaxNoOfPlayers2 = 4;
        int testMinAge2 = 10;
        int testMaxAge2 = 99;
        int testReleaseYear2 = 2000;
        Languages lang1 = Languages.ENGLISH;
        Languages lang2 = Languages.DANISH;

        List<Languages> testLanguages2 = new ArrayList<>();
        testLanguages2.add(lang1);
        testLanguages2.add(lang2);

        Set<Genre> testGenre2 = new HashSet<>();
        Genre genre1 = Genre.ADVENTURE;
        testGenre2.add(genre1);

        Set<GameList> testCollections2 = new HashSet<>();
        GameList testGameList2 = new GameList("TestCollectionName 2");

        Game testGame2 = new Game(testTitle2, testDescription2, testMinNoOfPlayers2, testMaxNoOfPlayers2, testMinAge2,
                                  testMaxAge2, testReleaseYear2, testLanguages2, testCollections2, testGenre2);
        // Act
        Game result = genericDAO.create(testGame2);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getGameId());
        assertEquals(testGame2.getTitle(), result.getTitle());
        assertEquals(testGame2.getLanguages().size(),result.getLanguages().size());
        assertTrue(testGame2.getGenres().contains(Genre.ADVENTURE));
    }
}