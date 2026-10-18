package az.iktex.coreplatform.simasignapi.logging;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Getter
@Component
@RequestScope
public class RequestContext {
    private final String traceId= UUID.randomUUID().toString();
}
