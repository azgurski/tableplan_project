package com.zgurski.controller;

import com.zgurski.controller.hateoas.ReservationModelAssembler;
import com.zgurski.controller.requests.ReservationCreateRequest;
import com.zgurski.controller.requests.ReservationSearchCriteria;
import com.zgurski.controller.requests.ReservationUpdateRequest;
import com.zgurski.domain.enums.ReservationStatuses;
import com.zgurski.domain.hibernate.Reservation;
import com.zgurski.exception.FailedTransactionException;
import com.zgurski.exception.InvalidInputValueException;
import com.zgurski.service.ReservationService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    private final ConversionService conversionService;

    private final ReservationModelAssembler reservationModelAssembler;

    @Value("${spring.data.rest.default-page-size}")
    private Integer size;

    @Value("${response.entity.name.reservations}")
    private String reservationsString;

    @GetMapping("/reservations")
    public ResponseEntity<Object> findAllReservationsForAllRestaurants() {
        return new ResponseEntity<>(Collections.singletonMap(reservationsString,
                reservationService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/reservations/page/{page}")
    public ResponseEntity<Object> findAllReservationsPageable(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable("page") int page) {

        return new ResponseEntity<>(Collections.singletonMap(reservationsString,
                reservationService.findAllPageable(PageRequest.of(page, size))), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/reservations/{reservationId}")
    public ResponseEntity<EntityModel<Reservation>> findReservationById(@PathVariable Long restaurantId,
                                                                  @PathVariable Long reservationId) {

      Reservation reservation = reservationService.findByReservationIdAndRestaurantId(reservationId, restaurantId).get();
      EntityModel<Reservation> reservationEntityModel = reservationModelAssembler.toModel(reservation);

        return ResponseEntity.ok(reservationEntityModel);
    }

    @GetMapping("/restaurants/{restaurantId}/reservations")
    public ResponseEntity<List<EntityModel<Reservation>>> findAllReservationsByRestaurantId(@PathVariable Long restaurantId) {

        List<EntityModel<Reservation>> reservations = reservationService
                .findReservationsByRestaurantId(restaurantId).stream()
                .map(reservationModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reservations);
    }

    //TODO check exceptions
    @GetMapping("/restaurants/{restaurantId}/reservations/search")
    public ResponseEntity<List<EntityModel<Reservation>>> findAllReservationsByStatus(
            @PathVariable Long restaurantId,
            @RequestParam("status") ReservationStatuses reservationStatus) {

        List<EntityModel<Reservation>> reservations = reservationService
                .findByStatus(restaurantId, reservationStatus).stream()
                .map(reservationModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/restaurants/{restaurantId}/reservations/{year}/{month}/{day}")
    public ResponseEntity<Object> findAllReservationsByDate(
            @PathVariable Long restaurantId,
            @PathVariable int year, @PathVariable int month, @PathVariable int day) {

        return new ResponseEntity<>(Collections.singletonMap(reservationsString,
                reservationService.findAllByDateAndRestaurantId(
                        restaurantId, year, month, day)), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/reservations/{year}/{month}/{day}/search")
    public ResponseEntity<List<EntityModel<Reservation>>> findAllReservationsByDateAndStatus(
            @PathVariable Long restaurantId, @RequestParam ReservationStatuses status,
            @PathVariable int year, @PathVariable int month, @PathVariable int day) {

        List<EntityModel<Reservation>> reservations = reservationService
                .findAllByDateStatusAndRestaurantId(restaurantId, status, year, month, day).stream()
                .map(reservationModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/restaurants/{restaurantId}/availability/{year}/{month}/{day}/occupancy")
    public ResponseEntity<Object> findOccupancyByDate(@PathVariable Long restaurantId,
                                                      @PathVariable int year, @PathVariable int month, @PathVariable int day) {

        return new ResponseEntity<>(Collections.singletonMap(
                "Occupancy by hours for calendarDay={" + LocalDate.of(year, month, day) + "}",
                reservationService.getOccupancyByHour(restaurantId, year, month, day)), HttpStatus.OK);
    }

    //@GetMapping("/restaurants/{restaurantId}/availability/{year}/{month}/{day}/occupancy")
    //    public ResponseEntity<Object> findOccupancyByDate(@PathVariable Long restaurantId,
    //                                                      @PathVariable int year, @PathVariable int month, @PathVariable int day) {
    //
    //        return new ResponseEntity<>(Collections.singletonMap(
    //                "Occupancy by hours for calendarDay={" + LocalDate.of(year, month, day) + "}",
    //                reservationService.getOccupancyByHour(restaurantId, year, month, day)), HttpStatus.OK);
    //    }

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
}