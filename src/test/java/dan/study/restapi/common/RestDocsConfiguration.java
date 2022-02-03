package dan.study.restapi.common;

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

/**
 * Spring Rest Docs 사용시 Custom을 위한 bean
 * Request / Response 데이터를 줄바꾸기와 같이 예쁘게 볼 수 있도록 설정
 */
//Test돌릴때만 설정하기 위한 애노테이션
@TestConfiguration
public class RestDocsConfiguration {

    //람다 변환 후
    @Bean
    public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcConfigurationCustomizer(){
        return configurer -> configurer.operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint());

        //람다 변환 전
//        return new RestDocsMockMvcConfigurationCustomizer() {
//            @Override
//            public void customize(MockMvcRestDocumentationConfigurer configurer) {
//                configurer.operationPreprocessors()
//                        .withRequestDefaults(prettyPrint())
//                        .withResponseDefaults(prettyPrint());
//
//            }
//        };
    }
}
