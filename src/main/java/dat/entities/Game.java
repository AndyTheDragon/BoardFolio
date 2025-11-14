package dat.entities;

import dat.dto.GameDTO;
import dat.enums.Genre;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    private String title;
    @Lob
    private String description;
    private int minAge;
    private int minNoOfPlayers;
    private int maxNoOfPlayers;
    private int releaseYear;

    private String imageURL;
    private String thumbnailURL;

    @ElementCollection(targetClass = Genre.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Genre> genres = new HashSet<>();

    public Game(String title,
                String description,
                int minNoOfPlayers,
                int maxNoOfPlayers,
                int releaseYear,
                String imageURL,
                String thumbnailURL,
                Set<Genre> genres)
    {
        this.title = title;
        this.description = description;
        this.minNoOfPlayers = minNoOfPlayers;
        this.maxNoOfPlayers = maxNoOfPlayers;
        this.releaseYear = releaseYear;
        this.imageURL = imageURL;
        this.thumbnailURL = thumbnailURL;
        this.genres = genres;
    }

    public GameDTO toDTO(Game game)
    {
        Set<String> genreStrings = game.getGenres().stream()
                                       .map(Enum::name)
                                       .map(name -> name.replaceAll("_", " "))
                                       .map(name -> name.substring(0, 1).toUpperCase() + name.substring(1)
                                                                                             .toLowerCase())
                                       .collect(Collectors.toSet());

        GameDTO gameDTO = new GameDTO();
        gameDTO.setTitle(game.getTitle());
        gameDTO.setDescription(game.getDescription());
        gameDTO.setMinNoOfPlayers(game.getMinNoOfPlayers());
        gameDTO.setMaxNoOfPlayers(game.getMaxNoOfPlayers());
        gameDTO.setMinAge(game.getMinAge());
        gameDTO.setReleaseYear(game.getReleaseYear());
        gameDTO.setGenres(genreStrings);
        gameDTO.setImage(game.getImageURL());
        gameDTO.setThumbnail(game.getThumbnailURL());

        return gameDTO;
    }

}