package mucsi96.trainingLog;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithGoogleSecurityContextFactory.class)
public @interface WithMockGoogleUser {

  String username() default "rob";

  String name() default "Rob Winch";
}
