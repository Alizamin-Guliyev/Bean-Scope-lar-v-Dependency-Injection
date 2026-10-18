package az.iktex.coreplatform.simasignapi.service;

import az.iktex.coreplatform.simasignapi.dto.request.CreateUserRequest;
import az.iktex.coreplatform.simasignapi.dto.response.UserInfoResponse;
import az.iktex.coreplatform.simasignapi.logging.RequestContext;
import az.iktex.coreplatform.simasignapi.component.UserAuditLog;
import az.iktex.coreplatform.simasignapi.repository.UserRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {

    private final ObjectProvider<UserAuditLog> userAuditLogProvider;
    private final UserRepository userRepository;
    private final RequestContext requestContext;

    public UserInfoService(UserRepository userRepository, ObjectProvider<UserAuditLog> userAuditLogProvider, RequestContext requestContext) {
        this.userRepository = userRepository;
        this.userAuditLogProvider = userAuditLogProvider;
        this.requestContext = requestContext;
        System.out.println("UserInfoService yaradildi, hashCode: " + this.hashCode());
    }

    public UserInfoResponse getUserById(Long id) {
        // System.out.println("TraceID" + requestContext.getTraceId());
        return userRepository.getUserById(id);
    }

    public List<UserInfoResponse> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public UserInfoResponse createUser(CreateUserRequest createUserRequest) {
        UserAuditLog userAuditLog = userAuditLogProvider.getObject();
        userAuditLog.setAction("CREATE_USER");
        System.out.println("Audit: auditId=" + userAuditLog.getAuditId() + ", action=" + userAuditLog.getAction());
        return userRepository.createUser(createUserRequest);
    }
}
