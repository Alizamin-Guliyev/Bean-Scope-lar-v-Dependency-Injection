package az.iktex.coreplatform.simasignapi.component;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Component
@Scope("prototype")
public class UserAuditLog {
    private final UUID auditId = UUID.randomUUID();
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Setter
    private String action;


    public UserAuditLog() {
        System.out.println("UserAuditLog yaradildi: " + auditId);
    }

    @PreDestroy
    public void destroy() {
        System.out.println("UserAuditLog destroy: " + auditId);
    }

}
