package com.zgurski.domain.hibernate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
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
//@Cacheable
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
    private Set<DefaultWeekDay> defaultWeekDays;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}