package dat.dao;

import dat.config.HibernateConfig;
import dat.services.TestPopulator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DAOTestBase
{
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DAOTestBase.class);
    protected EntityManagerFactory emf;

    @BeforeAll
    void setUpAll()
    {
        if (emf == null || !emf.isOpen())
        {
            HibernateConfig.setTest(true);
            emf = HibernateConfig.getEntityManagerFactoryForTest();
        }
    }

    @BeforeEach
    void cleanDatabase()
    {
        if (emf != null && emf.isOpen())
        {
            try (EntityManager em = emf.createEntityManager())
            {
                em.getTransaction().begin();

                em.createNativeQuery("DELETE FROM custom_list").executeUpdate();
                em.createNativeQuery("DELETE FROM game_languages").executeUpdate();
                em.createNativeQuery("DELETE FROM game_genres").executeUpdate();
                em.createNativeQuery("DELETE FROM useraccount_roles").executeUpdate();
                em.createNativeQuery("DELETE FROM useraccount_roles").executeUpdate();
                em.createQuery("DELETE FROM GameList").executeUpdate();
                em.createQuery("DELETE FROM Game").executeUpdate();
                em.createQuery("DELETE FROM UserAccount").executeUpdate();


                em.getTransaction().commit();
            } catch (Exception e)
            {
                log.error("Failed to clean database", e);
            }
        }

        TestPopulator.populate();
    }
}
