package az.iktex.coreplatform.simasignapi.controller;

import az.iktex.coreplatform.simasignapi.component.UserStatistics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {

    private final UserStatistics userStatistics;

    public StatsController(UserStatistics userStatistics) {
        this.userStatistics = userStatistics;
    }

    @GetMapping
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("createdUsers", userStatistics.getCreatedUsers());
        stats.put("getUserByIdCalls", userStatistics.getGetUserByIdCalls());
        return stats;
    }
}
