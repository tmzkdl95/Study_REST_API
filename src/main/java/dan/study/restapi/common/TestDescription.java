package dan.study.restapi.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface TestDescription {

    /**
     * junit5 부터는 TestDescription를 따로 생성하지 않아도 자동으로 지원해준다.
     * Annotation으로 쓰던, 주석으로 쓰던 큰 차이는 없다. 취향차이
     *
     */
    String value();
}
