package com.zgurski.service.impl;

import com.zgurski.domain.entities.CalendarDay;
import com.zgurski.domain.entities.Reservation;
import com.zgurski.domain.entities.Restaurant;
import com.zgurski.domain.entities.Timeslot;
import com.zgurski.domain.enums.ReservationStatuses;
import com.zgurski.repository.CalendarDayRepository;
import com.zgurski.repository.ReservationRepository;
import com.zgurski.repository.RestaurantRepository;
import com.zgurski.repository.TimeslotRepository;
import com.zgurski.service.ReservationService;
import com.zgurski.util.email.EmailService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(properties = {
        "spring.jpa.properties.hibernate.jdbc.batch_size=20"
})
class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CalendarDayRepository calendarDayRepository;

    @Autowired
    private TimeslotRepository timeslotRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @MockitoBean
    private EmailService emailService;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void updateStatus_shouldCancelReservationAndDecreaseTimeslotCapacity() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        // GIVEN
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("Test Restaurant");
        restaurant.setContactEmail("test@restaurant.com");
        restaurant.setCreated(now);
        restaurant.setChanged(now);
        restaurant.setIsDeleted(false);
        restaurant.setDefaultTimeslotCapacity(20);

        restaurant = restaurantRepository.saveAndFlush(restaurant);

        LocalDate reservationDate = LocalDate.of(2026, 9, 1);
        LocalTime reservationTime = LocalTime.of(12, 30);

        CalendarDay calendarDay = CalendarDay.builder()
                .localDate(reservationDate)
                .restaurant(restaurant)
                .isOpen(true)
                .created(now)
                .changed(now)
                .isDeleted(false)
                .build();

        calendarDay = calendarDayRepository.saveAndFlush(calendarDay);

        Timeslot timeslot = Timeslot.builder()
                .localTime(reservationTime)
                .calendarDay(calendarDay)
                .currentSlotCapacity(10)
                .maxSlotCapacity(20)
                .isAvailable(true)
                .created(now)
                .changed(now)
                .isDeleted(false)
                .build();

        timeslot = timeslotRepository.saveAndFlush(timeslot);

        Reservation reservation = new Reservation();
        reservation.setPnr("TEST01");
        reservation.setRestaurant(restaurant);
        reservation.setLocalDate(reservationDate);
        reservation.setLocalTime(reservationTime);
        reservation.setPartySize(4);
        reservation.setGuestFullName("Test Guest");
        reservation.setGuestEmail("guest@test.com");
        reservation.setGuestPhone("0000000000");
        reservation.setGuestLanguage("en");
        reservation.setStatus(ReservationStatuses.CONFIRMED);
        reservation.setCreated(now);
        reservation.setChanged(now);
        reservation.setIsDeleted(false);

        reservation = reservationRepository.saveAndFlush(reservation);

        reservationService.updateStatus(
                restaurant.getRestaurantId(),
                reservation.getReservationId(),
                ReservationStatuses.CANCELLED
        );

        Reservation updatedReservation = reservationRepository
                .findByReservationId(reservation.getReservationId()).orElseThrow();

        Timeslot updatedTimeslot = timeslotRepository
                .findById(timeslot.getTimeslotId()).orElseThrow();

        assertEquals(ReservationStatuses.CANCELLED, updatedReservation.getStatus());
        assertEquals(6, updatedTimeslot.getCurrentSlotCapacity());
    }
}