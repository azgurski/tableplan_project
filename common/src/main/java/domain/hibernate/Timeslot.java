package domain.hibernate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Entity
@EqualsAndHashCode(exclude = {
        "calendarDay"
//        ,"beginTime"
})
@ToString(exclude = {
        "calendarDay"
        //        ,"beginTime"
})
@Table(name = "timeslots")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@NamedQuery(name = "m_restaurant_multiple_ids_search", query = "select r from Restaurant where r.id = :restaurantIds)
//@Cacheable
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timeslot_id")
    private Long timeslotId;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Column(name = "current_slot_capacity")
    private Integer currentSlotCapacity;

    @Column(name = "max_slot_capacity")
    private Integer maxSlotCapacity;

    @Column
    @JsonFormat(pattern = "HH:mm") //TODO may be yyyy-MM-dd HH:mm:ss
    private Timestamp created;

    @Column
    @JsonIgnore
    private Timestamp changed;

    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "calendar_day_id")
    @JsonBackReference
    private CalendarDay calendarDay;

    @ManyToOne
    @JoinColumn(name = "begin_time_id")
    @JsonBackReference
    private BeginTime beginTime;
}