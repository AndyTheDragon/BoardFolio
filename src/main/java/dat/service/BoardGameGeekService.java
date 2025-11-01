package dat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.JsonNode;
import dat.dto.GameDTO;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// BGG = Board Game Geek
// API URL: https://boardgamegeek.com/using_the_xml_api
// API2 Docs: https://boardgamegeek.com/wiki/page/BGG_XML_API2
public class BoardGameGeekService
{
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final XmlMapper xmlMapper = new XmlMapper();
    static String BGG_API_KEY = System.getenv("BGG_API_KEY"); //TODO setup API Key as a secret system variable
    private static String bggUri = "https://boardgamegeek.com/xmlapi2/thing?id=";
    private static Long maxId = 457416L;
    private static int batchSize = 100;
    private int rateLimit = 5000;

    public static List<GameDTO> getBGGGames()
    {
        List<GameDTO> gameDTOs = new ArrayList<>();

        try
        {
            int startId = 1;
            while (startId < 200)
            {

            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(bggUri))
                    .header("Authorization", "Bearer " + BGG_API_KEY) //TODO insert API key here as a secret variable
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200)
            {
                String xml = response.body();

                // Preprocess XML to fix unescaped &
                xml = xml.replaceAll("&(?!amp;)", "&amp;");

                // Parse XML into a JsonNode tree
                JsonNode rootNode = xmlMapper.readTree(xml);

                // Get the <item> array
                JsonNode itemArray = rootNode.path("item");

                if (itemArray.isArray())
                {
                    for (JsonNode itemNode : itemArray)
                    {
                        GameDTO game = new GameDTO();

                        // Extract title from <name>
                        JsonNode nameNode = itemNode.path("name");
                        if (!nameNode.isMissingNode())
                        {
                            game.setTitle(nameNode.path("").asText(""));
                        }

                        // Extract min/max players from <stats>
                        JsonNode statsNode = itemNode.path("stats");
                        if (!statsNode.isMissingNode())
                        {
                            game.setMinNoOfPlayers(statsNode.path("minplayers").asInt(0));
                            game.setMaxNoOfPlayers(statsNode.path("maxplayers").asInt(0));
                        }

                        // Extract release year
                        game.setReleaseYear(itemNode.path("yearpublished").asInt(0));

                        // Set empty/default values for missing fields
                        game.setDescription("");
                        game.setLanguages(List.of());
                        game.setGenre(null);

                        gameDTOs.add(game);
                    }
                } else
                {
                    System.out.println("No <item> elements found in XML!");
                }

            } else
            {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return gameDTOs;
    }

    // builds a list of URIs, each string has a set amount of Ids based on batchSize
    // number of strings is based on maxId
    private static List<String> buildAllBGGUris()
    {
        List<String> bggUris = new ArrayList<>();

        for (long start = 1; start <= maxId; start += batchSize)
        {
            long end = Math.min(start + batchSize - 1, maxId); // last ID in this batch
            StringBuilder uri = new StringBuilder(bggUri);

            for (long i = start; i <= end; i++)
            {
                uri.append(i);
                if (i < end) // no trailing comma
                {
                    uri.append(",");
                }
            }

            bggUris.add(uri.toString());
        }

        return bggUris;
    }


    public static List<GameDTO> getBGGGamesFromFile()
    {
        List<GameDTO> gameDTOs = new ArrayList<>();

        try
        {
            File xmlFile = new File("src/main/java/dat/service/testdata/bgg_test_data.xml");

            // Read the XML as string and fix unescaped &
            String xml = java.nio.file.Files.readString(xmlFile.toPath(), java.nio.charset.StandardCharsets.UTF_8);
            xml = xml.replaceAll("&(?!amp;)", "&amp;"); // fix unescaped &

            // Parse XML into a JsonNode tree
            JsonNode rootNode = xmlMapper.readTree(xml);

            // Get the <item> array
            JsonNode itemArray = rootNode.path("item");

            if (itemArray.isArray())
            {
                for (JsonNode itemNode : itemArray)
                {
                    GameDTO game = new GameDTO();

                    // Extract title from <name>
                    JsonNode nameNode = itemNode.path("name");
                    if (!nameNode.isMissingNode())
                    {
                        game.setTitle(nameNode.path("").asText("")); // actual text is under the empty key
                    }

                    // Extract min/max players from <stats>
                    JsonNode statsNode = itemNode.path("stats");
                    if (!statsNode.isMissingNode())
                    {
                        game.setMinNoOfPlayers(statsNode.path("minplayers").asInt(0));
                        game.setMaxNoOfPlayers(statsNode.path("maxplayers").asInt(0));
                    }

                    // Extract release year
                    game.setReleaseYear(itemNode.path("yearpublished").asInt(0));

                    // We don’t have description, languages, or genre in XML yet
                    game.setDescription("");  // leave empty
                    game.setLanguages(List.of()); // empty list
                    game.setGenre(null); // leave null

                    gameDTOs.add(game);
                }
            } else
            {
                System.out.println("No <item> elements found in XML!");
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return gameDTOs;
    }

}
