package dat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dat.entities.Game;
import dat.enums.Genre;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameDTO
{
    @JsonProperty("bgg_API_ID")
    private Long BGG_API_ID;
    private String title;
    private String image;
    private String thumbnail;
    private String description;
    private int minNoOfPlayers;
    private int maxNoOfPlayers;
    private int minAge;
    private int releaseYear;
    private Set<String> genres;


    public Game toEntity(GameDTO gameDTO)
    {
        Set<Genre> genreEnums = gameDTO.getGenres().stream()
                .map(genre -> genre.trim()
                        .replaceAll("\\s+", "_")
                        .replaceAll("'", "")
                        .toUpperCase())
                .map(Genre::valueOf)
                .collect(Collectors.toSet());


        Game game = Game.builder()
                .gameId(gameDTO.getBGG_API_ID())
                .title(gameDTO.getTitle())
                .description(gameDTO.getDescription())
                .minNoOfPlayers(gameDTO.getMinNoOfPlayers())
                .maxNoOfPlayers(gameDTO.getMaxNoOfPlayers())
                .minAge(gameDTO.getMinAge())
                .releaseYear(gameDTO.getReleaseYear())
                .genres(genreEnums)
                .imageURL(gameDTO.getImage())
                .thumbnailURL(gameDTO.getThumbnail())
                .build();

        return game;
    }
}