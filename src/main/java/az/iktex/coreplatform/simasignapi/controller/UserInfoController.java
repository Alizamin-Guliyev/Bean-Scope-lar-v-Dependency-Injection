package az.iktex.coreplatform.simasignapi.controller;

import az.iktex.coreplatform.simasignapi.component.RequestContext;
import az.iktex.coreplatform.simasignapi.dto.request.CreateUserRequest;
import az.iktex.coreplatform.simasignapi.dto.response.UserInfoResponse;
import az.iktex.coreplatform.simasignapi.component.RecentlyViewedUsers;
import az.iktex.coreplatform.simasignapi.service.UserInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-info")
public class UserInfoController {
    private final UserInfoService userInfoService;
    private final RecentlyViewedUsers recentlyViewedUsers;
    private final RequestContext requestContext;

    public UserInfoController(UserInfoService userInfoService, RecentlyViewedUsers recentlyViewedUsers, RequestContext requestContext) {
        this.userInfoService = userInfoService;
        this.recentlyViewedUsers = recentlyViewedUsers;
        this.requestContext = requestContext;
        System.out.println("UserInfoController yaradildi, hashCode: " + this.hashCode());
    }

    @GetMapping("/{id}")
    public UserInfoResponse getUserById(@PathVariable Long id){
        System.out.println("[Controller] requestId=" + requestContext.getRequestId());
        System.out.println("[Controller] requestContext class: " + requestContext.getClass().getName());
        UserInfoResponse response = userInfoService.getUserById(id);
        recentlyViewedUsers.addUser(id);
        logDuration();
        return response;
    }

    @GetMapping("/all")
    public List<UserInfoResponse> getAllUsers(){
        List<UserInfoResponse> response = userInfoService.getAllUsers();
        logDuration();
        return response;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserInfoResponse createUser(@RequestBody CreateUserRequest createUserRequest){
        UserInfoResponse response = userInfoService.createUser(createUserRequest);
        logDuration();
        return response;
    }

    @GetMapping("/recent")
    public List<UserInfoResponse> getRecentUsers(){
        List<UserInfoResponse> response = userInfoService.getUsersByIds(recentlyViewedUsers.getUsers());
        logDuration();
        return response;
    }

    private void logDuration() {
        long duration = System.currentTimeMillis() - requestContext.getStartTime();
        System.out.println("[Controller] requestId=" + requestContext.getRequestId() + " " + duration + " ms cekdi");
    }
}
