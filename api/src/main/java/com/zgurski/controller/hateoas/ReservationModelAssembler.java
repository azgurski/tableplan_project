package com.zgurski.controller.hateoas;

import com.zgurski.controller.ReservationController;
import com.zgurski.domain.enums.ReservationStatuses;
import com.zgurski.domain.entities.Reservation;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ReservationModelAssembler implements RepresentationModelAssembler<Reservation, EntityModel<Reservation>> {

    @Override
    public EntityModel<Reservation> toModel(Reservation reservation) {

        Long restaurantId = reservation.getRestaurant().getRestaurantId();

        LocalDate date = reservation.getLocalDate();
        LocalDate nextDate = date.plusDays(1);
        LocalDate previousDate = date.minusDays(1);

        Link linkSelf = linkTo(methodOn(ReservationController.class)
                .findOneById(restaurantId, restaurantId))
                .withSelfRel();

        Link linkReservationsCurrentDay = linkTo(methodOn(ReservationController.class)
                .findAllByDateAndStatus(restaurantId, ReservationStatuses.CONFIRMED,
                        date.getYear(), date.getMonthValue(), date.getDayOfMonth()))
                .withRel("confirmed-reservations-for-current-day");

        Link linkReservationsNextDay = linkTo(methodOn(ReservationController.class)
                .findAllByDateAndStatus(restaurantId, ReservationStatuses.CONFIRMED,
                        nextDate.getYear(), nextDate.getMonthValue(),
                        nextDate.getDayOfMonth()))
                .withRel("confirmed-reservations-for-next-day");

        Link linkReservationsPreviousDay = linkTo(methodOn(ReservationController.class)
                .findAllByDateAndStatus(restaurantId, ReservationStatuses.CONFIRMED,
                        previousDate.getYear(), previousDate.getMonthValue(),
                        previousDate.getDayOfMonth()))
                .withRel("confirmed-reservations-for-previous-day");

        Link linkReservationsNotConfirmed = linkTo(methodOn(ReservationController.class)
                .findAllByStatus(restaurantId, ReservationStatuses.UNREAD))
                .withRel("all-unread-reservations");

        Link linkOccupancyPerHour = linkTo(methodOn(ReservationController.class)
                .findOccupancyByDate(restaurantId, date.getYear(), date.getMonthValue(), date.getDayOfMonth()))
                .withRel("occupancy-for-current-day");

        return EntityModel.of(reservation, linkSelf,
                linkReservationsCurrentDay, linkReservationsNextDay, linkReservationsPreviousDay,
                linkReservationsNotConfirmed, linkOccupancyPerHour);
    }
}