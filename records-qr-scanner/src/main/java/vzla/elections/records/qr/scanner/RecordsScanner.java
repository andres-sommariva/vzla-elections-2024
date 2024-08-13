package vzla.elections.records.qr.scanner;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class RecordsScanner {

    static final private Map HINT_MAP = new HashMap() {{
        put(DecodeHintType.CHARACTER_SET, "ISO-8859-1");
        put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(BarcodeFormat.QR_CODE));
        //put(DecodeHintType.ALSO_INVERTED, Boolean.TRUE);
    }};

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
            } catch (IOException e) {
                resultBuilder.isSuccess(false);
                resultBuilder.errorMessage("Can not read file: " + e.getMessage());
            } catch (NotFoundException e) {
                resultBuilder.isSuccess(false);
                resultBuilder.errorMessage("Can not FIND QR code: " + e.getMessage());
            } catch (FormatException e) {
                resultBuilder.isSuccess(false);
                resultBuilder.errorMessage("Can not READ QR code: " + e.getMessage());
            } catch (ChecksumException e) {
                resultBuilder.isSuccess(false);
                resultBuilder.errorMessage("Error correction fail: " + e.getMessage());
            } catch (Exception e) {
                resultBuilder.isSuccess(false);
                resultBuilder.errorMessage("Unexpected error: " + e.getMessage());
            } finally {
                this.scanResults.add(resultBuilder.build());
            }
        });

        return this.scanResults;
    }

    private String readQRCode(String filePath) throws IOException, NotFoundException, ChecksumException, FormatException {
        Result qrCodeResult;
        try (FileInputStream fis = new FileInputStream(filePath)) {
            BufferedImage image = ImageIO.read(fis);
            BinaryBitmap binaryBitmap = new BinaryBitmap(
                    new HybridBinarizer(
                            new BufferedImageLuminanceSource(image)
                                    .crop(
                                            0,
                                            Double.valueOf(image.getHeight() * 0.8).intValue(),
                                            image.getWidth(),
                                            Double.valueOf(image.getHeight() * 0.2).intValue())
                    ));
            qrCodeResult = new MultiFormatReader().decode(binaryBitmap, HINT_MAP);
            //qrCodeResult = new QRCodeReader().decode(binaryBitmap, HINT_MAP);
        }
        return qrCodeResult.getText();
    }
}
