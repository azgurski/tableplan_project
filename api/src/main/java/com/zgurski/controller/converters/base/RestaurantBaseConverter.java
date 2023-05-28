package com.zgurski.controller.converters.base;

import com.zgurski.controller.requests.RestaurantCreateRequest;
import com.zgurski.domain.entities.Restaurant;
import org.springframework.core.convert.converter.Converter;

public abstract class RestaurantBaseConverter<S, T> implements Converter<S, T> {

    public Restaurant doConvert(Restaurant restaurantForUpdate,
                                RestaurantCreateRequest request) {

        restaurantForUpdate.setRestaurantName(request.getRestaurantName());
        restaurantForUpdate.setContactEmail(request.getContactEmail());
        restaurantForUpdate.setPhone(request.getPhone());
        restaurantForUpdate.setAddress(request.getAddress());
        restaurantForUpdate.setPostalCode(request.getPostalCode());
        restaurantForUpdate.setCity(request.getCity());
        restaurantForUpdate.setCountry(request.getCountry());
        restaurantForUpdate.setWebsite(request.getWebsite());
        restaurantForUpdate.setRestaurantLanguage(request.getRestaurantLanguage());
        restaurantForUpdate.setRestaurantTimezone(request.getRestaurantTimezone());
        restaurantForUpdate.setImageURL(request.getImageURL());
        restaurantForUpdate.setLogoURL(request.getLogoURL());
        restaurantForUpdate.setDefaultTimeslotCapacity(request.getDefaultTimeslotCapacity());

        /* System fields filling */
        restaurantForUpdate.setIsDeleted(false);

        return restaurantForUpdate;
    }
}