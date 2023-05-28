package com.zgurski.controller.requests.searchcriteria;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Schema(description = "Search criteria object: search by country, text description, capacity")
public class RestaurantSearchCriteria {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "query", type = "string", description = "text query")
    @NotNull
    @Size(min = 1, max = 100)
    private String query;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "France", type = "country", description = "restaurant country")
    @NotNull
    @Size(min = 1, max = 100)
    private String country;

//    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "NOT_SELECTED", type = "Capacity", description = "restaurant capacity")
//    @NotNull
//    private Capacity capacity;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}

