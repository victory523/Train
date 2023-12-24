package mucsi96.traininglog;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import io.github.mucsi96.kubetools.security.TestSecurityConfigurer;

public class WithMockUserRolesSecurityContextFactory implements WithSecurityContextFactory<WithMockUserRoles> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserRoles mockUser) {
        return TestSecurityConfigurer.createSecurityContext(mockUser.value());
    }

}
