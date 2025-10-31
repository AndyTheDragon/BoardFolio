package dat.dto;

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

    public GameDTO(String title, String description, int minNoOfPlayers, int maxNoOfPlayers, int minAge, int maxAge, int releaseYear, List<String> languages, Genre genre)
    {
        this.title = title;
        this.description = description;
        this.minNoOfPlayers = minNoOfPlayers;
        this.maxNoOfPlayers = maxNoOfPlayers;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.releaseYear = releaseYear;
        this.languages = languages;
        this.genre = genre;
    }

    public void titleDTO(String title)
    {
        this.title = title;
    }


}
