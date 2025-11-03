package dat.services;

import dat.config.HibernateConfig;
import dat.entities.Game;
import dat.entities.UserAccount;
import dat.enums.Genre;
import dat.enums.Roles;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.sql.SQLOutput;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class TestPopulator {
    public static void populate() {

        SessionFactory sessionFactory = HibernateConfig.getEntityManagerFactory().unwrap(SessionFactory.class);
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            System.out.println(">>> POPULATOR: starting");
            // Opret testbrugere
            UserAccount user1 = new UserAccount("testuser", "password123");
            user1.addRole(Roles.USER);
            UserAccount adminUser = new UserAccount("admin", "adminpass");
            adminUser.addRole(Roles.ADMIN);
            adminUser.addRole(Roles.USER);

            Set<Genre> genres = EnumSet.of(Genre.RACING, Genre.CIVILIZATION);

            // Opret test-spil (Game) med nødvendige felter
            Game game = new Game(
                    "Catan",
                    "Trade, build, and settle.",
                    10,                // minAge
                    3,                 // minNoOfPlayers
                    4,                 // maxNoOfPlayers
                    1995,              // releaseYear
                    "https://example.com/catan.jpg",        // imageURL
                    "https://example.com/catan-thumb.jpg",  // thumbnailURL
                    Collections.emptySet(),
                    genres
            );
            Game game2 = new Game(
                    "Matador",
                    "Klassisk økonomi/handel brætspil for hele familien",
                    2,
                    4,
                    6,
                    1935,               // udgivelsesår for Matador/Monopoly
                    "https://example.com/catan.jpg",        // imageURL
                    "https://example.com/catan-thumb.jpg",
                    Collections.emptySet(),
                    genres // **Genre.FAMILY antages at eksistere eller vælg passende kategori**
            );

            // Persistér (gem) objekterne i databasen
            session.persist(user1);
            session.persist(adminUser);
            session.persist(game);
            session.persist(game2);


            // Commit transaktionen hvis alt er gået godt
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();  // Rul tilbage ved fejl
            }
            throw e;  // Genkast exception efter rollback (kan udvides med logning)
        } finally {
            session.close();  // Luk altid sessionen i finally-blok
        }
    }
}