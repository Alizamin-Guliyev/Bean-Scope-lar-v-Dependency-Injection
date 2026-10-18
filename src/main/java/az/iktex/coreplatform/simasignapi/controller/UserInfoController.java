package az.iktex.coreplatform.simasignapi.controller;

import az.iktex.coreplatform.simasignapi.dto.request.CreateUserRequest;
import az.iktex.coreplatform.simasignapi.dto.response.UserInfoResponse;
import az.iktex.coreplatform.simasignapi.logging.RecentlyViewedUsers;
import az.iktex.coreplatform.simasignapi.service.UserInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-info")
public class UserInfoController {
    private final UserInfoService userInfoService;
    private final RecentlyViewedUsers recentlyViewedUsers;

    public UserInfoController(UserInfoService userInfoService, RecentlyViewedUsers recentlyViewedUsers) {
        this.userInfoService = userInfoService;
        this.recentlyViewedUsers = recentlyViewedUsers;
        System.out.println("UserInfoController yaradildi, hashCode: " + this.hashCode());
    }

    @GetMapping("/{id}")
    public UserInfoResponse getUserById(@PathVariable Long id){
        recentlyViewedUsers.addUser(id);
        return userInfoService.getUserById(id);
    }

    @GetMapping("/all")
    public List<UserInfoResponse> getAllUsers(){
        return userInfoService.getAllUsers();
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserInfoResponse createUser(@RequestBody CreateUserRequest createUserRequest){
        return userInfoService.createUser(createUserRequest);
    }

    @GetMapping("/recent")
    public List<Long> getUsersIdsRecent(){ return recentlyViewedUsers.getUsers();}
}
