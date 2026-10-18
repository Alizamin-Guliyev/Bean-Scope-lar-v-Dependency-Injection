package az.iktex.coreplatform.simasignapi.service;

import az.iktex.coreplatform.simasignapi.component.RequestContext;
import az.iktex.coreplatform.simasignapi.component.UserAuditLog;
import az.iktex.coreplatform.simasignapi.component.UserStatistics;
import az.iktex.coreplatform.simasignapi.dto.response.UserInfoResponse;
import az.iktex.coreplatform.simasignapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserInfoServiceTest {

    private UserRepository userRepository;
    private UserInfoService userInfoService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        userRepository = mock(UserRepository.class);
        ObjectProvider<UserAuditLog> auditLogProvider = mock(ObjectProvider.class);
        RequestContext requestContext = mock(RequestContext.class);
        when(requestContext.getRequestId()).thenReturn("test1234");

        userInfoService = new UserInfoService(userRepository, auditLogProvider, requestContext, List.of(userRepository), mock(UserStatistics.class));
    }

    @Test
    void getUserById_returnsUserFromRepository() {
        UserInfoResponse user = UserInfoResponse.builder().id(1L).fullName("Test User").build();
        when(userRepository.getUserById(1L)).thenReturn(user);

        UserInfoResponse result = userInfoService.getUserById(1L);

        assertEquals("Test User", result.getFullName());
        verify(userRepository).getUserById(1L);
    }

    @Test
    void getAllUsers_returnsAllUsersFromRepository() {
        when(userRepository.getAllUsers()).thenReturn(List.of(
                UserInfoResponse.builder().id(1L).build(),
                UserInfoResponse.builder().id(2L).build()));

        assertEquals(2, userInfoService.getAllUsers().size());
    }
}
