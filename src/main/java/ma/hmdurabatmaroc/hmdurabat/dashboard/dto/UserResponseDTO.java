package ma.hmdurabatmaroc.hmdurabat.dashboard.dto;

import java.util.List;

public class UserResponseDTO {
    private Long id;
    private String username;
    private List<String> roles;

    public UserResponseDTO() {}

    public UserResponseDTO(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
