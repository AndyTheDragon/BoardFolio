package dat.controllers;

import dat.dao.BoardgameDAO;
import dat.dao.GenericDAO;
import dat.entities.Game;
import dat.exceptions.DaoException;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GameController
{

    private final GenericDAO genericDAO;
    private final BoardgameDAO boardgameDAO;
    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    public GameController(EntityManagerFactory emf)
    {
        this.genericDAO = new GenericDAO(emf);
        this.boardgameDAO = new BoardgameDAO(emf);
    }

    public void populateBoardGames(@NotNull Context context)
    {
        try
        {
            Game game = Game.builder()
                    .title("Catan")
                    .description("Trade, build, and settle the island of Catan in this classic board game.")
                    .minNoOfPlayers(3)
                    .maxNoOfPlayers(4)
                    .minAge(10)
                    .maxAge(99)
                    .releaseYear(1995)
                    .languages(List.of("English", "German", "French"))
                    .genre(dat.enums.Genre.STRATEGY)
                    .build();

            Game saved = genericDAO.create(game);
            context.status(200).json(game);
        } catch (DaoException daoException)
        {
            logger.error(daoException.getMessage());
            context.status(400).result(daoException.getMessage());
        }
    }

    public void getAllBoardGames(@NotNull Context ctx)
    {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
        int pageSize = ctx.queryParamAsClass("pageSize", Integer.class).getOrDefault(25);
        try
        {
            List<Game> boardGameList = boardgameDAO.getBoardgames(page, pageSize);
            ctx.status(200).json(boardGameList);
        } catch (DaoException | IllegalArgumentException exception)
        {
            logger.error(exception.getMessage());
            ctx.status(400).result(exception.getMessage());
        }
    }
}
