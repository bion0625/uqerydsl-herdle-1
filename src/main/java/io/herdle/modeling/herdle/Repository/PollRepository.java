package io.herdle.modeling.herdle.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.herdle.modeling.herdle.Dto.PollSummary;
import io.herdle.modeling.herdle.Dto.PollSummaryResponse;
import io.herdle.modeling.herdle.Dto.PollVoteRequest;
import io.herdle.modeling.herdle.Entity.PollOption;
import io.herdle.modeling.herdle.Entity.QPoll;
import io.herdle.modeling.herdle.Entity.QPollAnswer;
import io.herdle.modeling.herdle.Entity.QPollOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class PollRepository {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);

    void check(){
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("check");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
    }

    public void savePollAnswer(String label){
        QPollAnswer qPollAnswer = QPollAnswer.pollAnswer;
        jpaQueryFactory.insert(qPollAnswer)
                .columns(qPollAnswer.userChoice)
                .values(label)
                .execute();
    }

    public void savePollOption(String label, int position){
        String insert = "insert into tb_poll_option ('laber','position') values("+label+","+position+")";
        em.createNamedQuery(insert);
//        QPollOption qPollOption = QPollOption.pollOption;
//        jpaQueryFactory.insert(qPollOption)
//                .columns(qPollOption.label,qPollOption.position)
//                .values(label,position)
//                .execute();
    }

    public List<PollOption> selectPollOptions(){



        QPollOption qPollOption = QPollOption.pollOption;
        return jpaQueryFactory.select(Projections.fields(PollOption.class, qPollOption.id, qPollOption.label, qPollOption.position, qPollOption.Poll))
                .from(qPollOption)
                .fetch();
//        return jpaQueryFactory.selectFrom(PollOption).fetch();;
    }

    public PollSummaryResponse logicCode(){
        PollVoteRequest pollVoteRequest = new PollVoteRequest();

        QPoll qPoll = QPoll.poll;
        QPollOption qPollOption = QPollOption.pollOption;
        QPollAnswer qPollAnswer = QPollAnswer.pollAnswer;

        List<PollSummary> response = jpaQueryFactory.select(Projections.constructor(PollSummary.class, qPollOption.label, qPollAnswer.userChoice.count().as("votes")))
                .from(qPoll)
                .join(qPoll.PollOptions, qPollOption)
                .leftJoin(qPollAnswer).on(qPollOption.label.eq(qPollAnswer.userChoice))
                .where(qPoll.id.eq(pollVoteRequest.getId()))
                .groupBy(qPollOption.label)
                .orderBy(qPollOption.position.asc())
                .fetch();

        PollSummaryResponse summaryResponse = new PollSummaryResponse();
        summaryResponse.setSummaries(response);
        summaryResponse.setNumberOfVoters(response.stream().mapToLong(r -> r.getVotes()).sum());

        return summaryResponse;
    }
}
