package mucsi96.traininglog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserRolesSecurityContextFactory.class)
public @interface WithMockUserRoles {
    String[] value() default { "user" };
}
