package com.zgurski.service;

import com.zgurski.domain.hibernate.DefaultWeekDay;
import com.zgurski.domain.hibernate.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DefaultWeekDayService {
    List<DefaultWeekDay> findAll();

    Page<DefaultWeekDay> findAllPageable(Pageable pageable);

    Optional<DefaultWeekDay> findByDefaultWeekDayIdAndRestaurantId(Long defaultWeekDayId, Long restaurantId);

    List<DefaultWeekDay> findScheduleByRestaurantId(Long id);

    DefaultWeekDay save(Long restaurantId, DefaultWeekDay defaultWeekDay);

    DefaultWeekDay update(Long restaurantId, DefaultWeekDay defaultWeekDay);

    Boolean checkIfDefaultWeekDayExistsById(Long id);

    Long deleteSoft(Long restaurantId, Long defaultWeekDayId);
}