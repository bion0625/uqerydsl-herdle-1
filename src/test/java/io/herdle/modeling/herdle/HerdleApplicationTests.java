package io.herdle.modeling.herdle;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.herdle.modeling.herdle.Entity.PollOption;
import io.herdle.modeling.herdle.Repository.PollOptionRepository;
import io.herdle.modeling.herdle.Repository.PollRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
@Transactional
@SpringBootTest
class HerdleApplicationTests {

	private EntityManager em;

	private JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);

	@InjectMocks
	PollRepository pollRepository;

//	@InjectMocks
//	PollOptionRepository pollOptionRepository;

	@Mock
	PollOption pollOption;


	@Test
	void contextLoads() {
//		pollOption.setLabel("c");
//		pollOption.setPosition(1);
//		PollOption savePollOption = pollOptionRepository.save(pollOption);
//		System.out.println("savePollOption : " + savePollOption.getLabel());
//		if (savePollOption.getId() != null){
//			PollOption findPollOption = pollOptionRepository.findById(savePollOption.getId()).orElse(null);
//			System.out.println("findPollOption : " + findPollOption.toString());
//		}else{
//			System.out.println("savePollOption.getId() is null");
//		}
//
//		System.out.println("PollOptions : " + pollOptionRepository.findAll().toString());
	}

	@Test
	public void saveData(){
		System.out.println("save start");

		pollRepository.savePollOption("c",1);
		System.out.println("1 pollOptions : " + pollRepository.selectPollOptions().toString());
		pollRepository.savePollOption("c+",2);
		System.out.println("2 pollOptions : " + pollRepository.selectPollOptions().toString());
		pollRepository.savePollOption("java",3);
		System.out.println("3 pollOptions : " + pollRepository.selectPollOptions().toString());
		pollRepository.savePollOption("vue.js",4);
		System.out.println("4 pollOptions : " + pollRepository.selectPollOptions().toString());
		pollRepository.savePollOption("react",5);
		System.out.println("5 pollOptions : " + pollRepository.selectPollOptions().toString());


		pollRepository.savePollAnswer("c");
		pollRepository.savePollAnswer("c");
		pollRepository.savePollAnswer("java");
		pollRepository.savePollAnswer("vue.js");
		pollRepository.savePollAnswer("c+");
		pollRepository.savePollAnswer("java");
		System.out.println("save end");
	}

	@Test
	public void selectPollOptions(){
		saveData();
		System.out.println("pollOptions : " + pollRepository.selectPollOptions().toString());
	}

	@Test
	public void logicCode(){
		saveData();
		System.out.println("PollSummaryResponse : " + pollRepository.logicCode().toString());
	}

}
