package com.zgurski.controller.hateoas;

import com.zgurski.controller.ReservationController;
import com.zgurski.controller.TimeslotController;
import com.zgurski.controller.requests.ReservationCreateRequest;
import com.zgurski.domain.enums.ReservationStatuses;
import com.zgurski.domain.hibernate.CalendarDay;
import com.zgurski.domain.hibernate.Reservation;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.QueryParameter;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ReservationModelAssembler implements RepresentationModelAssembler<Reservation, EntityModel<Reservation>> {

    @Override
    public EntityModel<Reservation> toModel(Reservation reservation) {

        Long restaurantId = reservation.getRestaurant().getRestaurantId();

        //TODO заменить в остальных
        LocalDate todayDate = reservation.getLocalDate();

        LocalDate date = reservation.getLocalDate();
        LocalDate nextDate = date.plusDays(1);
        LocalDate previousDate = date.minusDays(1);

        Link linkSelf = linkTo(methodOn(ReservationController.class)
                .findReservationById(restaurantId, restaurantId))
                .withSelfRel();

        //todo change to search by pnr

        Link linkReservationsCurrentDay = linkTo(methodOn(ReservationController.class)
                .findAllReservationsByDateAndStatus(restaurantId, ReservationStatuses.CONFIRMED,
                        date.getYear(), date.getMonthValue(), date.getDayOfMonth()))
                .withRel("confirmed-reservations-for-current-day");

        Link linkReservationsNextDay = linkTo(methodOn(ReservationController.class)
                .findAllReservationsByDateAndStatus(restaurantId, ReservationStatuses.CONFIRMED,
                        nextDate.getYear(), nextDate.getMonthValue(),
                        nextDate.getDayOfMonth()))
                .withRel("confirmed-reservations-for-next-day");

        Link linkReservationsPreviousDay = linkTo(methodOn(ReservationController.class)
                .findAllReservationsByDateAndStatus(restaurantId, ReservationStatuses.CONFIRMED,
                        previousDate.getYear(), previousDate.getMonthValue(),
                        previousDate.getDayOfMonth()))
                .withRel("confirmed-reservations-for-previous-day");

        Link linkReservationsNotConfirmed = linkTo(methodOn(ReservationController.class)
                .findAllReservationsByStatus(restaurantId, ReservationStatuses.UNREAD))
                .withRel("all-unread-reservations");

        Link linkOccupancyPerHour = linkTo(methodOn(ReservationController.class)
                .findOccupancyByDate(restaurantId, date.getYear(), date.getMonthValue(), date.getDayOfMonth()))
                .withRel("occupancy-for-current-day");

        return EntityModel.of(reservation, linkSelf,
                linkReservationsCurrentDay, linkReservationsNextDay, linkReservationsPreviousDay,
                linkReservationsNotConfirmed, linkOccupancyPerHour);
    }
}
