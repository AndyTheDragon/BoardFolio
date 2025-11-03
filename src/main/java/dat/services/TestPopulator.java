package dat.services;

import dat.config.HibernateConfig;
import dat.entities.Game;
import dat.entities.UserAccount;
import dat.enums.Genre;
import dat.enums.Roles;
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

        SessionFactory sessionFactory = HibernateConfig.getEntityManagerFactory().unwrap(SessionFactory.class);
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try
        {
            tx = session.beginTransaction();
            UserAccount user1 = new UserAccount("testuser", "password123");
            user1.addRole(Roles.USER);
            UserAccount adminUser = new UserAccount("admin", "adminpass");
            adminUser.addRole(Roles.ADMIN);
            adminUser.addRole(Roles.USER);

            Set<Genre> genres = EnumSet.of(Genre.RACING, Genre.CIVILIZATION);

            Game game = new Game(
                    "Catan",
                    "Trade, build, and settle.",
                    10,
                    3,
                    4,
                    1995,
                    "https://example.com/catan.jpg",
                    "https://example.com/catan-thumb.jpg",
                    Collections.emptySet(),
                    genres
            );
            Game game2 = new Game(
                    "Matador",
                    "Klassisk økonomi/handel brætspil for hele familien",
                    2,
                    4,
                    6,
                    1935,
                    "https://example.com/catan.jpg",
                    "https://example.com/catan-thumb.jpg",
                    Collections.emptySet(),
                    genres
            );


            session.persist(user1);
            session.persist(adminUser);
            session.persist(game);
            session.persist(game2);


            tx.commit();
        } catch (Exception e)
        {
            if (tx != null)
            {
                tx.rollback();
            }
            throw e;
        } finally
        {
            session.close();
        }
    }
}