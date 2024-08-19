package vzla.elections.records.qr.scanner;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordsScanner {

    final private List<String> files;
    final private List<ScanResult> scanResults = new ArrayList<>();

    public RecordsScanner(List<String> files) {
        this.files = files;
    }

    public List<ScanResult> scan() {
        this.files.stream().parallel().forEach(file -> {
            ScanResult.ScanResultBuilder resultBuilder = ScanResult.builder();
            resultBuilder.filePath(file);

            try {
                resultBuilder.qrCodeText(readQRCode(file));
                resultBuilder.isSuccess(true);
            } catch (Exception e) {
                resultBuilder.isSuccess(false);
                resultBuilder.errorMessage(e.getMessage());
            } finally {
                this.scanResults.add(resultBuilder.build());
            }
        });

        return this.scanResults;
    }

    private String readQRCode(String filePath) throws IOException, NotFoundException, ChecksumException, FormatException {
        String scanType =  System.getProperty("scan.type", "boofcv");
        if ("boofcv".equals(scanType)) {
            return BoofCVReader.readQRCode(filePath);
        }
        return ZXingReader.readQRCode(filePath);
    }
}
