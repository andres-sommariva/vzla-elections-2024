package vzla.elections.records.qr.scanner;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ZXingReader {

    static final private Map HINT_MAP = new HashMap() {{
        put(DecodeHintType.CHARACTER_SET, "ISO-8859-1");
        put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(BarcodeFormat.QR_CODE));
        //put(DecodeHintType.ALSO_INVERTED, Boolean.TRUE);
    }};

    public static String readQRCode(String filePath) {
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
        } catch (NotFoundException e) {
            throw new RuntimeException("Can not FIND QR code: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Can not read file: " + e.getMessage());
        }
        return qrCodeResult.getText();
    }

}
