package az.iktex.coreplatform.simasignapi.service;

import org.springframework.stereotype.Service;

@Service
public class ConsoleNotificationService implements NotificationService {

    @Override
    public void sendWelcome(String email) {
        System.out.println("Xos geldin, " + email);
    }
}
