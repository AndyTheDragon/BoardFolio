package dat.services;

import dat.config.HibernateConfig;
import dat.entities.Game;
import dat.entities.UserAccount;
import dat.enums.Genre;
import dat.enums.Languages;
import dat.enums.Roles;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Set;

public class TestPopulator {
    public static void populate() {
        // Hent SessionFactory fra Hibernate-konfigurationen (f.eks. via HibernateConfig)
        SessionFactory sessionFactory = HibernateConfig.getEntityManagerFactory().unwrap(SessionFactory.class);
        // Åbn en ny Hibernate-session og start transaktion
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // Opret testbrugere
            UserAccount user1 = new UserAccount("testuser", "password123");
            user1.addRole(Roles.USER);
            UserAccount adminUser = new UserAccount("admin", "adminpass");
            adminUser.addRole(Roles.ADMIN);
            adminUser.addRole(Roles.USER);

            // Opret test-spil (Game) med nødvendige felter
            Game game1 = new Game(
                    "Skak",
                    "Klassisk strategispil for to spillere",
                    2, 2,               // min og max antal spillere
                    6, 99,              // aldersgruppe min og max
                    1475,               // udgivelsesår (skak estimeret opfundet år)
                    // Sæt af sprog og genre for spillet
                    List.of(Languages.ENGLISH, Languages.DANISH),
                    Set.of(Genre.ABSTRACT_STRATEGY, Genre.STRATEGY)  // **Genre.STRATEGY antages at eksistere eller brug en passende genre**
            );
            Game game2 = new Game(
                    "Matador",
                    "Klassisk økonomi/handel brætspil for hele familien",
                    2, 6,
                    8, 99,
                    1935,               // udgivelsesår for Matador/Monopoly
                    List.of(Languages.DANISH, Languages.ENGLISH),
                    Set.of(Genre.ECONOMIC, Genre.FAMILY)           // **Genre.FAMILY antages at eksistere eller vælg passende kategori**
            );

            // Persistér (gem) objekterne i databasen
            session.persist(user1);
            session.persist(adminUser);
            session.persist(game1);
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