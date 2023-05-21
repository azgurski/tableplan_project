package com.zgurski.repository;

import com.zgurski.domain.hibernate.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Restaurant> findAll(Pageable pageable);

    Optional<Restaurant> findByRestaurantId(Long restaurantId);

    @Modifying
    @Query(value = "update Restaurant r set r.isDeleted = true where r.restaurantId = :restaurantId")
    void delete(Long restaurantId);
}