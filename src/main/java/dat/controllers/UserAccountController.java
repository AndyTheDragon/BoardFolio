package dat.controllers;

import dat.dao.BoardgameDAO;
import dat.dao.GenericDAO;
import dat.entities.Game;
import dat.entities.UserAccount;
import dat.exceptions.DaoException;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserAccountController
{
    private final GenericDAO genericDAO;
    private final BoardgameDAO boardgameDAO;
    private final Logger logger = LoggerFactory.getLogger(UserAccountController.class);

    public UserAccountController(EntityManagerFactory emf)
    {
        this.genericDAO = new GenericDAO(emf);
        this.boardgameDAO = new BoardgameDAO(emf);
    }

    public void doLogin(@NotNull Context ctx)
    {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        if (email != null)
            email = email.toLowerCase();

        try
        {
            UserAccount account = genericDAO.login(email, password);

            ctx.sessionAttribute("currentUser", account);

            ctx.redirect("/");
        }
        catch (DaoException e)
        {
            logger.warn("Login failed: {}", e.getMessage());

            ctx.status(401).attribute("message", e.getMessage());
            ctx.redirect("/login"); // stay on login page
        }
    }


}
