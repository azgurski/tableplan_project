package com.zgurski.controller;

import com.zgurski.configuration.WebMVC;
import com.zgurski.controller.hateoas.RestaurantModelAssembler;
import com.zgurski.domain.entities.Restaurant;
import com.zgurski.service.RestaurantService;
import com.zgurski.util.RandomValuesGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.convert.ConversionService;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = RestaurantController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebMVC.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "com\\.zgurski\\.controller\\.converters\\..*"
                )
        }
)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantService restaurantService;

    @MockitoBean
    private RestaurantModelAssembler restaurantAssembler;

    @Autowired
    private ConversionService conversionService;

    @MockitoBean
    private RandomValuesGenerator randomValuesGenerator;

    @MockitoBean
    private PlatformTransactionManager transactionManager;

    @Test
    void findAll_shouldReturn200AndEmptyRestaurantsList() throws Exception {

        when(restaurantService.findAll())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurants").isArray())
                .andExpect(jsonPath("$.restaurants").isEmpty());

        verify(restaurantService).findAll();
    }

    @Test
    void findOneById_shouldReturn200AndRestaurant() throws Exception {
        Long restaurantId = 1L;

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(restaurantId);

        EntityModel<Restaurant> restaurantModel = EntityModel.of(restaurant);

        when(restaurantService.findByRestaurantId(restaurantId))
                .thenReturn(Optional.of(restaurant));

        when(restaurantAssembler.toModel(restaurant))
                .thenReturn(restaurantModel);

        mockMvc.perform(get("/restaurants/{restaurantId}", restaurantId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId").value(restaurantId));

        verify(restaurantService).findByRestaurantId(restaurantId);
        verify(restaurantAssembler).toModel(restaurant);
    }

    @Test
    void deleteSoft_shouldReturn200AndSuccessMessage() throws Exception {
        Long restaurantId = 1L;

        when(restaurantService.deleteSoft(restaurantId))
                .thenReturn(restaurantId);

        mockMvc.perform(delete("/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successMessage")
                        .value("Restaurant with id={1} is deleted."));

        verify(restaurantService).deleteSoft(restaurantId);
    }
}