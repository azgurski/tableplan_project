package domain.hibernate;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;

@Data
@Entity
@EqualsAndHashCode(exclude = {
        "restaurant"
})
@ToString(exclude = {
        "restaurant"
})
@Table(name = "billing_details")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@NamedQuery(name = "m_restaurant_multiple_ids_search", query = "select r from Restaurant where r.id = :restaurantIds)
//@Cacheable
public class BillingDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long billingDataId;

    @Column
    @Pattern(regexp = "^(?:(?:IT|SM)\\d{2}[A-Z]\\d{22}|CY\\d{2}[A-Z]\\d{23}|NL\\d{2}[A-Z]{4}\\d{10}|LV\\d{2}[A-Z]{4}" +
            "\\d{13}|(?:BG|BH|GB|IE)\\d{2}[A-Z]{4}\\d{14}|GI\\d{2}[A-Z]{4}\\d{15}|RO\\d{2}[A-Z]{4}\\d{16}|KW" +
            "\\d{2}[A-Z]{4}\\d{22}|MT\\d{2}[A-Z]{4}\\d{23}|NO\\d{13}|(?:DK|FI|GL|FO)\\d{16}|MK\\d{17}" +
            "|(?:AT|EE|KZ|LU|XK)\\d{18}|(?:BA|HR|LI|CH|CR)\\d{19}|(?:GE|DE|LT|ME|RS)\\d{20}|IL\\d{21}|" +
            "(?:AD|CZ|ES|MD|SA)\\d{22}|PT\\d{23}|(?:BE|IS)\\d{24}|(?:FR|MR|MC)\\d{25}|(?:AL|DO|LB|PL)\\d{26}|" +
            "(?:AZ|HU)\\d{27}|(?:GR|MU)\\d{28})$", message = "Enter the correct IBAN.")
    private String iban;

    @Column
    @Pattern(regexp = "([a-zA-Z]{4})([a-zA-Z]{2})" +
            "(([2-9a-zA-Z]{1})([0-9a-np-zA-NP-Z]{1}))" +
            "((([0-9a-wy-zA-WY-Z]{1})" +
            "([0-9a-zA-Z]{2}))|([xX]{3})|)", message = "Enter the correct BIC code.")
    private String bic;

    @Column
    @JsonIgnore
    private Timestamp created;

    @Column
    @JsonIgnore
    private Timestamp changed;

    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @JsonBackReference
    private Restaurant restaurant;
}
