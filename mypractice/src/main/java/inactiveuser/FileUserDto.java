package inactiveuser;

import java.time.LocalDateTime;

public class FileUserDto {

    private Long id;
    private String name;
    private String status;
    private LocalDateTime updatedTime;

    public FileUserDto() {
    }

    public FileUserDto(Long id, String name, String status, LocalDateTime updatedTime) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.updatedTime = updatedTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserStatus getStatus() {
        return UserStatus.valueOf(status);
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
}
