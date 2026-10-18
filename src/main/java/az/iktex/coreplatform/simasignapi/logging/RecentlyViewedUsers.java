package az.iktex.coreplatform.simasignapi.logging;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
@SessionScope
public class RecentlyViewedUsers {
    private final List<Long> users = new ArrayList<>();

    public void addUser(long userId) {
        users.remove(userId);
        users.add(0,userId);
        if(users.size()>5){
            users.remove(5);
        }
    }
}
