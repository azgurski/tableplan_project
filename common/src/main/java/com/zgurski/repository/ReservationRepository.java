package com.zgurski.repository;

import com.zgurski.domain.enums.ReservationStatuses;
import com.zgurski.domain.hibernate.Reservation;
import com.zgurski.domain.hibernate.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        PagingAndSortingRepository<Reservation, Long>, CrudRepository<Reservation, Long> {

    Optional<Reservation> findByReservationId(Long reservationId);

    Optional<Reservation> findReservationByReservationIdAndRestaurant_RestaurantId(Long reservationId, Long restaurantId);

    List<Reservation> findReservationsByRestaurant_RestaurantIdOrderByLocalDateAscLocalTimeAsc(Long restaurantId);

    List<Reservation> findReservationsByRestaurant_RestaurantIdAndStatusOrderByLocalDateAscLocalTimeAsc
            (Long restaurantId, ReservationStatuses status);

    @Query(value = "select rsv from Reservation rsv where rsv.localDate = :localDate and rsv.restaurant = :restaurant " +
            "order by rsv.localDate asc, rsv.localTime asc")
    List<Reservation> findAllByDateAndRestaurant(LocalDate localDate, Restaurant restaurant);


    @Query(value = "select rsv from Reservation rsv where rsv.localDate = :localDate and rsv.status = :status " +
            "and rsv.restaurant = :restaurant order by rsv.localDate asc, rsv.localTime asc")
    List<Reservation> findAllByDateStatusAndRestaurantId (LocalDate localDate, ReservationStatuses status,
                                                          Restaurant restaurant);

    @Query(value = "select * from occupancy_table_by_hour(:restaurantId, :year, :month, :day)", nativeQuery = true)
    List<Object> getOccupancyByHour(Long restaurantId, int year, int month, int day);

    Boolean existsReservationByReservationId(Long reservationId);

    @Modifying
    @Query(value = "update Reservation rsv set rsv.isDeleted = true, rsv.changed = NOW() where rsv.reservationId = :reservationId")
    void deleteSoft(Long reservationId);
}