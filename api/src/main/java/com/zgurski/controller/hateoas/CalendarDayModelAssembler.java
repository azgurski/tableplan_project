package com.zgurski.controller.hateoas;

import com.zgurski.controller.CalendarDayController;
import com.zgurski.controller.TimeslotController;
import com.zgurski.domain.hibernate.CalendarDay;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CalendarDayModelAssembler implements RepresentationModelAssembler<CalendarDay, EntityModel<CalendarDay>> {

    @Override
    public EntityModel<CalendarDay> toModel(CalendarDay calendarDay) {

        Long restaurantId = calendarDay.getRestaurant().getRestaurantId();

        LocalDate calendarDate = calendarDay.getLocalDate();
        LocalDate nextCalendarDate = calendarDate.plusDays(1);
        LocalDate nextCalendarMonth = calendarDate.plusMonths(1);

        Link linkSelfThisDay = linkTo(methodOn(CalendarDayController.class)
                .findByDateAndRestaurantId(restaurantId, calendarDate.getYear(),
                        calendarDate.getMonthValue(), calendarDate.getDayOfMonth()))
                .withSelfRel();

        Link linkTimeslots = linkTo(methodOn(TimeslotController.class)
                .findAllByIsAvailableByDate(restaurantId, calendarDate.getYear(),
                        calendarDate.getMonthValue(), calendarDate.getDayOfMonth(), true))
                .withRel("open-times-for-current-day");

        Link linkThisMonth = linkTo(methodOn(CalendarDayController.class)
                .findAllByMonth(restaurantId, calendarDate.getYear(), calendarDate.getMonthValue()))
                .withRel("availability-for-current-month");

        Link linkNextDay = linkTo(methodOn(CalendarDayController.class)
                .findByDateAndRestaurantId(restaurantId, nextCalendarDate.getYear(), nextCalendarDate.getMonthValue(),
                        nextCalendarDate.getDayOfMonth()))
                .withRel("availability-for-following-day");

        Link linkNextMonth = linkTo(methodOn(CalendarDayController.class)
                .findAllByMonth(restaurantId, nextCalendarMonth.getYear(),
                        nextCalendarMonth.getMonthValue()))
                .withRel("availability-for-following-month");


        return EntityModel.of(calendarDay, linkSelfThisDay, linkTimeslots, linkThisMonth, linkNextDay, linkNextMonth);
    }
}
