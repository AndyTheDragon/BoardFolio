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
import java.util.List;

// BGG = Board Game Geek
// API URL: https://boardgamegeek.com/using_the_xml_api
// API2 Docs: https://boardgamegeek.com/wiki/page/BGG_XML_API2
public class BoardGameGeekService
{
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final XmlMapper xmlMapper = new XmlMapper();
    private static final String bggUri = "https://boardgamegeek.com/xmlapi/collection/eekspider";  //TODO this is a temporary URI for getting mock-data

    public static List<GameDTO> getBGGGames()
    {
        List<GameDTO> gameDTOs = new ArrayList<>();

        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(bggUri))
                    .header("Authorization", "Bearer YOUR_TOKEN_HERE") // TODO: insert API key here as a secret variable
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
