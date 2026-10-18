package az.iktex.coreplatform.simasignapi.repository;

import az.iktex.coreplatform.simasignapi.component.RequestContext;
import az.iktex.coreplatform.simasignapi.dto.request.CreateUserRequest;
import az.iktex.coreplatform.simasignapi.dto.response.UserInfoResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@ConditionalOnProperty(name = "app.repository-type", havingValue = "memory", matchIfMissing = true)
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, UserInfoResponse> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final RequestContext requestContext;

    public InMemoryUserRepository(RequestContext requestContext) {
        this.requestContext = requestContext;
        System.out.println("InMemoryUserRepository yaradildi, hashCode: " + this.hashCode());
    }

    @PostConstruct
    public void init() {
        saveInitialUser("Ali Aliyev", "ali@example.com");
        saveInitialUser("Vali Valiyev", "vali@example.com");
        saveInitialUser("Leyla Mammadova", "leyla@example.com");
        System.out.println("UserRepository hazirdir, " + storage.size() + " user yuklendi");
    }

    @PreDestroy
    public void destroy() {
        int count = storage.size();
        storage.clear();
        System.out.println("UserRepository baglanir, " + count + " user silindi, storage temizlendi");
    }

    private void saveInitialUser(String fullName, String email) {
        long id = idGenerator.incrementAndGet();
        storage.put(id, UserInfoResponse.builder()
                .id(id)
                .fullName(fullName)
                .email(email)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Override
    public UserInfoResponse getUserById(Long id){
        System.out.println("[Repository] requestId=" + requestContext.getRequestId());
        UserInfoResponse user = storage.get(id);
        if (user == null) {
            throw new NoSuchElementException("User tapilmadi, id: " + id);
        }
        return user;
    }

    @Override
    public List<UserInfoResponse> getAllUsers() {
        return List.of(getUserById(1L), getUserById(2L), getUserById(3L));
    }

    @Override
    public UserInfoResponse createUser(CreateUserRequest request) {
        long id = idGenerator.incrementAndGet();

        UserInfoResponse user = UserInfoResponse.builder()
                .id(id)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        storage.put(id, user);

        return user;
    }


//    public UserInfoResponse createUser(CreateUserRequest createUserRequest){
////        UserInfoResponse user = new UserInfoResponse();
////        user.setFullName(createUserRequest.getFullName());
////        user.setEmail(createUserRequest.getEmail());
////        user.setActive(true);
////        user.setCreatedAt(LocalDateTime.now());
////        return user;
//
//        return UserInfoResponse.builder()
//                .id(idGenerator.incrementAndGet())
//                .fullName(createUserRequest.getFullName())
//                .email(createUserRequest.getEmail())
//                .active(true)
//                .createdAt(LocalDateTime.now())
//                .build();
//    }
}
