package com.zgurski.controller;

import com.zgurski.controller.requests.ReservationCreateRequest;
import com.zgurski.controller.requests.ReservationSearchCriteria;
import com.zgurski.controller.requests.ReservationUpdateRequest;
import com.zgurski.domain.hibernate.Reservation;
import com.zgurski.exception.FailedTransactionException;
import com.zgurski.exception.InvalidInputValueException;
import com.zgurski.service.ReservationService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    private final ConversionService conversionService;

    @Value("${spring.data.rest.default-page-size}")
    private Integer size;

    @GetMapping("/reservations")
    public ResponseEntity<Object> findAllReservationsForAllRestaurants() {
        return new ResponseEntity<>(Collections.singletonMap("reservations",
                reservationService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/reservations/page/{page}")
    public ResponseEntity<Object> findAllReservationsPageable(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable("page") int page) {

        return new ResponseEntity<>(Collections.singletonMap("reservations",
                reservationService.findAllPageable(PageRequest.of(page, size))), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/reservations/{reservationId}")
    public ResponseEntity<Object> findReservationById(@PathVariable Long restaurantId,
                                                      @PathVariable Long reservationId) {

        return new ResponseEntity<>(Collections.singletonMap("reservations",
                reservationService.findByReservationIdAndRestaurantId(reservationId, restaurantId)), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/reservations")
    public ResponseEntity<Object> findAllReservationsByRestaurantId(@PathVariable Long restaurantId) {

        return new ResponseEntity<>(Collections.singletonMap("reservations",
                reservationService
                        .findReservationsByRestaurantId(restaurantId)), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/reservations/search")
    public ResponseEntity<Object> findAllReservationsByStatus(@PathVariable Long restaurantId,
            @Valid @ModelAttribute ReservationSearchCriteria criteria, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new InvalidInputValueException();
        }

        return new ResponseEntity<>(Collections.singletonMap("reservations",
                reservationService
                        .findByStatus(restaurantId, criteria.getReservationStatus())), HttpStatus.OK);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PostMapping("/restaurants/{restaurantId}/reservations")
    public ResponseEntity<Object> saveReservation(
            @Valid @RequestBody ReservationCreateRequest request, @PathVariable Long restaurantId) {

        Reservation reservation = conversionService.convert(request, Reservation.class);
        Reservation savedReservation = reservationService.save(restaurantId, reservation);

        return new ResponseEntity<>(Collections.singletonMap("reservation", savedReservation), HttpStatus.CREATED);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PutMapping("/restaurants/{restaurantId}/reservations")
    public ResponseEntity<Object> updateReservation(@Valid @RequestBody ReservationUpdateRequest request,
                                                   @PathVariable Long restaurantId) {

        Reservation reservation = conversionService.convert(request, Reservation.class);
        Reservation updatedReservation = reservationService.update(restaurantId, reservation);

        return new ResponseEntity<>(Collections.singletonMap("reservation", updatedReservation), HttpStatus.CREATED);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @DeleteMapping("/restaurants/{restaurantId}/reservations/{reservationId}")
    public ResponseEntity<Object> deleteSoftReservation(
            @PathVariable Long restaurantId, @PathVariable Long reservationId) {

        return new ResponseEntity<>(Collections.singletonMap("successMessage",
                "Reservation with id={" + reservationService.deleteSoft(restaurantId, reservationId) +
                        "} is deleted."), HttpStatus.OK);
    }

    //TODO search Criteria query by by ReservationCode, byDate, ByGuestName, ByEmail
    //TODO function summary party size for each day
    //TODO PUT status / guest Detais
}