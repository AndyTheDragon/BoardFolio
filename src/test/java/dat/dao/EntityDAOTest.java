package dat.dao;

import dat.config.HibernateConfig;
import dat.entities.Collection;
import dat.entities.Game;
import dat.entities.UserAccount;
import dat.enums.Genre;
import dat.enums.Languages;
import dat.enums.Roles;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class EntityDAOTest
{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static final GenericDAO genericDAO = new GenericDAO(emf);

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

            Set<Collection> testCollections = new HashSet<>();
            Collection testCollection = new Collection("TestCollectionName");

            Game testGame = new Game(testTitle, testDescription, testMinNoOfPlayers, testMaxNoOfPlayers, testMinAge, testMaxAge, testReleaseYear,testLanguages, testCollections, testGenre);

            testCollection.addGame(testGame);

            em.getTransaction().commit();
        }
    }
}