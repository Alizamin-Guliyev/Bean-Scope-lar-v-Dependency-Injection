package az.iktex.coreplatform.simasignapi.repository;

import az.iktex.coreplatform.simasignapi.dto.request.CreateUserRequest;
import az.iktex.coreplatform.simasignapi.dto.response.UserInfoResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@ConditionalOnProperty(name = "app.repository-type", havingValue = "mock")
public class MockUserRepository implements UserRepository {

    @Override
    public UserInfoResponse getUserById(Long id) {
        return mockUser(id);
    }

    @Override
    public List<UserInfoResponse> getAllUsers() {
        return List.of(mockUser(1L), mockUser(2L));
    }

    @Override
    public UserInfoResponse createUser(CreateUserRequest request) {
        return mockUser(0L);
    }

    private UserInfoResponse mockUser(long id) {
        return UserInfoResponse.builder()
                .id(id)
                .fullName("Mock User")
                .email("mock@example.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
