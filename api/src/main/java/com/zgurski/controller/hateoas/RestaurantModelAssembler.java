package com.zgurski.controller.hateoas;

import com.zgurski.controller.CalendarDayController;
import com.zgurski.controller.DefaultWeekDayController;
import com.zgurski.controller.ReservationController;
import com.zgurski.controller.RestaurantController;
import com.zgurski.domain.hibernate.Restaurant;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RestaurantModelAssembler implements RepresentationModelAssembler<Restaurant, EntityModel<Restaurant>> {

    LocalDate calendarDate = LocalDate.now();

    LocalDate nextCalendarDate = calendarDate.plusDays(1);

    @Override
    public EntityModel<Restaurant> toModel(Restaurant restaurant) {

        Link linkSelf = linkTo(methodOn(RestaurantController.class)
                .findRestaurantById(restaurant.getRestaurantId())).withSelfRel();

        Link linkReservations = linkTo(methodOn(ReservationController.class)
                .findAllReservationsByRestaurantId(restaurant.getRestaurantId())).withRel("reservations");

        //todo add all reservations today

        Link linkAvailabilityToday = linkTo(methodOn(CalendarDayController.class)
                .findByDateAndRestaurantId(restaurant.getRestaurantId(),
                        calendarDate.getYear(), calendarDate.getMonthValue(), calendarDate.getDayOfMonth()))
                .withRel("availability-for-today");

        Link linkAvailabilityTomorrow = linkTo(methodOn(CalendarDayController.class)
                .findByDateAndRestaurantId(restaurant.getRestaurantId(),
                        nextCalendarDate.getYear(), nextCalendarDate.getMonthValue(), nextCalendarDate.getDayOfMonth()))
                .withRel("availability-for-tomorrow");

        Link linkAvailabilityNextSixtyDays = linkTo(methodOn(CalendarDayController.class)
                .findAllForNextSixtyDays(restaurant.getRestaurantId())).withRel("availability-next-60-days");

        Link linkSchedule = linkTo(methodOn(DefaultWeekDayController.class)
                .findSchedulesByRestaurantId(restaurant.getRestaurantId())).withRel("default-schedule");

        return EntityModel.of(restaurant, linkSelf, linkReservations, linkAvailabilityToday, linkAvailabilityTomorrow,
                linkAvailabilityNextSixtyDays, linkSchedule);
    }
}