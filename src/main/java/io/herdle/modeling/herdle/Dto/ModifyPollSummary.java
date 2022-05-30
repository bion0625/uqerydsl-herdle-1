package io.herdle.modeling.herdle.Dto;

import lombok.Data;

@Data
public class ModifyPollSummary {
    /** 문구 **/
    private String label;

    /** 총 개수 대비 문항 그룹별 개수 비율 **/
    private String per;

    /** 문항 그룹별 개수 **/
    private Long votes;

    /** 총 개수 **/
    private Long voters;

    public ModifyPollSummary(String label, Long votes) {
        this.label=label;
        this.votes=votes;
    }

    public Long getAverage() {

        return (this.votes / this.voters) * 100;
    }
}
