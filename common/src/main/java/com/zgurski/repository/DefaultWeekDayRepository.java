package com.zgurski.repository;

import com.zgurski.domain.hibernate.CalendarDay;
import com.zgurski.domain.hibernate.DefaultWeekDay;
import com.zgurski.domain.hibernate.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface DefaultWeekDayRepository extends JpaRepository<DefaultWeekDay, Long>,
        PagingAndSortingRepository<DefaultWeekDay, Long>, CrudRepository<DefaultWeekDay, Long> {

    Optional<DefaultWeekDay> findById(Long defaultWeekDayId);

    Optional<DefaultWeekDay> findDefaultWeekDayByDefaultWeekDayIdAndRestaurant_RestaurantId
            (Long defaultWeekDayId, Long restaurantId);

    Optional<DefaultWeekDay> findDefaultWeekDayByDayOfWeekAndIsOpenAndRestaurant_RestaurantId(
            DayOfWeek dayOfWeek, Boolean isOpen, Long restaurantId);

    List<DefaultWeekDay> findDefaultWeekDaysByRestaurant_RestaurantIdOrderByDayOfWeek(Long restaurantId);

    Boolean existsByDefaultWeekDayId(Long defaultWeekDayId);

    Boolean existsByDayOfWeekAndRestaurant_RestaurantId(DayOfWeek dayOfWeek, Long restaurantId);

    @Modifying
    @Query(value = "update DefaultWeekDay dwd set dwd.isOpen = false, dwd.isDeleted = true, dwd.changed = NOW() " +
            "where dwd.defaultWeekDayId = :defaultWeekDayId")
    void deleteSoft(Long defaultWeekDayId);
}