package com.zgurski.repository;

import com.zgurski.domain.entities.CalendarDay;
import com.zgurski.domain.entities.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
class CalendarDayRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private CalendarDayRepository calendarDayRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deleteSoft_shouldMarkCalendarDayAsDeletedAndClosed() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("Test Restaurant");
        restaurant.setContactEmail("test@restaurant.com");
        restaurant.setCreated(now);
        restaurant.setChanged(now);
        restaurant.setIsDeleted(false);
        restaurant.setDefaultTimeslotCapacity(20);

        restaurant = restaurantRepository.saveAndFlush(restaurant);

        CalendarDay calendarDay = CalendarDay.builder()
                .localDate(LocalDate.of(2026, 9, 1))
                .restaurant(restaurant)
                .isOpen(true)
                .created(now)
                .changed(now)
                .isDeleted(false)
                .build();

        calendarDay = calendarDayRepository.saveAndFlush(calendarDay);

        Long calendarDayId = calendarDay.getCalendarDayId();

        calendarDayRepository.deleteSoft(calendarDayId);

        entityManager.flush();
        entityManager.clear();

        CalendarDay updatedCalendarDay = calendarDayRepository.findById(calendarDayId)
                .orElseThrow();

        assertFalse(updatedCalendarDay.getIsOpen());
        assertTrue(updatedCalendarDay.getIsDeleted());
        assertNotNull(updatedCalendarDay.getChanged());
    }

    @Test
    void save_shouldThrowException_whenCalendarDayAlreadyExistsForRestaurantAndDate() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("Test Restaurant");
        restaurant.setContactEmail("test@restaurant.com");
        restaurant.setCreated(now);
        restaurant.setChanged(now);
        restaurant.setIsDeleted(false);
        restaurant.setDefaultTimeslotCapacity(20);

        restaurant = restaurantRepository.saveAndFlush(restaurant);

        LocalDate date = LocalDate.of(2026, 9, 1);

        CalendarDay firstCalendarDay = CalendarDay.builder()
                .localDate(date)
                .restaurant(restaurant)
                .isOpen(true)
                .created(now)
                .changed(now)
                .isDeleted(false)
                .build();

        calendarDayRepository.saveAndFlush(firstCalendarDay);

        CalendarDay secondCalendarDay = CalendarDay.builder()
                .localDate(date)
                .restaurant(restaurant)
                .isOpen(true)
                .created(now)
                .changed(now)
                .isDeleted(false)
                .build();

        assertThrows(
                DataIntegrityViolationException.class,
                () -> calendarDayRepository.saveAndFlush(secondCalendarDay)
        );
    }
}
