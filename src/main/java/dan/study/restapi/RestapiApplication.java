package dan.study.restapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RestapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestapiApplication.class, args);
	}

	/**
	 * ModelMapper는 공용으로 사용할 것이기 때문에 Bean으로 등록한다.
	 */
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

}
