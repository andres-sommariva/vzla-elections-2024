package vzla.elections.records.qr.scanner;

import boofcv.abst.fiducial.QrCodeDetector;
import boofcv.alg.fiducial.qrcode.QrCode;
import boofcv.factory.fiducial.ConfigQrCode;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BoofCVReader {

    public static String readQRCode(String filePath) {
        BufferedImage image = getCroppedImage(UtilImageIO.loadImageNotNull(filePath));

        // Convert to grayscale
        GrayU8 gray = ConvertBufferedImage.convertFrom(image, (GrayU8) null);

        ConfigQrCode config = ConfigQrCode.fast();
        config.considerTransposed = false; // by default, it will consider incorrectly encoded markers. Faster if false

        QrCodeDetector<GrayU8> detector = FactoryFiducial.qrcode(config, GrayU8.class);
        detector.process(gray);

        // Gets a list of all the qr codes it could successfully detect and decode
        List<QrCode> detections = detector.getDetections();

        if (detections.size() > 0) {
            return detections.get(0).message;
        } else {
            if (detector.getFailures().size() > 0) {
                return detector.getFailures().get(0).message;
            }
            throw new RuntimeException("Can not FIND QR code.");
        }
    }

    private static BufferedImage getCroppedImage(BufferedImage temp) {
        return cropImage(temp,
                new Rectangle(
                        0,
                        Double.valueOf(temp.getHeight() * 0.8).intValue(),
                        temp.getWidth(),
                        Double.valueOf(temp.getHeight() * 0.2).intValue()));
    }

    private static BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        BufferedImage dest = src.getSubimage(rect.x, rect.y, rect.width, rect.height);
        return dest;
    }

}
