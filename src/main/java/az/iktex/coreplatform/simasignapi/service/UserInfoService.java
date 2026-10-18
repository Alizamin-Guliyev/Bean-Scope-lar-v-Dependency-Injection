package az.iktex.coreplatform.simasignapi.service;

import az.iktex.coreplatform.simasignapi.dto.request.CreateUserRequest;
import az.iktex.coreplatform.simasignapi.dto.response.UserInfoResponse;
import az.iktex.coreplatform.simasignapi.logging.RequestContext;
import az.iktex.coreplatform.simasignapi.logging.UserAuditLog;
import az.iktex.coreplatform.simasignapi.repository.UserRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {

    private final ObjectProvider<UserAuditLog> userAuditLog;
    private final UserRepository userRepository;
    private final RequestContext requestContext;

    public UserInfoService(UserRepository userRepository, ObjectProvider<UserAuditLog> userAuditLog, RequestContext requestContext) {
        this.userRepository = userRepository;
        this.userAuditLog = userAuditLog;
        this.requestContext = requestContext;
        System.out.println("UserInfoService yaradildi, hashCode: " + this.hashCode());
    }

    public UserInfoResponse getUserById(Long id) {
        UserAuditLog userAuditLogProvider = userAuditLog.getObject();
        // System.out.println("AuditID" + userAuditLogProvider.getAuditId());
        // System.out.println("TraceID" + requestContext.getTraceId());
        return userRepository.getUserById(id);
    }

    public List<UserInfoResponse> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public UserInfoResponse createUser(CreateUserRequest createUserRequest) {
        return userRepository.createUser(createUserRequest);
    }
}
