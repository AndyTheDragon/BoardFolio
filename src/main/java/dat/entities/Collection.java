package dat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Collection
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer listID;

    private String name;

    @ManyToMany
    private Set<Game> games = new HashSet<>();

    @ManyToOne
    private UserAccount user;

    public Collection(String name)
    {
        this.name = name;
    }

    public void addGame(Game game)
    {
        this.games.add(game);
        game.addToCollection(this);

    }

    public void removeGame(Game game)
    {
        this.games.remove(game);
    }
}
