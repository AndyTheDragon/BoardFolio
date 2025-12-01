package dat.services;

import dat.config.HibernateConfig;
import dat.dao.GenericDAO;
import dat.dto.GameDTO;
import dat.entities.Game;
import dat.entities.GameList;
import dat.entities.UserAccount;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dat.enums.Roles;


public class Populator
{
    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private final static GenericDAO genericDAO = new GenericDAO(emf);
    private final static Logger logger = LoggerFactory.getLogger(Populator.class);

    public void testPopulator()
    {
    }

    public static void DevPopulator()
    {
        try
        {
            String xmlFilePath = "src/main/resources/testdata/bgg_response_1_to_10.xml";
            String xmlAsString = readCsvFileToString(xmlFilePath);

            List<GameDTO> gameDTOS = BoardGameGeekService.parseBatchOfGames(xmlAsString);


            Map<Long, Game> gameMap = new HashMap<>();
            for (GameDTO dto : gameDTOS)
            {
                Game game = dto.toEntity(dto);
                Game game1 = genericDAO.create(game);
                gameMap.put(game.getGameId(), game1);
            }


            UserAccount user = new UserAccount("testUser", "test");
            user.addRole(Roles.USER);

            user.getMyCollection().setUser(user);
            user.getMyCollection().setCreatedDate(LocalDateTime.now());
            user.getMyCollection().setName("My collection of games");

            Game game1 = gameMap.get(1L);
            Game game2 = gameMap.get(2L);
            Game game3 = gameMap.get(3L);

            user.addToMyCollection(game1);
            user.addToMyCollection(game2);
            user.addToMyCollection(game3);


            genericDAO.create(user);

            GameList customList = new GameList("test");
            customList.setCreatedDate(LocalDateTime.now());
            user.addList(customList);

            customList.addGame(gameMap.get(1L));
            customList.addGame(gameMap.get(3L));
            customList.addGame(gameMap.get(4L));

            genericDAO.create(customList);

        } catch (Exception e)
        {
            logger.error("Error populating database: " + e.getMessage());
        }
    }

    public static String readCsvFileToString(String filePath)
    {
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                content.append(line).append("\n");
            }
        } catch (IOException e)
        {
            logger.error("Error reading CSV file: " + e.getMessage());
            content.setLength(0); // Clear content on error
        }

        return content.toString();
    }
}
