package com.zgurski.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.cache.annotation.Cacheable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(exclude = {
        "defaultWeekDays"
})
@ToString(exclude = {
        "defaultWeekDays"
})
@Table(name = "default_times")
@Cacheable
public class DefaultTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "default_time_id")
    private Long defaultTimeId;

    @Column(name = "local_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime localTime;

    @ManyToMany
    @JoinTable(name = "l_default_week_days_times",
            joinColumns = @JoinColumn(name = "default_time_id"),
            inverseJoinColumns = @JoinColumn(name = "default_week_day_id"))
    @JsonIgnoreProperties("defaultTimes")
    @JsonIgnore
    private Set<DefaultWeekDay> defaultWeekDays;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}