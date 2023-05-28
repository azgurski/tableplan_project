package com.zgurski.controller.hateoas;

import com.zgurski.controller.DefaultWeekDayController;
import com.zgurski.domain.entities.DefaultWeekDay;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DefaultWeekDayModelAssembler implements RepresentationModelAssembler<DefaultWeekDay, EntityModel<DefaultWeekDay>> {

    @Override
    public EntityModel<DefaultWeekDay> toModel(DefaultWeekDay weekDay) {

        DayOfWeek dayOfWeek = weekDay.getDayOfWeek();
        DayOfWeek nextDayOfWeek = dayOfWeek.plus(1);
        DayOfWeek previousDayOfWeek = dayOfWeek.minus(1);

        Long restaurantId = weekDay.getRestaurant().getRestaurantId();
        Long weekDayId = weekDay.getDefaultWeekDayId();

        Link linkSelfThisDay = linkTo(methodOn(DefaultWeekDayController.class)
                .findOneByIdAndRestaurantId(weekDayId, restaurantId)).withSelfRel();

        Link linkNextDay = linkTo(methodOn(DefaultWeekDayController.class)
                .findOneByDayOfWeekAndRestaurantId(nextDayOfWeek.toString(), restaurantId))
                .withRel("schedule-for-next-week-day");

        Link linkPreviousDay = linkTo(methodOn(DefaultWeekDayController.class)
                .findOneByDayOfWeekAndRestaurantId(previousDayOfWeek.toString(), restaurantId))
                .withRel("schedule-for-previous-week-day");

        Link linkAllSchedule = linkTo(methodOn(DefaultWeekDayController.class)
                .findAllByRestaurantId(restaurantId))
                .withRel("schedule-for-all-days");

        return EntityModel.of(weekDay, linkSelfThisDay, linkNextDay, linkPreviousDay, linkAllSchedule);
    }
}
