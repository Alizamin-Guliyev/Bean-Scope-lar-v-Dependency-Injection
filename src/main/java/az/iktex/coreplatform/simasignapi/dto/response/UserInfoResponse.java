package az.iktex.coreplatform.simasignapi.dto.response;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserInfoResponse {
    private long id;
    private String fullName;
    private String email;
    private boolean active;
    private LocalDateTime createdAt;

//    public UserInfoDto() {
//    }
//
//    public UserInfoDto(long id, String fullName, String email, boolean active, LocalDateTime createdAt) {
//        this.id = id;
//        this.fullName = fullName;
//        this.email = email;
//        this.createdAt = createdAt;
//        this.active = active;
//    }
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public String getFullName() {
//        return fullName;
//    }
//
//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public boolean isActive() {
//        return active;
//    }
//
//    public void setActive(boolean active) {
//        this.active = active;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
}
