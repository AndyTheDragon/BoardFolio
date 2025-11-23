package dat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dat.enums.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserAccount
{
    @Id
    private String username;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Roles> roles = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @JsonIgnore
    private GameList myCollection = new GameList();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @ToString.Exclude
    private List<GameList> gameLists = new ArrayList<>();

    public UserAccount(String userName, String userPass)
    {
        this.username = userName;
        this.password = BCrypt.hashpw(userPass, BCrypt.gensalt());
        this.myCollection = new GameList();
        this.myCollection.setUser(this);
    }

    public UserAccount(String userName, Set<Roles> roleEntityList)
    {
        this.username = userName;
        this.roles = roleEntityList;
        this.myCollection = new GameList();
        this.myCollection.setUser(this);
    }

    public Set<String> getRolesAsString()
    {
        return roles.stream().map(Roles::toString).collect(Collectors.toSet());
    }


    public boolean verifyPassword(String pw)
    {
        return BCrypt.checkpw(pw, this.password);
    }


    public void addRole(Roles role)
    {
        if (role != null)
        {
            roles.add(role);
        }
    }

    public void removeRole(Roles role)
    {
        roles.remove(role);
    }

    public void removeRole(String roleName)
    {
        //roles.remove(Roles.valueOf(roleName.toUpperCase()));
        roles.removeIf(r -> r.toString().equals(roleName));
    }

    public void addToMyCollection(Game newGame)
    {
        if (myCollection == null)
        {
            myCollection = new GameList();
            myCollection.setUser(this);
        }
        myCollection.addGame(newGame);
    }


    public void removeFromMyCollection(Game oldGame)
    {
        myCollection.removeGame(oldGame);
    }


    public void addList(GameList list)
    {
        if (list == null)
        {
            return;
        }
        gameLists.add(list);
        list.setUser(this);
    }

    public void removeList(GameList list)
    {
        if (list == null)
        {
            return;
        }
        gameLists.remove(list);
        list.setUser(null);
    }
}