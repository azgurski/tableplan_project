package domain.hibernate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.cache.annotation.Cacheable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(exclude = {
       "timeslots"
})
@ToString(exclude = {
        "timeslots"
})
@Table(name = "c_begin_times")
//@Cacheable
public class BeginTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "begin_time_id")
    @JsonIgnore
    private Long beginTimeId;

    @Column(name = "local_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime localTime;

    @OneToMany(mappedBy = "beginTime", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private Set<Timeslot> timeslots = Collections.emptySet();

    @ManyToMany
    @JoinTable(name = "l_default_times",
            joinColumns = @JoinColumn(name = "c_begin_time_id"),
            inverseJoinColumns = @JoinColumn(name = "default_day_id"))
    @JsonIgnoreProperties("beginTimes")
    private Set<DefaultDay> defaultDays;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}