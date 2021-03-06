package dan.study.restapi.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import dan.study.restapi.common.RestDocsConfiguration;
import dan.study.restapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.regex.Matcher;

import static org.junit.Assert.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class EventControllerTests {

    /**
     * MockMvc-??????/????????? ???????????? ??? ?????? ?????????(SpringMVC ????????? ?????? ???????????? ????????? ??? ??????)
     * ?????? : WebServer??? ????????? ??????????????? ????????? ????????? DespatherServlet?????? ???????????? ?????? ?????????
     *       ???????????????????????? ?????????. ??? ???????????? ????????? ?????????????????? ??? ????????????, ???????????????????????? ??? ??? ??????
     */
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private MediaType contentType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));


    @Test
    @TestDescription("??????????????? ???????????? ???????????? ?????????")
    public void createEvent() throws Exception {
        //request??? ?????? ?????? ??? ??????
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
                .location("????????? D2 ????????? ?????????")
                .build();

        /**
         * @WebMvcTest??? ???????????? ??????????????? ????????? Web??? ????????? ????????? ?????? ?????????
         * Repository ????????? Mocking??? ?????? ????????? Nullpointer Error ??????
         * ?????????, Repository??? ??????????????? ???????????? ????????? ????????? WebMvcTest ???????????? ?????????????????? ?????????.
         * SpringBootTest??? ????????? Mocking ???????????? ??????????????? AutoConfigureMockMvc??? ????????????.
         *
         */
        //event.setId(10);
        //Mockito.when(eventRepository.save(event)).thenReturn(event);

        //http???????????? ???????????? ???????????? ?????? ????????? ??? ??? ?????? ?????????
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
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                              linkWithRel("self").description("link to self"),
                              linkWithRel("query-events").description("link to query-events"),
                              linkWithRel("update-event").description("link to update an existing events")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base Price of new event"),
                                fieldWithPath("maxPrice").description("max Price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of new event")
                        ),

                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        /**
                         * response / relaxedResponseFields ????????? ???????????? ?????????
                         * response??? ???????????? ?????? ???????????? ????????? ??????????????? ????????? ?????????
                         * - ???????????? ?????? ??? ??? ???????????? ???????????? ????????? ??????????????? ????????? Fail??????
                         * relaxedResponseFields??? ???????????? ??? ?????? ????????? ????????? ?????? ????????? ????????? ??????
                         * - ??????????????? ?????????, ???????????? ?????? ?????? Pass
                         * - ???????????? ?????? ???????????? ????????? ?????? Fail
                         *
                         */
                        responseFields(
                                fieldWithPath("id").description("Identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base Price of new event"),
                                fieldWithPath("maxPrice").description("max Price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of Enrollment"),
                                fieldWithPath("free").description("It tells if this event is free or not"),
                                fieldWithPath("offline").description("It tells if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event Status"),
                                fieldWithPath("_links.self.href").description("link to self href"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update event list")

                        )
                ))

            ;
    }


    @Test
    @TestDescription("???????????? ???????????? ????????? ????????? ???????????? ?????????")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @TestDescription("???????????? ????????? ????????? ???????????? ???????????? ?????????")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,12,25,14,40))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,12,26,14,40))
                .beginEventDateTime(LocalDateTime.of(2021,12,25,14,40))
                .endEventDateTime(LocalDateTime.of(2021,12,26,14,40))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? D2 ????????? ?????????")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                //.andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
               // .andExpect(jsonPath("$[0].rejectedValue").exists())
                ;
    }






}
