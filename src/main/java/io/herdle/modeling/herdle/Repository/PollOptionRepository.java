package io.herdle.modeling.herdle.Repository;

import io.herdle.modeling.herdle.Entity.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollOptionRepository extends JpaRepository<PollOption, Long> {
    List<PollOption> findAll();

    Optional<PollOption> findById(Long id);
}
