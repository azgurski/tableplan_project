package com.zgurski.controller;

import com.zgurski.controller.hateoas.ReservationModelAssembler;
import com.zgurski.controller.openapi.reservation.ReservationDeleteSoftOpenApi;
import com.zgurski.controller.openapi.reservation.ReservationFindAllByDateAndRestaurantIdOpenApi;
import com.zgurski.controller.openapi.reservation.ReservationFindAllByDateAndStatusOpenApi;
import com.zgurski.controller.openapi.reservation.ReservationFindAllByRestaurantId;
import com.zgurski.controller.openapi.reservation.ReservationFindOneByIdAndRestaurantIdOpenApi;
import com.zgurski.controller.openapi.reservation.ReservationFindOccupancyByDateOpenApi;
import com.zgurski.controller.openapi.reservation.ReservationSaveOpenApi;
import com.zgurski.controller.openapi.reservation.ReservationUpdateProfileOpenApi;
import com.zgurski.controller.openapi.reservation.ReservationUpdateStatusOpenApi;
import com.zgurski.controller.requests.ReservationCreateRequest;
import com.zgurski.controller.requests.ReservationUpdateRequest;
import com.zgurski.domain.enums.ReservationStatuses;
import com.zgurski.domain.entities.Reservation;
import com.zgurski.exception.FailedTransactionException;
import com.zgurski.repository.ReservationRepository;
import com.zgurski.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.util.stream.Collectors;

