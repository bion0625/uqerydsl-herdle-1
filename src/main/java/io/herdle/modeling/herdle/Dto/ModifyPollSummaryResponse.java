package io.herdle.modeling.herdle.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModifyPollSummaryResponse {
//    private Long numberOfVoters;

    private List<ModifyPollSummary> summaries;

    public void setNumberOfVoters(Long voters) {
        if (summaries != null) {


            summaries.forEach(s -> s.setVoters(voters));
            summaries.forEach(s -> s.setPer(String.format("%d%%",(s.getVotes()*100)/voters)));
        }
//        this.numberOfVoters = voters;
    }

    public static ModifyPollSummaryResponse emptyResponse() {

        ModifyPollSummaryResponse response = new ModifyPollSummaryResponse();
        response.setNumberOfVoters(new Long(0));
        response.setSummaries(Collections.emptyList());

        return new ModifyPollSummaryResponse();
    }
}
