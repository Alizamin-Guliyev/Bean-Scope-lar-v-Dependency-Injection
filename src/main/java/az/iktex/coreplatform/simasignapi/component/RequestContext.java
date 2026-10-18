package az.iktex.coreplatform.simasignapi.component;

import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Getter
@Component
//@RequestScope
@Scope(
        value = "request",
        proxyMode = ScopedProxyMode.TARGET_CLASS
)
public class RequestContext {
    private final String requestId = UUID.randomUUID().toString().substring(0, 8);
    private final long startTime = System.currentTimeMillis();
}
