class EntityDAOTest {
    // ... (setup of EntityManagerFactory and DAO)
    private Game testGame;
    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // Clean up existing data
            em.createNativeQuery("DELETE FROM game_genres").executeUpdate();
            em.createNativeQuery("DELETE FROM game_languages").executeUpdate();
            em.createNativeQuery("DELETE FROM collection_game").executeUpdate();
            em.createNativeQuery("DELETE FROM game").executeUpdate();
            em.createNativeQuery("DELETE FROM collection").executeUpdate();
            em.createNativeQuery("DELETE FROM useraccount_roles").executeUpdate();
            em.createNativeQuery("DELETE FROM useraccount").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE game_gameid_seq RESTART WITH 1").executeUpdate();
            // Create test data
            String testTitle = "Test Title";
            String testDescription = "Test description";
            // ... (other primitive fields)
            List<Languages> testLanguages = new ArrayList<>();
            testLanguages.add(Languages.ENGLISH);
            testLanguages.add(Languages.DANISH);
            Set<Genre> testGenres = new HashSet<>();
            testGenres.add(Genre.ADVENTURE);
            Set<Collection> testCollections = new HashSet<>();
            Collection testCollection = new Collection("TestCollectionName");
            testCollections.add(testCollection);
            testGame = new Game(testTitle, testDescription, 2, 4, 10, 99, 2000,
                    testLanguages, testCollections, testGenres);
            testCollection.addGame(testGame);
            em.persist(testCollection);
            em.persist(testGame);
            em.getTransaction().commit();
        }
    }
    @Test
    void getGame() {
        Game result = genericDAO.getById(Game.class, 1);
        assertNotNull(result);
        assertEquals(testGame.getTitle(), result.getTitle());
        assertEquals(testGame.getMinNoOfPlayers(), result.getMinNoOfPlayers());
    }
    @Test
    void createGame() {
        // Create a game without associating it to a collection (like adding a new library game)
        List<Languages> langs = new ArrayList<>();
        langs.add(Languages.ENGLISH);
        langs.add(Languages.DANISH);
        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.ADVENTURE);
        Game testGame2 = new Game("Test Title 2", "Test description 2", 2, 4, 10, 99, 2000,
                langs, new HashSet<>(), genres);
        Game result = genericDAO.create(testGame2);
        assertNotNull(result);
        assertEquals(2, result.getGameId());
        assertEquals(testGame2.getTitle(), result.getTitle());
        assertEquals(testGame2.getLanguages().size(), result.getLanguages().size());
        assertTrue(result.getGenres().contains(Genre.ADVENTURE));
    }
}
