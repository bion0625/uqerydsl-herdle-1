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
public class PollSummaryResponse {
    private Long numberOfVoters;

    private List<PollSummary> summaries;

    public void setNumberOfVoters(Long voters) {
        if (summaries != null) {
            summaries.forEach(s -> s.setVoters(voters));
        }
    }

    public static PollSummaryResponse emptyResponse() {

        PollSummaryResponse response = new PollSummaryResponse();
        response.setNumberOfVoters(new Long(0));
        response.setSummaries(Collections.emptyList());

        return new PollSummaryResponse();
    }
}
