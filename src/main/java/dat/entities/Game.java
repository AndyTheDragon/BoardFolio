@Entity
public class Game {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;
    private String title;
    private String description;
    private int minNoOfPlayers;
    private int maxNoOfPlayers;
    private int minAge;
    private int maxAge;
    private int releaseYear;
    @ManyToMany(mappedBy = "games")
    private Set<Collection> collections = new HashSet<>();
    @ElementCollection(targetClass = Languages.class)
    @Enumerated(EnumType.STRING)
    private List<Languages> languages;
    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    private Set<Genre> genres = new HashSet<>();
    public Game(String title, String description, int minNoOfPlayers, int maxNoOfPlayers,
                int minAge, int maxAge, int releaseYear,
                List<Languages> languages, Set<Collection> collections, Set<Genre> genres) {
        this.title = title;
        this.description = description;
        this.minNoOfPlayers = minNoOfPlayers;
        this.maxNoOfPlayers = maxNoOfPlayers;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.releaseYear = releaseYear;
        this.languages = languages;
        this.genres = genres;
        if (collections != null) {
            for (Collection col : collections) {
                this.addToCollection(col);
            }
        }
    }
    public void addToCollection(Collection collection) {
        if (collection == null) return;
        collections.add(collection);
        collection.getGames().add(this);
    }
    public void removeFromCollection(Collection collection) {
        if (collection == null) return;
        collections.remove(collection);
        collection.getGames().remove(this);
    }
}
