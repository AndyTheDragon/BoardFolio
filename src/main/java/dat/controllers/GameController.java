package dat.controllers;

import dat.dao.GenericDAO;
import dat.entities.Game;
import dat.exceptions.DaoException;
import io.javalin.http.Context;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GameController {

    private GenericDAO genericDAO;
    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    public GameController(EntityManagerFactory emf) {
        this.genericDAO = new GenericDAO(emf);
    }

    public void getAllBoardGames(@NotNull Context context) {
        try{
            List<Game> boardGameList = genericDAO.getAll(Game.class);
            context.status(200).json(boardGameList);
        }catch (DaoException daoException){
            logger.error(daoException.getMessage());
            context.status(400).result(daoException.getMessage());
        }
    }
}
