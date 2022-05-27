package io.herdle.modeling.herdle;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.herdle.modeling.herdle.Dto.PollSummary;
import io.herdle.modeling.herdle.Dto.PollSummaryResponse;
import io.herdle.modeling.herdle.Dto.PollVoteRequest;
import io.herdle.modeling.herdle.Entity.*;
import io.herdle.modeling.herdle.Repository.PollRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
@Transactional
@SpringBootTest
class HerdleApplicationTests {

	@PersistenceContext
	EntityManager em;


	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	public void saveBaseData(){

		JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);

		System.out.println("save start");

		PollOption pollOptionC = new PollOption();
		pollOptionC.setLabel("c");
		pollOptionC.setPosition(1);
		em.persist(pollOptionC);

		PollOption pollOptionCPlus = new PollOption();
		pollOptionCPlus.setLabel("c+");
		pollOptionCPlus.setPosition(2);
		em.persist(pollOptionCPlus);

		PollOption pollOptionJava = new PollOption();
		pollOptionJava.setLabel("java");
		pollOptionJava.setPosition(3);
		em.persist(pollOptionJava);

		PollOption pollOptionReact = new PollOption();
		pollOptionReact.setLabel("react");
		pollOptionReact.setPosition(4);
		em.persist(pollOptionReact);

		List<PollOption> pollOptions = new ArrayList<>();
		pollOptions.add(pollOptionC);
		pollOptions.add(pollOptionCPlus);
		pollOptions.add(pollOptionJava);
		pollOptions.add(pollOptionReact);

		Poll poll = new Poll();
		poll.setPollOptions(pollOptions);
		em.persist(poll);



		Random random = new Random();
		for (int i = 0; i < 100; i++) {

			PollAnswer pollAnswer = new PollAnswer();

			int k = random.nextInt(4) + 1;


			QPollOption qPollOption = QPollOption.pollOption;
			List<PollOption> pollOptionList = jpaQueryFactory
					.selectFrom(qPollOption)
					.fetch();

			for (PollOption pollOption : pollOptionList) {
				if (pollOption.getPosition() == (long)k){
					pollAnswer.setUserChoice(pollOption.getLabel());
					em.persist(pollAnswer);
				}
			}
		}

		System.out.println("save end");
	}

	@Test
	public void selectPollOptions(){
		saveBaseData();
		JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);

		QPoll qPoll = QPoll.poll;

		Poll poll = jpaQueryFactory
				.selectFrom(qPoll)
				.fetchOne();

		QPollAnswer qPollAnswer = QPollAnswer.pollAnswer;

		List<PollAnswer> pollAnswers = jpaQueryFactory
				.selectFrom(qPollAnswer)
				.fetch();

		System.out.println("poll : " + poll.toString());
		System.out.println("pollOptions : " + poll.getPollOptions().toString());
		System.out.println("pollAnswers : " + pollAnswers.toString());
	}

	@Test
	public void logicCode(){

		saveBaseData();

		QPoll qPoll = QPoll.poll;
		QPollOption qPollOption = QPollOption.pollOption;
		QPollAnswer qPollAnswer = QPollAnswer.pollAnswer;

		JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);

		Poll poll = jpaQueryFactory
				.selectFrom(qPoll)
				.fetchOne();

		PollVoteRequest pollVoteRequest = new PollVoteRequest();
		pollVoteRequest.setId(poll.getId());

		//logicCode start
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
		//logicCode end

		System.out.println("summaryResponse : " + summaryResponse.toString());
	}

}
