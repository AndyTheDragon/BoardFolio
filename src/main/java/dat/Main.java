package dat;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.controllers.SecurityController;
import dat.dao.GameDAO;
import dat.exceptions.DaoException;
import dat.routes.Routes;
import jakarta.persistence.EntityManagerFactory;


public class Main
{
    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();


    public static void main(String[] args)
    {

        GameDAO gameDAO = new GameDAO(emf);

        try {
            gameDAO.createMockGames();
        } catch (DaoException e) {
            e.printStackTrace();
        }

        SecurityController securityController = new SecurityController(emf);
        Routes routes = new Routes(securityController);

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(routes.getRoutes())
                .handleException()
                .setApiExceptionHandling()
                .checkSecurityRoles()
                .startServer(7070);
    }
}