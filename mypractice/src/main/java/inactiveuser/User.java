package inactiveuser;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    private Long id;
    private String name;
    private String status;
    private LocalDateTime updatedTime;

    public User() {
    }

    public User(Long id, String name, UserStatus status, LocalDateTime updatedTime) {
        this.id = id;
        this.name = name;
        this.status = String.valueOf(status);
        this.updatedTime = updatedTime;
    }

    public User toInactive() {
        this.status = "INACTIVE";
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
}
