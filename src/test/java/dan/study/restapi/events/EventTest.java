package dan.study.restapi.events;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder().build();
        assertThat(event).isNotNull();
    }


    @Test
    public void testFree(){

        //Given
        Event event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        //When
        event.update();

        //Then
        assertThat(event.isFree()).isTrue();


        //Given
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        //When
        event.update();

        //Then
        assertThat(event.isFree()).isTrue();
    }

    @Test
    public void testOffline(){
        //Given
        Event event = Event.builder()
                .location("강남역 네이버 D2 스타텁 팩토리")
                .build();

        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isTrue();

        //Given
        event = Event.builder()
                .build();

        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isFalse();

    }

}