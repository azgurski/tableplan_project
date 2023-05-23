package com.zgurski.repository;

import com.zgurski.domain.hibernate.DefaultWeekDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface DefaultWeekDayRepository extends JpaRepository<DefaultWeekDay, Long>,
        PagingAndSortingRepository<DefaultWeekDay, Long>, CrudRepository<DefaultWeekDay, Long> {

    Optional<DefaultWeekDay> findById(Long defaultWeekDayId);

    List<DefaultWeekDay> findDefaultWeekDaysByRestaurant_RestaurantIdOrderByDayOfWeek(Long restaurantId);

    Boolean existsByDefaultWeekDayId(Long defaultWeekDayId);

    @Modifying
    @Query(value = "update DefaultWeekDay dwd set dwd.isDeleted = true, dwd.changed = NOW() where dwd.defaultWeekDayId = :defaultWeekDayId")
    void deleteSoft(Long defaultWeekDayId);
}
