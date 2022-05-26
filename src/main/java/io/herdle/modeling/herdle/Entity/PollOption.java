package io.herdle.modeling.herdle.Entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PollOption {
    @Id
    @GeneratedValue
    private Long id;
    private String label;
    private long position;

    @ManyToOne
    private Poll Poll;
}
