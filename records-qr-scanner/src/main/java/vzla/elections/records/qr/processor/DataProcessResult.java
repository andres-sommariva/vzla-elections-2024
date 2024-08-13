package vzla.elections.records.qr.processor;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class DataProcessResult {

    private Map<String, Integer> votesPerCandidate;
    private Integer nullVotes;
    private Integer emptyVotes;

}
