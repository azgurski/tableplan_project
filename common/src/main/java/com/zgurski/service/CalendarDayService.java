package com.zgurski.service;

import com.zgurski.domain.hibernate.CalendarDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CalendarDayService {

    List<CalendarDay> findAll();

    Page<CalendarDay> findAllPageable(Pageable pageable);

    Optional<CalendarDay> findByCalendarDayIdAndRestaurantId(Long calendarDayId, Long restaurantId);

    List<CalendarDay> findScheduleByRestaurantId(Long id);

    CalendarDay save(Long restaurantId, CalendarDay calendarDay);

    CalendarDay update(Long restaurantId, CalendarDay calendarDay);

    Boolean checkIfCalendarDayExistsById(Long id);

    Long deleteSoft(Long restaurantId, Long calendarDayId);
}
