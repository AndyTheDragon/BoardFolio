package dat;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.routes.Routes;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main
{
    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private final static Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args)
    {
        Routes routes = new Routes(emf);

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(routes.getRoutes())
                .handleException()
                .setApiExceptionHandling()
                .checkSecurityRoles()
                .startServer(7070);
        logger.info("Server started on http://127.0.0.1:7070/api");

    }
}