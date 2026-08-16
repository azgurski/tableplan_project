package com.zgurski.service.impl;

import com.zgurski.domain.entities.Restaurant;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.repository.RestaurantRepository;
import com.zgurski.service.impl.RestaurantServiceImpl;
import com.zgurski.util.CustomErrorMessageGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private CustomErrorMessageGenerator messageGenerator;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    @Test
    void findById_shouldReturnRestaurant_whenRestaurantExists() {
        Long restaurantId = 10L;
        Restaurant restaurant = new Restaurant();

        when(restaurantRepository.existsRestaurantByRestaurantId(restaurantId))
                .thenReturn(true);

        when(restaurantRepository.findByRestaurantId(restaurantId))
                .thenReturn(Optional.of(restaurant));

        Optional<Restaurant> result = restaurantService.findById(restaurantId);

        assertTrue(result.isPresent());
        assertSame(restaurant, result.get());

        verify(restaurantRepository)
                .existsRestaurantByRestaurantId(restaurantId);

        verify(restaurantRepository)
                .findByRestaurantId(restaurantId);
    }

    @Test
    void findById_shouldThrowException_whenRestaurantDoesNotExist() {
        Long restaurantId = 10L;

        when(restaurantRepository.existsRestaurantByRestaurantId(restaurantId))
                .thenReturn(false);

        assertThrows(
                EntityNotFoundException.class,
                () -> restaurantService.findById(restaurantId)
        );

        verify(restaurantRepository, never())
                .findByRestaurantId(restaurantId);
    }

    @Test
    void update_shouldReturnRestaurant_whenRestaurantExists() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(10L);

        when(restaurantRepository.existsRestaurantByRestaurantId(restaurant.getRestaurantId()))
                .thenReturn(true);

        when(restaurantRepository.save(restaurant))
                .thenReturn(restaurant);

        Restaurant result = restaurantService.update(restaurant);

        assertSame(restaurant, result);

        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void update_shouldThrowException_whenRestaurantDoesNotExist() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(10L);

        when(restaurantRepository.existsRestaurantByRestaurantId(restaurant.getRestaurantId()))
                .thenReturn(false);

        assertThrows(
                EntityNotFoundException.class,
                () -> restaurantService.update(restaurant)
        );

        verify(restaurantRepository, never()).save(restaurant);
    }
}
