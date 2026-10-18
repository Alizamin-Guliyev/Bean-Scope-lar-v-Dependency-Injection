package az.iktex.coreplatform.simasignapi.service;

import az.iktex.coreplatform.simasignapi.dto.request.CreateUserRequest;
import az.iktex.coreplatform.simasignapi.dto.response.UserInfoResponse;
import az.iktex.coreplatform.simasignapi.component.RequestContext;
import az.iktex.coreplatform.simasignapi.component.UserAuditLog;
import az.iktex.coreplatform.simasignapi.repository.UserRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {

    private final ObjectProvider<UserAuditLog> userAuditLogProvider;
    private final UserRepository userRepository;
    private final RequestContext requestContext;
    private NotificationService notificationService;

    public UserInfoService(@Qualifier("mockUserRepository") UserRepository userRepository, ObjectProvider<UserAuditLog> userAuditLogProvider, RequestContext requestContext,
                           List<UserRepository> allRepositories) {
        this.userRepository = userRepository;
        this.userAuditLogProvider = userAuditLogProvider;
        this.requestContext = requestContext;
        System.out.println("UserInfoService yaradildi, hashCode: " + this.hashCode());
        System.out.println("UserRepository bean sayi: " + allRepositories.size());
        allRepositories.forEach(r -> System.out.println(" - " + r.getClass().getSimpleName()));
    }

    @Autowired(required = false)
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public UserInfoResponse getUserById(Long id) {
        System.out.println("[Service] requestId=" + requestContext.getRequestId());
        return userRepository.getUserById(id);
    }

    public List<UserInfoResponse> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public List<UserInfoResponse> getUsersByIds(List<Long> ids) {
        return ids.stream()
                .map(userRepository::getUserById)
                .toList();
    }

    public UserInfoResponse createUser(CreateUserRequest createUserRequest) {
        UserAuditLog userAuditLog = userAuditLogProvider.getObject();
        userAuditLog.setAction("CREATE_USER");
        System.out.println("Audit: auditId=" + userAuditLog.getAuditId() + ", action=" + userAuditLog.getAction());
        UserInfoResponse user = userRepository.createUser(createUserRequest);
        if (notificationService != null) {
            notificationService.sendWelcome(user.getEmail());
        } else {
            System.out.println("NotificationService yoxdur, welcome gonderilmedi");
        }
        return user;
    }
}
