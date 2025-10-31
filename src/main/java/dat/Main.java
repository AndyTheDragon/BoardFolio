package dat;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.controllers.GameController;
import dat.controllers.SecurityController;
import dat.routes.Routes;
import jakarta.persistence.EntityManagerFactory;


public class Main
{
    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();


    public static void main(String[] args)
    {
        SecurityController securityController = new SecurityController(emf);
        GameController gameController = new GameController(emf);
        Routes routes = new Routes(securityController, gameController);

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