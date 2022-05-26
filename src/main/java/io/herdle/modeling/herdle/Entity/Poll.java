package io.herdle.modeling.herdle.Entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data
public class Poll {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany
    private List<PollOption> PollOptions;
}
