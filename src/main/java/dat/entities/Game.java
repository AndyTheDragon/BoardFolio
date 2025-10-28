package dat.entities;

import dat.enums.Genre;
import dat.enums.Languages;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Game
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

    @ElementCollection(targetClass = Languages.class)
    @Enumerated(EnumType.STRING)
    private List<Languages> languages;

    @ManyToMany
    private Set<Collection> collections = new HashSet<>();

    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    private Set<Genre> genres = new HashSet<>();
}