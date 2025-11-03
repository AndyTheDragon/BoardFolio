package dat.dao;

import dat.config.HibernateConfig;
import dat.services.TestPopulator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DAOTestBase
{
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

                // 1) Clear join/element-collection tables (native SQL); ignore if absent
                try { em.createNativeQuery("DELETE FROM custom_list").executeUpdate(); } catch (Exception ignored) {}
                try { em.createNativeQuery("DELETE FROM game_languages").executeUpdate(); } catch (Exception ignored) {}
                try { em.createNativeQuery("DELETE FROM game_genres").executeUpdate(); } catch (Exception ignored) {}
                try { em.createNativeQuery("DELETE FROM useraccount_roles").executeUpdate(); } catch (Exception ignored) {}

                // 2) Delete entities in FK-safe order (JPQL bulk updates bypass cascades)
                //    Order: GameList -> Game -> UserAccount
                try { em.createQuery("DELETE FROM GameList").executeUpdate(); } catch (Exception ignored) {}
                try { em.createQuery("DELETE FROM Game").executeUpdate(); } catch (Exception ignored) {}
                try { em.createQuery("DELETE FROM UserAccount").executeUpdate(); } catch (Exception ignored) {}

                em.getTransaction().commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        // 3) Seed baseline data
        TestPopulator.populate();
    }
}
