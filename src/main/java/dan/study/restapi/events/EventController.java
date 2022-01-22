package dan.study.restapi.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Controller
@RequestMapping(value ="/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;


    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){

        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);

        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        /**
         * Repository에 저장하기 위해선 EventDto를 event 값으로 바꿔주어야하는데
         * 아래 코드처럼 하려면 너무 길기 떄문에 @ModelMapper를 사용한다.
         *  Event event = Event.builder()
         *                 .name(eventDto.getName())
         *                 .description(eventDto.getDescription())
         *                 ...
         *                 .build();
         * Repository에 받는 값을 EventDto로 바꾸면 안되나 라는 생각
         * -> EventDto는 Event Entity값이 너무 많아 Annotiation을 분리하기 위해 만든거기떄문에 EventDto로 바꾸면
         * 애초에 EventDto를 만들 필요가 없다.
         */
        Event event = modelMapper.map(eventDto,Event.class);
        
        //원래는 Service에 들어가야 할 로직
        event.update();
        Event newEvent = this.eventRepository.save(event);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createUri = selfLinkBuilder.toUri();

        //eventResource는 HATOS에서 link를 생성해주기 위해 생성
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));

        return ResponseEntity.created(createUri).body(eventResource);
    }
}
