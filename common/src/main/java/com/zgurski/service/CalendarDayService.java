package com.zgurski.service;

import com.zgurski.domain.hibernate.CalendarDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CalendarDayService {

    List<CalendarDay> findAll();

    Page<CalendarDay> findAllPageable(Pageable pageable);

    List<CalendarDay> findAllByRestaurantId(Long id);

    Optional<CalendarDay> findByCalendarDayIdAndRestaurantId(Long calendarDayId, Long restaurantId);

    Optional<CalendarDay> findByDateAndRestaurantId(Long restaurantId, int year, int month, int day);

    CalendarDay save(Long restaurantId, CalendarDay calendarDay);

    CalendarDay update(Long restaurantId, CalendarDay calendarDay);

    Boolean checkIfCalendarDayExistsById(Long id);

    Boolean checkBelongingCalendarDayToRestaurant(Long restaurantId, Long calendarDayId);

    Long deleteSoft(Long restaurantId, Long calendarDayId);
}
