package com.zgurski.service;

import com.zgurski.domain.hibernate.DefaultTime;
import com.zgurski.domain.hibernate.DefaultWeekDay;
import com.zgurski.domain.hibernate.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DefaultWeekDayService {
    List<DefaultWeekDay> findAll();

    Page<DefaultWeekDay> findAllPageable(Pageable pageable);

    Optional<DefaultWeekDay> findByDefaultWeekDayIdAndRestaurantId(Long defaultWeekDayId, Long restaurantId);

    Optional<DefaultWeekDay> findDefaultWeekDayByDayOfWeekAndRestaurant_RestaurantId(
            DayOfWeek dayOfWeek, Long restaurantId);

    List<DefaultWeekDay> findScheduleByRestaurantId(Long id);

    List<DefaultTime> findAllDefaultTimes();

    DefaultWeekDay save(Long restaurantId, DefaultWeekDay defaultWeekDay);

    DefaultWeekDay update(Long restaurantId, DefaultWeekDay defaultWeekDay);

    Boolean checkIfDefaultWeekDayExistsById(Long id);

    Long deleteSoft(Long restaurantId, Long defaultWeekDayId);
}