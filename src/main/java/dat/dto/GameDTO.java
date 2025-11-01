package dat.dto;

import dat.enums.Genre;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@ToString
public class GameDTO
{
    private Long BGG_API_ID;
    private String title;
    private String description;
    private int minNoOfPlayers;
    private int maxNoOfPlayers;
    private int minAge;
    private int intMaxAge;
    private int releaseYear;
    private List<String> languages;
    private Genre genre;
}
