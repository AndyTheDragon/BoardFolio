package dat.dto;

import dat.entities.Game;
import dat.enums.Genre;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameDTO
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;
    private String title;
    private String description;
    private int minNoOfPlayers;
    private int maxNoOfPlayers;
    private int minAge;
    private int maxAge;
    private int releaseYear;
    @ElementCollection
    @CollectionTable(name = "game_languages", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "language")
    private List<String> languages;
    private Genre genre;

    public GameDTO(Game game)
    {
        this.gameId = game.getGameId();
        this.title = game.getTitle();
        this.description = game.getDescription();
        this.minNoOfPlayers = game.getMinNoOfPlayers();
        this.maxNoOfPlayers = game.getMaxNoOfPlayers();
        this.minAge = game.getMinAge();
        this.maxAge = game.getMaxAge();
        this.releaseYear = game.getReleaseYear();
        this.languages = game.getLanguages();
        this.genre = game.getGenre();
    }

    public void titleDTO(Game game)
    {
        this.title = game.getTitle();
    }


}
