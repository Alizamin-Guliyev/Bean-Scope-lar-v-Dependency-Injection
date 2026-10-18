package az.iktex.coreplatform.simasignapi.repository;

import az.iktex.coreplatform.simasignapi.dto.request.CreateUserRequest;
import az.iktex.coreplatform.simasignapi.dto.response.UserInfoResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    // Sual: Adi HashMap ve long counter burda niye tehlukelidir? Singleton bean-i eyni anda nece thread istifade edir?
    // Cavab:
    // - UserRepository singleton-dur: butun tetbiqde 1 obyekt var. Tomcat her HTTP sorgunu ayri thread-de
    //   isleyir (default thread pool max 200). Yeni eyni anda gelen sorgularin hamisi (200-e qeder thread)
    //   EYNI obyekti ve EYNI storage/sayaci paralel istifade edir.
    // - long counter: "id++" bir emeliyyat deyil, 3 addimdir: oxu -> 1 artir -> geri yaz. Iki thread eyni anda
    //   5 oxusa, ikisi de 6 yazacaq. Iki user eyni ID alir, biri o birini Map-de silib ustune yazir (data itir).
    //   Hemcinin bir thread-in yazdigi deyeri diger thread gormeye biler (visibility problemi).
    // - HashMap: paralel put() zamani daxili strukturu (bucket-ler, resize) pozula biler: elementler itir,
    //   size() sehv olur, get() null qaytarir, hetta sonsuz dovr/exception ola biler.
    // - Hell: ConcurrentHashMap paralel oxuma/yazmani tehlukesiz edir, AtomicLong.incrementAndGet() ise artirmani
    //   CAS ile tek, bolunmez (atomik) addimda edir, ona gore her thread ferqli ID alir.
    private final Map<Long, UserInfoResponse> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public UserRepository() {
        System.out.println("UserRepository yaradildi, hashCode: " + this.hashCode());
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

    public UserInfoResponse getUserById(Long id){
        UserInfoResponse user = storage.get(id);
        if (user == null) {
            throw new NoSuchElementException("User tapilmadi, id: " + id);
        }
        return user;
    }

    public List<UserInfoResponse> getAllUsers() {
        return List.of(getUserById(1L), getUserById(2L), getUserById(3L));
    }

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
