package az.iktex.coreplatform.simasignapi.repository;

import az.iktex.coreplatform.simasignapi.dto.request.CreateUserRequest;
import az.iktex.coreplatform.simasignapi.dto.response.UserInfoResponse;

import java.util.List;

public interface UserRepository {

    UserInfoResponse getUserById(Long id);

    List<UserInfoResponse> getAllUsers();

    UserInfoResponse createUser(CreateUserRequest request);
}
