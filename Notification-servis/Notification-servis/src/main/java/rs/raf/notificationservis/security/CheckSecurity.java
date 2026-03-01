package rs.raf.notificationservis.security;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckSecurity {
    String[] roles() default {};
}