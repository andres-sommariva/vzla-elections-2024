package vzla.elections.records.qr.processor;

import lombok.AllArgsConstructor;
import lombok.Data;
import vzla.elections.records.qr.scanner.ScanResult;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RecordsDataProcessor {

    private final CandidateConfig[] CANDIDATES = new CandidateConfig[] {
            new CandidateConfig("NICOLAS MADURO", 0, 12),
            new CandidateConfig("LUIS MARTINEZ", 13, 18),
            new CandidateConfig("JAVIER BERTUCCI", 19, 19),
            new CandidateConfig("JOSE BRITO", 20, 23),
            new CandidateConfig("ANTONIO ECARRI", 24, 29),
            new CandidateConfig("CLAUDIO FERMIN", 30, 30),
            new CandidateConfig("DANIEL CEBALLOS", 31, 32),
            new CandidateConfig("EDMUNDO GONZALEZ", 33, 35),
            new CandidateConfig("ENRIQUE MARQUEZ", 36, 36),
            new CandidateConfig("BENJAMIN RAUSSEO", 37, 37)
    };

    public DataProcessResult process(List<ScanResult> data) {
        Map<String, Integer> votesPerCandidate = new LinkedHashMap<>();
        AtomicInteger nullVotes = new AtomicInteger();
        AtomicInteger emptyVotes = new AtomicInteger();

        data.stream()
                .filter(ScanResult::getIsSuccess)
                .forEach(scanResult -> {
                    String[] chunks = scanResult.getQrCodeText().split("!");
                    if (chunks.length < 2) {
                        scanResult.setIsSuccess(false);
                        scanResult.setErrorMessage("Can not read votes.");
                    }
                    if (chunks.length >= 2) {
                        String[] votes = chunks[1].split(",");
                        int[] votesValues = Arrays.stream(votes).mapToInt(Integer::parseInt).toArray();
                        calculateVotes(votesPerCandidate, votesValues);
                    }
                    if (chunks.length >= 3) {
                        nullVotes.addAndGet(Integer.parseInt(chunks[2]));
                        //if (Integer.parseInt(chunks[2]) > 0) {
                        //    System.out.println("null: " + scanResult.getFilePath());
                        //}
                    }
                    if (chunks.length == 4) {
                        emptyVotes.addAndGet(Integer.parseInt(chunks[3]));
                        //if (Integer.parseInt(chunks[3]) > 0) {
                        //    System.out.println("empty: " + scanResult.getFilePath());
                        //}
                    }
        });

        DataProcessResult result = DataProcessResult.builder()
                .votesPerCandidate(votesPerCandidate)
                .nullVotes(nullVotes.get())
                .emptyVotes(emptyVotes.get())
                .build();

        return result;
    }

    private void calculateVotes(Map<String, Integer> votesPerCandidate, int[] votesValues) {
        Arrays.stream(CANDIDATES).forEach(candidateConfig -> {
            int votesSum = 0;
            for (int i = candidateConfig.getVotesFromPos(); i <= candidateConfig.getVotesToPos(); i++) {
                votesSum += votesValues[i];
            }
            if (votesPerCandidate.containsKey(candidateConfig.getName())) {
                votesSum += votesPerCandidate.get(candidateConfig.getName());
            }
            votesPerCandidate.put(candidateConfig.getName(), Integer.valueOf(votesSum));
        });
    }

    @Data
    @AllArgsConstructor
    private class CandidateConfig {
        private String name;
        private int votesFromPos;
        private int votesToPos;
    }

}
