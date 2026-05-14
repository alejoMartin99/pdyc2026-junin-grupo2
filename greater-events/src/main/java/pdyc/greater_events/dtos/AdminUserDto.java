package pdyc.greater_events.dtos;

public class AdminUserDto {

    private String id;
    private String username;
    private boolean enabled;

    public AdminUserDto() {
    }

    public AdminUserDto(String id, String username, boolean enabled) {
        this.id = id;
        this.username = username;
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
