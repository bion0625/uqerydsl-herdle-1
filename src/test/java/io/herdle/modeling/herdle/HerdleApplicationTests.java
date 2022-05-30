package io.herdle.modeling.herdle;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.herdle.modeling.herdle.Dto.*;
import io.herdle.modeling.herdle.Entity.*;
import io.herdle.modeling.herdle.Repository.PollRepository;
import org.junit.jupiter.api.Assertions;
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
import java.util.stream.LongStream;

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
	public void logicCode(){
		/** 투표자 수 설정 **/
		int voters = 1000;

		/** 문항 그룹 설정 **/
		String [] label = {"c","c+","java","react","vue.js"};

		List<PollOption> pollOptions = new ArrayList<>();
		for (int i = 0; i < label.length; i++) {
			PollOption pollOption = new PollOption();
			pollOption.setPosition(i+1);
			pollOption.setLabel(label[i]);
			pollOptions.add(pollOption);
		}

		//문항 그룹 설정 및 랜덤 투표 저장
		saveBaseData(voters, pollOptions);

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

		Assertions.assertTrue(summaryResponse.getSummaries().stream().mapToLong(value -> value.getVotes()).sum() == voters);
		Assertions.assertTrue(summaryResponse.getSummaries().stream().mapToLong(value -> value.getVotes()).count() == label.length);
		/*
		* pollSummaryResponse 의 setNumberOfVoters 메소드의 경우, 파라미터(전체 투표수)를 받으면
		* 내부 프로퍼티인 List<PollSummary> summaries 의 리스트 개별 값의 프로퍼티인 voters 에 값을 할당하고
		* 자기 자신의 또 다른 프로퍼티인 numberOfVoters 에는 값을 할당하지 않습니다.
		*
		* 따라서 필연적으로 pollSummaryResponse 의 numberOfVoters 는 null 이 되는데요. 총 투표수는 어쨌든
		* List<PollSummary> summaries 의 개별 리스트에 각기 들어가므로 numberOfVoters 로 따로 관리할 필요 없어보여
		* setNumberOfVoters 메소드는 남겨두되 numberOfVoters 프로퍼티는 제거하는 게
		* 좋을 거 같습니다(아래의 수정된 DTO 에 작업 후 주석처리 해두었습니다).
		*
		* 그럼에도 굳이 numberOfVoters 프로퍼티를 남겨두고자 한다면, setNumberOfVoters 로직 하단부에
		* "this.numberOfVoters = voters;" 코드를 추가해 두어야 값이 들어갈 것으로
		* 보입니다(아래의 수정된 DTO 에 작업 후 주석처리 해두었습니다).
		*
		* 추가로, 해당 로직은 전체 투표 수와 각 과목당 나누어진 투표 수를 계산하는 로직으로 보이는데요. 통계를 목적으로 한다면
		* 비율을 표시하는 프로퍼티가 추가되면 좋을 거 같습니다. PollSummaryResponse 를 기준으로 해당 프로퍼티 이름을 "per"로 추가해서
		* 작업한 ModifyPollSummary 와 ModifyPollSummaryResponse DTO 를 아래와 같이 만들어 로직을 짜보았습니다.
		*/
		List<ModifyPollSummary> modifyResponse = jpaQueryFactory.select(Projections.constructor(ModifyPollSummary.class, qPollOption.label, qPollAnswer.userChoice.count().as("votes")))
				.from(qPoll)
				.join(qPoll.PollOptions, qPollOption)
				.leftJoin(qPollAnswer).on(qPollOption.label.eq(qPollAnswer.userChoice))
				.where(qPoll.id.eq(pollVoteRequest.getId()))
				.groupBy(qPollOption.label)
				.orderBy(qPollOption.position.asc())
				.fetch();

		ModifyPollSummaryResponse modifyPollSummaryResponse = new ModifyPollSummaryResponse();
		modifyPollSummaryResponse.setSummaries(modifyResponse);
		modifyPollSummaryResponse.setNumberOfVoters(response.stream().mapToLong(r -> r.getVotes()).sum());

		Assertions.assertTrue(modifyPollSummaryResponse.getSummaries().stream().mapToLong(value -> value.getVotes()).sum() == voters);
		Assertions.assertTrue(modifyPollSummaryResponse.getSummaries().stream().mapToLong(value -> value.getVotes()).count() == label.length);
	}

	@Test
	@Transactional
	public void saveBaseData(int voters, List<PollOption> pollOptions){

		JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);

		for (int i = 0; i < pollOptions.size(); i++) {
			em.persist(pollOptions.get(i));
		}

		Poll poll = new Poll();
		poll.setPollOptions(pollOptions);
		em.persist(poll);

		Random random = new Random();
		for (int i = 0; i < voters; i++) {

			PollAnswer pollAnswer = new PollAnswer();

			int k = random.nextInt(pollOptions.size()) + 1;


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
	}
}
