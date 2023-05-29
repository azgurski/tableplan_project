package com.zgurski.service;

import com.zgurski.domain.entities.CalendarDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarDayService {

    List<CalendarDay> findAll();

    Page<CalendarDay> findAllPageable(Pageable pageable);

    List<CalendarDay> findAllForNextSixtyDays(Long id);

    List<CalendarDay> findAllByMonth(Long restaurantId, int year, int month);

    Optional<CalendarDay> findById(Long restaurantId, Long calendarDayId);

    Optional<CalendarDay> findByCalendarDayIdAndRestaurantId(Long calendarDayId, Long restaurantId);

    Optional<CalendarDay> findByDateAndRestaurantId(Long restaurantId, int year, int month, int day);

    CalendarDay save(Long restaurantId, CalendarDay calendarDay);

    CalendarDay update(Long restaurantId, CalendarDay calendarDay);

    Optional<CalendarDay> checkIfCalendarDayIsPresentByDay(LocalDate localDate, Optional<CalendarDay> calendarDay);

    Boolean checkIfCalendarDayExistsById(Long id);

    Boolean checkBelongingCalendarDayToRestaurant(Long restaurantId, Long calendarDayId, LocalDate localDate);

    Long deleteSoft(Long restaurantId, Long calendarDayId);
}