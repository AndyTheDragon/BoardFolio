package dat.controllers;

import dat.dao.BoardgameDAO;
import dat.dao.GenericDAO;
import dat.entities.Game;
import dat.enums.Genre;
import dat.exceptions.DaoException;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static dat.enums.Genre.STRATEGY;

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
            Set<Genre> genres = new HashSet<>();
            genres.add(STRATEGY);

            Game game = Game.builder()
                            .title("Catan")
                            .description("Trade, build, and settle the island of Catan in this classic board game.")
                            .minNoOfPlayers(3)
                            .maxNoOfPlayers(4)
                            .releaseYear(1995)
                            .genres(genres)
                            .build();

            Game saved = genericDAO.create(game);
            context.status(200).json(saved);
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

    public void searchGame(@NotNull Context ctx)
    {
        String title = ctx.queryParam("title");
        if (title == null || title.isBlank())
        {
            ctx.status(400).result("Title query parameter is required");
            return;
        }
        try
        {
            Game game = boardgameDAO.searchByTitle(title).stream().findFirst().orElse(null);
            ctx.status(200).json(game);
        } catch (DaoException daoException)
        {
            logger.error(daoException.getMessage());
            ctx.status(400).result(daoException.getMessage());
        }
    }

    public void searchByTitleAndCategory(@NotNull Context ctx)
    {
        String title = ctx.queryParam("title");
        String category = ctx.queryParam("category");
        if (title == null || title.isBlank() || category == null || category.isBlank())
        {
            ctx.status(400).result("Title or category is invalid");
            return;
        }
        try
        {
            List<Game> games = boardgameDAO.searchByTitle(title).stream()
                    .filter(game -> game.getGenres().stream()
                            .anyMatch(genre -> genre.name().equalsIgnoreCase(category)))
                    .toList();
            ctx.status(200).json(games);
        } catch (DaoException daoException)
        {
            logger.error(daoException.getMessage());
            ctx.status(400).result(daoException.getMessage());
        }
    }
}
