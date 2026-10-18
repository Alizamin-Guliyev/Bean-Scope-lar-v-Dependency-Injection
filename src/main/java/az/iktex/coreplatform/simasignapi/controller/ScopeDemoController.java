package az.iktex.coreplatform.simasignapi.controller;

import az.iktex.coreplatform.simasignapi.component.RequestContext;
import az.iktex.coreplatform.simasignapi.component.UserAuditLog;
import az.iktex.coreplatform.simasignapi.service.UserInfoService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scope-demo")
public class ScopeDemoController {

    private final UserInfoService userInfoService;
    private final ObjectProvider<UserAuditLog> userAuditLogProvider;
    private final RequestContext requestContext;

    public ScopeDemoController(UserInfoService userInfoService, ObjectProvider<UserAuditLog> userAuditLogProvider, RequestContext requestContext) {
        this.userInfoService = userInfoService;
        this.userAuditLogProvider = userAuditLogProvider;
        this.requestContext = requestContext;
    }

    @GetMapping
    public Map<String, Object> scopeDemo() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("singletonHashCode", userInfoService.hashCode());
        result.put("prototypeAuditId1", userAuditLogProvider.getObject().getAuditId());
        result.put("prototypeAuditId2", userAuditLogProvider.getObject().getAuditId());
        result.put("requestId", requestContext.getRequestId());
        return result;
    }
}
