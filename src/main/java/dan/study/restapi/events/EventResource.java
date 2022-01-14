package dan.study.restapi.events;

import org.springframework.hateoas.RepresentationModel;
public class EventResource extends RepresentationModel{

    private Event event;

    public EventResource(Event event){
        this.event = event;}

    public Event getEvent(){
        return event;

    }


}
