package io.herdle.modeling.herdle.Entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class PollAnswer {
    @Id
    @GeneratedValue
    private Long id;
    private String userChoice;
}
