package commandprompt.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Commands.class)
@Target({ElementType.METHOD})
public @interface Command {
    String name() default "";
    String description() default "";
    String usage() default "";

}
