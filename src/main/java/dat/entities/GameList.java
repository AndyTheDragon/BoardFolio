package dat.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamicUpdate
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

    private LocalDateTime createdDate;
    private boolean isPublic;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @Setter
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
        this.customList.add(game);

    }

    public void removeGame(Game game)
    {
        if (game == null)
        {
            return;
        }
        this.customList.remove(game);
    }
}