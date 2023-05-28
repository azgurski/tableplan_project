package com.zgurski.service;

import com.zgurski.domain.enums.ReservationStatuses;
import com.zgurski.domain.entities.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReservationService {

    List<Reservation> findAll();

    Page<Reservation> findAllPageable(Pageable pageable);

    Optional<Reservation> findByReservationIdAndRestaurantId(Long reservationId, Long restaurantId);

    List<Reservation> findReservationsByRestaurantId(Long id);

    List<Reservation> findAllByDateAndRestaurantId(Long restaurantId, int year, int month, int day);

    List<Reservation> findByStatus(Long restaurantId, ReservationStatuses status);

    List<Reservation> findAllByDateStatusAndRestaurantId(
            Long restaurantId, ReservationStatuses status, int year, int month, int day);

    List<Object> getOccupancyByHour(Long restaurantId, int year, int month, int day);

    Reservation save(Long restaurantId, Reservation reservation);

    Reservation update(Long restaurantId, Reservation reservationToUpdate, int initialPartySize);

    Reservation updateStatus(Long restaurantId, Long reservationId, ReservationStatuses status);

    Long deleteSoft(Long restaurantId, Long reservationId);

    Boolean checkIfReservationExistsById(Long id);
}