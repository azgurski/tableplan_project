package com.zgurski.service;

import com.zgurski.domain.hibernate.Reservation;
import com.zgurski.domain.hibernate.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReservationService {

    List<Reservation> findAll();

    Page<Reservation> findAllPageable(Pageable pageable);

    Optional<Reservation> findByReservationIdAndRestaurantId(Long reservationId, Long restaurantId);

    List<Reservation> findReservationsByRestaurantId(Long id);

    Reservation save(Reservation reservation);

    Reservation update(Reservation reservation);

    Boolean checkIfReservationExistsById(Long id);

    Optional<Reservation> deleteSoft(Long restaurantId, Long reservationId);
}
