package dat.services;

import dat.config.HibernateConfig;
import dat.dao.GenericDAO;
import dat.dto.GameDTO;
import dat.entities.Game;
import dat.entities.GameList;
import dat.entities.UserAccount;
import jakarta.persistence.EntityManagerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Populator
{
    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private final static GenericDAO genericDAO = new GenericDAO(emf);

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
                genericDAO.create(game);
                gameMap.put(dto.getBGG_API_ID(), game);
            }


            UserAccount user = new UserAccount("testUser", "test");
            user.getMyCollection().setUser(user);
            user.getMyCollection().setCreatedDate(LocalDateTime.now());
            user.getMyCollection().setName("My collection of games");


            user.addToMyCollection(gameMap.get(gameDTOS.get(1).getBGG_API_ID()));
            user.addToMyCollection(gameMap.get(gameDTOS.get(2).getBGG_API_ID()));
            user.addToMyCollection(gameMap.get(gameDTOS.get(5).getBGG_API_ID()));

            genericDAO.create(user);

            GameList customList = new GameList("test");
            customList.setCreatedDate(LocalDateTime.now());
            user.addList(customList);

            customList.addGame(gameMap.get(gameDTOS.get(0).getBGG_API_ID()));
            customList.addGame(gameMap.get(gameDTOS.get(1).getBGG_API_ID()));
            customList.addGame(gameMap.get(gameDTOS.get(2).getBGG_API_ID()));

            genericDAO.create(customList);

        } catch (Exception e)
        {
            throw new RuntimeException("Error in DevPopulator: " + e.getMessage(), e);
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
            e.printStackTrace();
        }

        return content.toString();
    }
}
