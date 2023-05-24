package com.zgurski.repository;

import com.zgurski.domain.enums.ReservationStatuses;
import com.zgurski.domain.hibernate.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        PagingAndSortingRepository<Reservation, Long>, CrudRepository<Reservation, Long> {

    Optional<Reservation> findByReservationId(Long reservationId);

    Optional<Reservation> findReservationByReservationIdAndRestaurant_RestaurantId(Long reservationId, Long restaurantId);

    List<Reservation> findReservationsByRestaurant_RestaurantIdOrderByLocalDateAscLocalTimeAsc(Long restaurantId);

    List<Reservation> findReservationsByRestaurant_RestaurantIdAndStatusOrderByLocalDateAscLocalTimeAsc
            (Long restaurantId, ReservationStatuses status);


    Boolean existsReservationByReservationId(Long reservationId);

    @Modifying
    @Query(value = "update Reservation rsv set rsv.isDeleted = true, rsv.changed = NOW() where rsv.reservationId = :reservationId")
    void deleteSoft(Long reservationId);
}