package com.zgurski.repository;

import com.zgurski.domain.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>,
        PagingAndSortingRepository<Restaurant, Long>, CrudRepository<Restaurant, Long> {

    List<Restaurant> findAll();

    Optional<Restaurant> findByRestaurantId(Long restaurantId);

    Boolean existsRestaurantByRestaurantId(Long restaurantId);

    @Modifying
    @Query(value = "update Restaurant r set r.isDeleted = true, r.changed = NOW() " +
            "where r.restaurantId = :restaurantId")
    void deleteSoft(Long restaurantId);
}