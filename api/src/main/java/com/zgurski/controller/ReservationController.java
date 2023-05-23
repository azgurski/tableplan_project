package com.zgurski.controller;


import com.zgurski.controller.requests.ReservationCreateRequest;
import com.zgurski.controller.requests.ReservationUpdateRequest;
import com.zgurski.controller.requests.RestaurantCreateRequest;
import com.zgurski.controller.requests.RestaurantUpdateRequest;
import com.zgurski.domain.hibernate.Reservation;
import com.zgurski.domain.hibernate.Restaurant;
import com.zgurski.exception.FailedTransactionException;
import com.zgurski.exception.IllegalRequestException;
import com.zgurski.repository.ReservationRepository;
import com.zgurski.service.ReservationService;
import com.zgurski.service.RestaurantService;
import com.zgurski.util.CustomErrorMessageGenerator;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.repository.RestaurantRepository;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
//@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationRepository reservationRepository;

    private final ReservationService reservationService;

    private final ConversionService conversionService;

    private final RestaurantService restaurantService;

    @Value("${spring.data.rest.default-page-size}")
    private Integer size;

    @GetMapping("/reservations")
    public ResponseEntity<Object> findAllReservationsForAllRestaurants() {
        return new ResponseEntity<>(Collections.singletonMap("reservations",
                reservationService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<Object> findAllReservationsPageable(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable("page") int page) {

        return new ResponseEntity<>(Collections.singletonMap("reservations",
                reservationService.findAllPageable(PageRequest.of(page, size))), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/reservations/{reservationId}")
    public ResponseEntity<Object> findReservationById(@PathVariable Long restaurantId,
                                                      @PathVariable Long reservationId) {

        return new ResponseEntity<>(Collections.singletonMap("reservation",
                reservationService.findByReservationIdAndRestaurantId(reservationId, restaurantId)), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/reservations")
    public ResponseEntity<Object> findAllReservationsByRestaurantId(@PathVariable Long restaurantId) {

        return new ResponseEntity<>(Collections.singletonMap("reservations",
                reservationService
                        .findReservationsByRestaurantId(restaurantId)), HttpStatus.OK);
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PostMapping("/restaurants/{restaurantId}/reservations")
    public ResponseEntity<Object> saveReservation(
            @Valid @RequestBody ReservationCreateRequest request, @PathVariable Long restaurantId) {

        Restaurant restaurant = restaurantService.findById(restaurantId).get();

        Reservation reservation = conversionService.convert(request, Reservation.class);
        reservation.setRestaurant(restaurant);

        return new ResponseEntity<>(Collections.singletonMap("reservation",
                reservationService.save(reservation)), HttpStatus.CREATED);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PutMapping("/restaurants/{restaurantId}/reservations")
    public ResponseEntity<Object> updateRestaurant(@Valid @RequestBody ReservationUpdateRequest request,
                                                   @PathVariable Long restaurantId) {

        reservationService.checkIfReservationExistsById(request.getReservationId());
        Restaurant restaurant = restaurantService.findById(restaurantId).get();

        Reservation reservation = conversionService.convert(request, Reservation.class);
        reservation.setRestaurant(restaurant);

        return new ResponseEntity<>(Collections.singletonMap("reservation",
                reservationService.update(reservation)), HttpStatus.CREATED);
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @DeleteMapping("/restaurants/{restaurantId}/reservations/{reservationId}")
    public ResponseEntity<Object> deleteSoftReservation(
            @PathVariable Long restaurantId, @PathVariable Long reservationId) {

        return new ResponseEntity<>(Collections.singletonMap("reservation",
                reservationService.deleteSoft(restaurantId, reservationId)), HttpStatus.OK);
    }

    //TODO search Criteria query by by ReservationCode, byDate, ByGuestName, ByEmail
    //TODO function summary party size for each day
    //TODO PUT status / guest Detais

}