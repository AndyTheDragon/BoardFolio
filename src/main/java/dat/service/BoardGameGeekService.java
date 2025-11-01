package dat.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.JsonNode;
import dat.dto.GameDTO;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// BGG = Board Game Geek
// API URL: https://boardgamegeek.com/using_the_xml_api
// API2 Docs: https://boardgamegeek.com/wiki/page/BGG_XML_API2
public class BoardGameGeekService
{
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final XmlMapper XML_MAPPER = new XmlMapper();
    private static final String BGG_URI = "https://boardgamegeek.com/xmlapi2/thing?id=";
    private static final String BGG_API_KEY = System.getenv("BGG_API_KEY"); //TODO setup API Key as a secret system variable
    // The max number of IDs from BGG
    private static final Long MAX_ID = 457416L;
    // The max number of games per request
    private static final int BATCH_SIZE = 20;
    // BGGs min rate limit per request
    private static final int RATE_LIMIT_MS = 5000;

    // Public method to start fetching all games
    public static List<GameDTO> fetchAllGames()
    {
        List<String> uris = buildAllBGGUris();
        List<GameDTO> allGames = new ArrayList<>();

        long totalRequests = uris.size();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < uris.size(); i++)
        {
            String uriStr = uris.get(i);
            try
            {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(uriStr))
                        .header("Authorization", "Bearer " + BGG_API_KEY)
                        .GET()
                        .build();

                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200)
                {
                    String xml = response.body().replaceAll("&(?!amp;)", "&amp;");

                    Future<List<GameDTO>> future = executor.submit(() -> parseBatchOfGames(xml));

                    allGames.addAll(future.get()); // wait for batch parsing

                    // Progress info
                    long requestsDone = i + 1;
                    long requestsLeft = totalRequests - requestsDone;

                    long estimatedRemainingMs = requestsLeft * RATE_LIMIT_MS;

                    // convert milliseconds to hours, minutes, seconds
                    long secondsTotal = estimatedRemainingMs / 1000;
                    long hours = secondsTotal / 3600;
                    long minutes = (secondsTotal % 3600) / 60;
                    long seconds = secondsTotal % 60;

                    // overwrite the same line using \r and flush the output
                    System.out.printf(
                            "\rProcessed batch %d/%d, total games fetched: %d, estimated time remaining: %dh %dm %ds",
                            requestsDone, totalRequests, allGames.size(), hours, minutes, seconds
                    );
                    System.out.flush();

                } else
                {
                    System.out.println("GET failed: " + response.statusCode());
                }

                Thread.sleep(RATE_LIMIT_MS);

            } catch (Exception e)
            {
                System.out.println("Error fetching games from Board Game Geek!");
                e.printStackTrace();
            }
        }

        executor.shutdown();
        return allGames;
    }

    // Parse a batch of XML into GameDTOs
    private static List<GameDTO> parseBatchOfGames(String xml) throws Exception
    {
        List<GameDTO> games = new ArrayList<>();
        JsonNode root = XML_MAPPER.readTree(xml);
        JsonNode itemsNode = root.path("item");

        if (!itemsNode.isMissingNode())
        {
            if (itemsNode.isArray())
            {
                for (JsonNode itemNode : itemsNode)
                {
                    games.add(parseGame(itemNode));
                }
            } else
            {
                games.add(parseGame(itemsNode));
            }
        }

        return games;
    }

    private static GameDTO parseGame(JsonNode itemNode)
    {
        GameDTO game = new GameDTO();

        game.setBGG_API_ID(itemNode.path("id").asLong());
        game.setTitle(itemNode.path("name").path("value").asText(""));
        game.setDescription(itemNode.path("description").asText(""));
        game.setMinNoOfPlayers(itemNode.path("minplayers").path("value").asInt(0));
        game.setMaxNoOfPlayers(itemNode.path("maxplayers").path("value").asInt(0));
        game.setMinAge(itemNode.path("minage").path("value").asInt(0));
        game.setReleaseYear(itemNode.path("yearpublished").path("value").asInt(0));
        game.setImage(itemNode.path("image").asText(""));
        game.setThumbnail(itemNode.path("thumbnail").asText(""));

        // Parse <link type="boardgamecategory" ...> elements into genres map
        Map<Long, String> genres = new HashMap<>();
        Iterator<JsonNode> links = itemNode.findValues("link").iterator();
        while (links.hasNext())
        {
            JsonNode linkNode = links.next();
            String type = linkNode.path("type").asText();
            if ("boardgamecategory".equals(type))
            {
                long id = linkNode.path("id").asLong();
                String value = linkNode.path("value").asText();
                genres.put(id, value);
            }
        }
        game.setGenres(genres);

        return game;
    }

    // builds a list of URIs, each string has a set amount of Ids based on batchSize
    // number of strings is based on maxId
    private static List<String> buildAllBGGUris()
    {
        List<String> bggUris = new ArrayList<>();

        for (long start = 1; start <= MAX_ID; start += BATCH_SIZE)
        {
            long end = Math.min(start + BATCH_SIZE - 1, MAX_ID); // last ID in this batch
            StringBuilder uri = new StringBuilder(BGG_URI);

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


    //TODO was only for testing, remove later
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
            JsonNode rootNode = XML_MAPPER.readTree(xml);

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
