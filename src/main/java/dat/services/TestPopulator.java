package dat.services;

import dat.config.HibernateConfig;
import dat.entities.Game;
import dat.entities.UserAccount;
import dat.enums.Genre;
import dat.enums.Roles;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class TestPopulator
{
    public static void populate()
    {

        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();

        try
        {
            em.getTransaction().begin();

            UserAccount user1 = new UserAccount("testuser", "password123");
            user1.addRole(Roles.USER);

            UserAccount adminUser = new UserAccount("admin", "adminpass");
            adminUser.addRole(Roles.ADMIN);
            adminUser.addRole(Roles.USER);

            Set<Genre> genres = EnumSet.of(Genre.RACING, Genre.CIVILIZATION);

            Game game = new Game(
                    "Catan",
                    "Trade, build, and settle.",
                    3,
                    4,
                    1995,
                    "https://example.com/catan.jpg",
                    "https://example.com/catan-thumb.jpg",
                    genres
            );

            Game game2 = new Game(
                    "Matador",
                    "Klassisk økonomi/handel brætspil for hele familien",
                    4,
                    6,
                    1935,
                    "https://example.com/matador.jpg",
                    "https://example.com/matador-thumb.jpg",
                    genres
            );

            em.persist(user1);
            em.persist(adminUser);
            em.persist(game);
            em.persist(game2);

            em.getTransaction().commit();
        } catch (Exception e)
        {
            if (em.getTransaction().isActive())
            {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally
        {
            em.close();
        }

    }
}