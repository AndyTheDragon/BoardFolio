package dat.dto;

import dat.enums.Genre;
import java.util.List;

public class GameDTO
{
    private String title;
    private String description;
    private int minNoOfPlayers;
    private int maxNoOfPlayers;
    private int releaseYear;
    private List<String> languages;
    private Genre genre;
}
