@Entity
public class UserAccount {
    @Id
    private String username;
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Roles> roles = new HashSet<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Collection> collections = new ArrayList<>();
    public UserAccount(String userName, String userPass) {
        this.username = userName;
        this.password = BCrypt.hashpw(userPass, BCrypt.gensalt());
    }
    public UserAccount(String userName, Set<Roles> roleList) {
        this.username = userName;
        this.roles = roleList;
    }
    // ... (other getters and role management methods)
    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw, this.password);
    }
    public void addRole(Roles role) { if (role != null) roles.add(role); }
    public void removeRole(Roles role) { roles.remove(role); }
    public void removeRole(String roleName) { roles.removeIf(r -> r.toString().equals(roleName)); }
    public void addCollection(Collection list) {
        if (list == null) return;
        collections.add(list);
        list.setUser(this);
    }
    public void removeCollection(Collection list) {
        if (list == null) return;
        collections.remove(list);
        list.setUser(null);
    }
}
