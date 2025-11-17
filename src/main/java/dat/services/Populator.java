package dat.services;

import dat.config.HibernateConfig;
import dat.dao.GenericDAO;
import dat.dto.GameDTO;
import dat.entities.Game;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


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
            String csvFilePath = "src/main/resources/testdata/bgg_response_1_to_10.xml";
            String csvAsString = readCsvFileToString(csvFilePath);

            List<GameDTO> gameDTOS = BoardGameGeekService.parseBatchOfGames(csvAsString);

            for (GameDTO gameDTO : gameDTOS)
            {
                Game game = gameDTO.toEntity(gameDTO);
                genericDAO.create(game);
            }
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
