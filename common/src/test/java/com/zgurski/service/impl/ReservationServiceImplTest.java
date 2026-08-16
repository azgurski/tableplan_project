package com.zgurski.service.impl;

import com.zgurski.domain.entities.Reservation;
import com.zgurski.domain.entities.Restaurant;
import com.zgurski.domain.enums.ReservationStatuses;
import com.zgurski.repository.ReservationRepository;
import com.zgurski.service.RestaurantService;
import com.zgurski.service.TimeslotService;
import com.zgurski.service.impl.ReservationServiceImpl;
import com.zgurski.util.CustomErrorMessageGenerator;
import com.zgurski.util.email.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private RestaurantService restaurantService;
    @Mock
    private TimeslotService timeslotService;
    @Mock
    private EmailService emailService;
    @Mock
    private CustomErrorMessageGenerator messageGenerator;
    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    void updateStatus_shouldIncreaseTimeslotCurrentOccupancy_whenUnreadBecomesConfirmed() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(10L);

        Reservation reservation = new Reservation();
        reservation.setRestaurant(restaurant);
        reservation.setReservationId(111L);
        reservation.setPartySize(4);
        reservation.setStatus(ReservationStatuses.UNREAD);
        reservation.setLocalDate(LocalDate.of(2026, 9, 1));
        reservation.setLocalTime(LocalTime.of(12, 30));

        when(restaurantService.findById(restaurant.getRestaurantId()))
                .thenReturn(Optional.of(restaurant));

        when(restaurantService.checkIfRestaurantExistsById(restaurant.getRestaurantId()))
                .thenReturn(true);

        when(reservationRepository.existsReservationByReservationId(reservation.getReservationId()))
                .thenReturn(true);

        when(reservationRepository
                .findReservationByReservationIdAndRestaurant_RestaurantId(
                        reservation.getReservationId(),
                        restaurant.getRestaurantId()))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.findById(reservation.getReservationId()))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.findByReservationId(reservation.getReservationId()))
                .thenReturn(Optional.of(reservation));

        Reservation result = reservationService.updateStatus(
                restaurant.getRestaurantId(),
                reservation.getReservationId(),
                ReservationStatuses.CONFIRMED
        );

        verify(timeslotService).updateTimeslotCapacity(
                reservation.getPartySize(),
                reservation.getLocalDate(),
                reservation.getLocalTime(),
                restaurant
        );

        verify(reservationRepository).saveAndFlush(reservation);

        verify(emailService).prepareConfirmedEmail(restaurant, reservation);

        assertEquals(ReservationStatuses.CONFIRMED, reservation.getStatus());
        assertSame(reservation, result);
    }

    @Test
    void updateStatus_shouldDecreaseTimeslotCurrentOccupancy_whenConfirmedBecomesCancelled() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(10L);

        Reservation reservation = new Reservation();
        reservation.setRestaurant(restaurant);
        reservation.setReservationId(111L);
        reservation.setPartySize(4);
        reservation.setStatus(ReservationStatuses.CONFIRMED);
        reservation.setLocalDate(LocalDate.of(2026, 9, 1));
        reservation.setLocalTime(LocalTime.of(12, 30));

        when(restaurantService.findById(restaurant.getRestaurantId()))
                .thenReturn(Optional.of(restaurant));

        when(restaurantService.checkIfRestaurantExistsById(restaurant.getRestaurantId()))
                .thenReturn(true);

        when(reservationRepository.existsReservationByReservationId(reservation.getReservationId()))
                .thenReturn(true);

        when(reservationRepository
                .findReservationByReservationIdAndRestaurant_RestaurantId(
                        reservation.getReservationId(),
                        restaurant.getRestaurantId()))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.findById(reservation.getReservationId()))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.findByReservationId(reservation.getReservationId()))
                .thenReturn(Optional.of(reservation));

        Reservation result = reservationService.updateStatus(
                restaurant.getRestaurantId(),
                reservation.getReservationId(),
                ReservationStatuses.CANCELLED
        );

        verify(timeslotService).updateTimeslotCapacity(
                -reservation.getPartySize(),
                reservation.getLocalDate(),
                reservation.getLocalTime(),
                restaurant
        );

        verify(reservationRepository).saveAndFlush(reservation);

        verify(emailService).prepareCancelledEmail(restaurant, reservation);

        assertEquals(ReservationStatuses.CANCELLED, reservation.getStatus());
        assertSame(reservation, result);
    }

    @Test
    void updateStatus_shouldDoNotIncreaseTimeslotCurrentOccupancy_whenUnreadBecomesNotConfirmed() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(10L);

        Reservation reservation = new Reservation();
        reservation.setRestaurant(restaurant);
        reservation.setReservationId(111L);
        reservation.setPartySize(4);
        reservation.setStatus(ReservationStatuses.UNREAD);
        reservation.setLocalDate(LocalDate.of(2026, 9, 1));
        reservation.setLocalTime(LocalTime.of(12, 30));

        when(restaurantService.findById(restaurant.getRestaurantId()))
                .thenReturn(Optional.of(restaurant));

        when(restaurantService.checkIfRestaurantExistsById(restaurant.getRestaurantId()))
                .thenReturn(true);

        when(reservationRepository.existsReservationByReservationId(reservation.getReservationId()))
                .thenReturn(true);

        when(reservationRepository
                .findReservationByReservationIdAndRestaurant_RestaurantId(
                        reservation.getReservationId(),
                        restaurant.getRestaurantId()))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.findById(reservation.getReservationId()))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.findByReservationId(reservation.getReservationId()))
                .thenReturn(Optional.of(reservation));

        Reservation result = reservationService.updateStatus(
                restaurant.getRestaurantId(),
                reservation.getReservationId(),
                ReservationStatuses.NOT_CONFIRMED
        );

        verify(timeslotService, never()).updateTimeslotCapacity(
                anyInt(),
                any(),
                any(),
                any()
        );

        verify(reservationRepository).saveAndFlush(reservation);

        verify(emailService).prepareNotConfirmedEmail(restaurant, reservation);

        assertEquals(ReservationStatuses.NOT_CONFIRMED, reservation.getStatus());
        assertSame(reservation, result);
    }

    @ParameterizedTest
    @EnumSource(
            value = ReservationStatuses.class,
            names = {"NOT_CONFIRMED", "CANCELLED"}
    )
    void updateStatus_shouldNotChangeTimeslotCapacity_whenUnreadBecomesNonConfirmedOrCancelledStatus(
            ReservationStatuses newStatus
    ) {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(10L);

        Reservation reservation = new Reservation();
        reservation.setRestaurant(restaurant);
        reservation.setReservationId(111L);
        reservation.setPartySize(4);
        reservation.setStatus(ReservationStatuses.UNREAD);
        reservation.setLocalDate(LocalDate.of(2026, 9, 1));
        reservation.setLocalTime(LocalTime.of(12, 30));

        when(restaurantService.findById(restaurant.getRestaurantId()))
                .thenReturn(Optional.of(restaurant));

        when(restaurantService.checkIfRestaurantExistsById(restaurant.getRestaurantId()))
                .thenReturn(true);

        when(reservationRepository.existsReservationByReservationId(reservation.getReservationId()))
                .thenReturn(true);

        when(reservationRepository
                .findReservationByReservationIdAndRestaurant_RestaurantId(
                        reservation.getReservationId(),
                        restaurant.getRestaurantId()))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.findById(reservation.getReservationId()))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.findByReservationId(reservation.getReservationId()))
                .thenReturn(Optional.of(reservation));

        Reservation result = reservationService.updateStatus(
                restaurant.getRestaurantId(),
                reservation.getReservationId(),
                newStatus
        );

        verify(timeslotService, never()).updateTimeslotCapacity(
                anyInt(),
                any(),
                any(),
                any()
        );
        verify(reservationRepository).saveAndFlush(reservation);

        if (newStatus == ReservationStatuses.NOT_CONFIRMED) {
            verify(emailService).prepareNotConfirmedEmail(restaurant, reservation);
        } else if (newStatus == ReservationStatuses.CANCELLED) {
            verify(emailService).prepareCancelledEmail(restaurant, reservation);
        }

        assertEquals(newStatus, reservation.getStatus());
        assertSame(reservation, result);
    }
}
