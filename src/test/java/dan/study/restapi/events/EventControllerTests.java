package dan.study.restapi.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import dan.study.restapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.regex.Matcher;

import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    /**
     * MockMvc-요청/응답을 테스트할 수 있는 클래스(SpringMVC 내에서 가장 핵심적인 테스트 중 하나)
     * 특징 : WebServer를 띄우지 않기떄문에 빠르긴 하지만 DespatherServlet까지 만들어야 하기 때문에
     *       단위테스트보다는 느리다. 웹 서버까지 띄우는 테스트보다는 덜 걸리지만, 단위테스트보다는 좀 더 걸림
     */
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private MediaType contentType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));


    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        //request를 위해 필요 값 셋팅
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,12,25,14,40))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,12,25,14,40))
                .beginEventDateTime(LocalDateTime.of(2021,12,25,14,40))
                .endEventDateTime(LocalDateTime.of(2021,12,25,14,40))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        /**
         * @WebMvcTest는 슬라이스 테스트이기 때문에 Web용 빈들만 등록을 하기 떄문에
         * Repository 객체를 Mocking을 하지 않으면 Nullpointer Error 발생
         * 추가로, Repository에 저장하려는 테스트를 하려면 더이상 WebMvcTest 슬라이스 테스트여서는 안된다.
         * SpringBootTest를 하면서 Mocking 테스트를 사용하려면 AutoConfigureMockMvc를 추가한다.
         *
         */
        //event.setId(10);
        //Mockito.when(eventRepository.save(event)).thenReturn(event);

        //http스펙으로 웹서버를 실행하지 않고 테스트 할 수 있는 메서드
               mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
//                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("offline").value(true))
//                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_link.self").exists())
                .andExpect(jsonPath("_link.query-events").exists())
                .andExpect(jsonPath("_link.update-event").exists())

            ;
    }


    @Test
    @TestDescription("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @TestDescription("입력값이 잘못된 경우에 이벤트를 생성하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,12,26,14,40))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,12,25,14,40))
                .beginEventDateTime(LocalDateTime.of(2021,12,24,14,40))
                .endEventDateTime(LocalDateTime.of(2021,12,23,14,40))
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        this.mockMvc.perform(post("/api/events"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }






}
