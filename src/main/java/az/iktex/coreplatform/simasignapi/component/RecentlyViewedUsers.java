package az.iktex.coreplatform.simasignapi.component;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
@SessionScope
public class RecentlyViewedUsers {
    private static final int MAX_SIZE = 5;
    private final LinkedList<Long> users = new LinkedList<>();

    public synchronized void addUser(Long userId) {
        users.remove(userId);
        users.addFirst(userId);
        if (users.size() > MAX_SIZE) {
            users.removeLast();
        }
    }

    public synchronized List<Long> getUsers() {
        return new ArrayList<>(users);
    }

    @PreDestroy
    public void destroy() {
        System.out.println("RecentlyViewedUsers destroy, session bitdi: " + users);
    }
}
