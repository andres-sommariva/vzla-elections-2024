package vzla.elections.records.qr;

import lombok.Getter;
import vzla.elections.records.qr.processor.DataProcessResult;
import vzla.elections.records.qr.processor.RecordsDataProcessor;
import vzla.elections.records.qr.scanner.RecordsScanner;
import vzla.elections.records.qr.scanner.ScanResult;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Application {

    private static final String SAMPLE_FILES_PATH = new File("").getAbsolutePath() + "/src/main/resources/samples";

    /**
     * The main method that orchestrates the scanning, processing, and printing of election records.
     * <p>
     * args[0] = path to files <br>
     * args[1] = from index <br>
     * args[2] = to index <br>
     * // (not yet) args[3] = path to output file <br>
     *
     * @param args the command-line arguments
     * @return void
     */
    public static void main(String[] args) {
        final RunContext runContext = new RunContext(args);

        List<String> filesPath = Arrays.asList(
                Arrays.stream(new File(runContext.getFilesFolderPath()).list())
                        .map(f -> runContext.getFilesFolderPath() + File.separator + f)
                        .sorted()
                        .toArray(String[]::new)
        );

        int fromIndex = (filesPath.size() < runContext.getFromIndex()) ? 0 : runContext.getFromIndex();
        int toIndex = Math.min(filesPath.size(), runContext.getToIndex());
        filesPath = filesPath.subList(fromIndex, toIndex);

        System.out.println(runContext);
        System.out.println("Files to process: " + filesPath.size());
        //filesPath.stream().forEach(System.out::println);

        long start, end;
        start = System.currentTimeMillis();
        RecordsScanner scanner = new RecordsScanner(filesPath);
        List<ScanResult> scanResults = scanner.scan();
        end = System.currentTimeMillis();
        System.out.println("Scan - Elapsed time: " + (end - start) + " ms");

        start = System.currentTimeMillis();
        RecordsDataProcessor processor = new RecordsDataProcessor();
        DataProcessResult processResult = processor.process(scanResults);
        end = System.currentTimeMillis();
        System.out.println("Count - Elapsed time: " + (end - start) + " ms");

        printScanResults(scanResults);
        printProcessorResults(processResult);
        //dumpErrors(scanResults);
    }

    private static void printScanResults(List<ScanResult> scanResults) {
        long total, success, error;
        total = scanResults.size();
        success = scanResults.stream().filter(ScanResult::getIsSuccess).count();
        error = scanResults.stream().filter(r -> !r.getIsSuccess()).count();

        System.out.println("------------------------------------------------------------------------------------");
        System.out.println(String.format("Files processed:   %5d", total));
        System.out.println(String.format(" - Without errors: %5d (%.2f %%)", success, (success * 100.0) / total));
        System.out.println(String.format(" - With errors:    %5d (%.2f %%)", error, (error * 100.0) / total));
    }

    private static void printProcessorResults(DataProcessResult processResult) {
        Map<String, Integer> votesPerCandidate = processResult.getVotesPerCandidate();
        int totalVotes = votesPerCandidate.values().stream().reduce(0, Integer::sum);
        System.out.println("------------------------------------------------------------------------------------");
        votesPerCandidate.keySet().forEach(
                key -> System.out.println(
                        String.format(" - %20s: %12d (%6.2f %%)",
                                key,
                                votesPerCandidate.get(key),
                                (votesPerCandidate.get(key) * 100.0) / totalVotes))
        );
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println(String.format(" -          Valid votes: %12d", totalVotes));
        System.out.println(String.format(" -           Null votes: %12d", processResult.getNullVotes()));
        System.out.println(String.format(" -          Empty votes: %12d", processResult.getEmptyVotes()));
    }

    private static void dumpErrors(List<ScanResult> scanResults) {
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Files with errors:");
        scanResults
                .stream()
                .filter(r -> !r.getIsSuccess())
                .forEach(
                        result -> System.out.println(String.format("%s \n - Error: %s \n - Code: %s",
                                result.getFilePath(),
                                result.getErrorMessage(),
                                result.getQrCodeText()))
                );
    }

    @Getter
    private static class RunContext {
        final String filesFolderPath; //args[0]
        final int fromIndex; //args[1]
        final int toIndex; //args[2]

        public RunContext(String[] args) {
            if (args.length > 0) {
                this.filesFolderPath = args[0];
            } else {
                filesFolderPath = SAMPLE_FILES_PATH;
            }
            this.fromIndex = (args.length > 1) ? Integer.parseInt(args[1]) : 0;
            this.toIndex = (args.length > 2) ? Integer.parseInt(args[2]) : Integer.MAX_VALUE;
        }

        @Override
        public String toString() {
            return "RunContext [filesFolderPath=" + filesFolderPath + ", fromIndex=" + fromIndex + ", toIndex=" + toIndex + "]";
        }

    }

}
