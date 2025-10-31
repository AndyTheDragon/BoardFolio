package dat.dao;

import dat.entities.Game;
import dat.enums.Genre;
import dat.exceptions.DaoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GameDAO extends GenericDAO
{

    private static final Logger logger = LoggerFactory.getLogger(GameDAO.class);

    public GameDAO(EntityManagerFactory emf)
    {
        super(emf);
    }

    /**
     * Inserts 20 mock games into the database if none exist yet.
     */
    public void createMockGames() throws DaoException
    {
        try (EntityManager em = emf.createEntityManager())
        {
            Long count = em.createQuery("SELECT COUNT(g) FROM Game g", Long.class).getSingleResult();

            if (count != null && count > 0)
            {
                logger.info("{} games already exist in the database. Skipping mock data creation.", count);
                return;
            }

            logger.info("Creating 20 mock games...");

            List<Game> games = List.of(
                    new Game(null, "Catan", "Trade and build settlements to dominate the island of Catan.", 3, 4, 10, 99, 1995, List.of("English", "German"), Genre.STRATEGY),
                    new Game(null, "Ticket to Ride", "Build train routes across continents to score the most points.", 2, 5, 8, 99, 2004, List.of("English", "French"), Genre.FAMILY),
                    new Game(null, "Carcassonne", "Place tiles to build cities and roads in medieval France.", 2, 5, 7, 99, 2000, List.of("English", "German"), Genre.TILE_PLACEMENT),
                    new Game(null, "Pandemic", "Work together to stop deadly diseases from spreading worldwide.", 2, 4, 8, 99, 2008, List.of("English", "Spanish"), Genre.COOPERATIVE),
                    new Game(null, "7 Wonders", "Build a civilization by drafting cards representing structures and wonders.", 2, 7, 10, 99, 2010, List.of("English", "French"), Genre.STRATEGY),
                    new Game(null, "Azul", "Decorate the royal palace of Evora with beautiful tiles.", 2, 4, 8, 99, 2017, List.of("English"), Genre.ABSTRACT),
                    new Game(null, "Splendor", "Collect gems to build a successful trading empire.", 2, 4, 10, 99, 2014, List.of("English"), Genre.CARD),
                    new Game(null, "Dominion", "Build your deck of cards to expand your dominion.", 2, 4, 13, 99, 2008, List.of("English"), Genre.DECKBUILDING),
                    new Game(null, "Codenames", "Give one-word clues to find your team’s secret agents.", 2, 8, 10, 99, 2015, List.of("English"), Genre.PARTY),
                    new Game(null, "Dixit", "Use imagination and storytelling to guess the right image.", 3, 6, 8, 99, 2008, List.of("English", "French"), Genre.FAMILY),
                    new Game(null, "Terraforming Mars", "Corporations compete to make Mars habitable.", 1, 5, 12, 99, 2016, List.of("English"), Genre.STRATEGY),
                    new Game(null, "Gloomhaven", "Epic tactical combat in a persistent world of shifting motives.", 1, 4, 14, 99, 2017, List.of("English"), Genre.ADVENTURE),
                    new Game(null, "Wingspan", "Attract birds to your wildlife preserve and build powerful combos.", 1, 5, 10, 99, 2019, List.of("English"), Genre.ENGINE_BUILDING),
                    new Game(null, "Risk", "Deploy armies to conquer the world through strategy and luck.", 2, 6, 10, 99, 1959, List.of("English"), Genre.STRATEGY),
                    new Game(null, "Monopoly", "Buy, trade, and build your way to financial domination.", 2, 8, 8, 99, 1935, List.of("English"), Genre.FAMILY),
                    new Game(null, "Clue (Cluedo)", "Solve the classic murder mystery by deduction.", 3, 6, 8, 99, 1949, List.of("English"), Genre.MYSTERY),
                    new Game(null, "The Crew", "Complete missions together in this trick-taking cooperative card game.", 2, 5, 10, 99, 2019, List.of("English"), Genre.COOPERATIVE),
                    new Game(null, "Betrayal at House on the Hill", "Explore a haunted mansion—until one of you betrays the others.", 3, 6, 12, 99, 2004, List.of("English"), Genre.HORROR),
                    new Game(null, "Scythe", "Lead your faction to power in a dieselpunk alternate history.", 1, 5, 14, 99, 2016, List.of("English"), Genre.STRATEGY),
                    new Game(null, "Uno", "Match colors and numbers to get rid of all your cards first.", 2, 10, 7, 99, 1971, List.of("English"), Genre.CARD)
            );

            em.getTransaction().begin();
            for (Game g : games)
            {
                em.persist(g);
            }
            em.getTransaction().commit();

            logger.info("Successfully inserted 20 mock games into the database.");
        }
        catch (Exception e)
        {
            logger.error("Error creating mock games", e);
            throw new DaoException("Error creating mock games", e);
        }
    }
}