@RestController
@Tag(name = "Reservation", description = "Managing reservations.")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    private final ReservationRepository reservationRepository;

    private final ReservationModelAssembler reservationModelAssembler;

    private final ConversionService conversionService;

    @Value("${spring.data.rest.default-page-size}")
    private Integer size;

    @Value("${response.entity.name.reservations}")
    private String reservationsString;

    @ReservationFindAllByDateAndRestaurantIdOpenApi
    @GetMapping("/restaurants/{restaurantId}/reservations/{year}/{month}/{day}")
    public ResponseEntity<Object> findAllByDateAndRestaurantId(
            @PathVariable Long restaurantId,
            @PathVariable int year, @PathVariable int month, @PathVariable int day) {

        return new ResponseEntity<>(Collections.singletonMap(reservationsString,
                reservationService.findAllByDateAndRestaurantId(
                        restaurantId, year, month, day)), HttpStatus.OK);
    }

    @ReservationFindAllByRestaurantId
    @GetMapping("/restaurants/{restaurantId}/reservations")
    public ResponseEntity<List<EntityModel<Reservation>>> findAllByRestaurantId(@PathVariable Long restaurantId) {

        List<EntityModel<Reservation>> reservations = reservationService
                .findReservationsByRestaurantId(restaurantId).stream()
                .map(reservationModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reservations);
    }

    @Operation(hidden = true)
    @GetMapping("/restaurants/{restaurantId}/reservations/all-days")
    public ResponseEntity<List<EntityModel<Reservation>>> findAllByStatus(
            @PathVariable Long restaurantId, @RequestParam("status") ReservationStatuses reservationStatus) {

        List<EntityModel<Reservation>> reservations = reservationService
                .findByStatus(restaurantId, reservationStatus).stream()
                .map(reservationModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reservations);
    }

    @ReservationFindAllByDateAndStatusOpenApi
    @GetMapping("/restaurants/{restaurantId}/reservations/{year}/{month}/{day}/search")
    public ResponseEntity<List<EntityModel<Reservation>>> findAllByDateAndStatus(
            @PathVariable Long restaurantId, @RequestParam ReservationStatuses status,
            @PathVariable int year, @PathVariable int month, @PathVariable int day) {

        List<EntityModel<Reservation>> reservations = reservationService
                .findAllByDateStatusAndRestaurantId(restaurantId, status, year, month, day).stream()
                .map(reservationModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reservations);
    }

    @ReservationFindOccupancyByDateOpenApi
    @GetMapping("/restaurants/{restaurantId}/reservations/{year}/{month}/{day}/occupancy")
    public ResponseEntity<Object> findOccupancyByDate(
            @PathVariable Long restaurantId, @PathVariable int year, @PathVariable int month, @PathVariable int day) {

        return new ResponseEntity<>(Collections.singletonMap(
                "Occupancy by hours for calendarDay={" + LocalDate.of(year, month, day) + "}",
                reservationService.getOccupancyByHour(restaurantId, year, month, day)), HttpStatus.OK);
    }

    @ReservationFindOneByIdAndRestaurantIdOpenApi
    @GetMapping("/restaurants/{restaurantId}/reservations/{reservationId}")
    public ResponseEntity<EntityModel<Reservation>> findOneById(@PathVariable Long restaurantId,
                                                                @PathVariable Long reservationId) {

        Reservation reservation = reservationService
                .findByReservationIdAndRestaurantId(reservationId, restaurantId).get();
        EntityModel<Reservation> reservationEntityModel = reservationModelAssembler.toModel(reservation);

        return ResponseEntity.ok(reservationEntityModel);
    }

    /* CRUD Methods */

    @Operation(hidden = true)
    @GetMapping("/reservations")
    public ResponseEntity<Object> findAll() {

        return new ResponseEntity<>(Collections.singletonMap(reservationsString,
                reservationService.findAll()), HttpStatus.OK);
    }

    @Operation(hidden = true)
    @GetMapping("/reservations/page/{page}")
    public ResponseEntity<Object> findAllPageable(
            @Parameter(name = "page", example = "1", required = true) @PathVariable("page") int page) {

        return new ResponseEntity<>(Collections.singletonMap(reservationsString,
                reservationService.findAllPageable(PageRequest.of(page, size))), HttpStatus.OK);
    }

    @ReservationSaveOpenApi
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PostMapping("/restaurants/{restaurantId}/reservations")
    public ResponseEntity<Object> save(
            @Valid @RequestBody ReservationCreateRequest request, @PathVariable Long restaurantId) {

        Reservation reservation = conversionService.convert(request, Reservation.class);
        Reservation savedReservation = reservationService.save(restaurantId, reservation);

        return new ResponseEntity<>(Collections.singletonMap("reservation", savedReservation), HttpStatus.CREATED);
    }

    @ReservationUpdateProfileOpenApi
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PutMapping("/restaurants/{restaurantId}/reservations")
    public ResponseEntity<Object> updateReservationProfile(@Valid @RequestBody ReservationUpdateRequest request,
                                                           @PathVariable Long restaurantId) {

        Integer initialPartySize = reservationRepository.getPartySize(request.getReservationId());
        Reservation reservationToUpdate = conversionService.convert(request, Reservation.class);

        Reservation updatedReservation = reservationService.update(restaurantId, reservationToUpdate, initialPartySize);

        return new ResponseEntity<>(Collections.singletonMap("reservation", updatedReservation), HttpStatus.CREATED);
    }

    @ReservationUpdateStatusOpenApi
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PutMapping("/restaurants/{restaurantId}/reservations/{reservationId}/change")
    public ResponseEntity<Object> updateReservationStatus(
            @PathVariable Long restaurantId, @PathVariable Long reservationId,
            @RequestParam ReservationStatuses status) {

        Reservation updatedReservation = reservationService.updateStatus(restaurantId, reservationId, status);

        return new ResponseEntity<>(Collections.singletonMap("reservation", updatedReservation), HttpStatus.CREATED);
    }

    @ReservationDeleteSoftOpenApi
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @DeleteMapping("/restaurants/{restaurantId}/reservations/{reservationId}")
    public ResponseEntity<Object> deleteSoft(
            @PathVariable Long restaurantId, @PathVariable Long reservationId) {

        return new ResponseEntity<>(Collections.singletonMap("successMessage",
                "Reservation with id={" + reservationService.deleteSoft(restaurantId, reservationId) +
                        "} is deleted."), HttpStatus.OK);
    }
}