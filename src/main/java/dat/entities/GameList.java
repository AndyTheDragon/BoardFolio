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
public class GameList
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer listID;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "custom_list",
            joinColumns = @JoinColumn(name = "list_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    @ToString.Exclude
    private Set<Game> customList = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private UserAccount user;

    public GameList(String name)
    {
        this.name = name;
    }

    public void addGame(Game game)
    {
        if (game == null)
        {
            return;
        }
        customList.add(game);
        game.getGameLists().add(this);

    }

    public void removeGame(Game game)
    {
        if (game == null)
        {
            return;
        }
        customList.remove(game);
        game.getGameLists().remove(this);
    }
}