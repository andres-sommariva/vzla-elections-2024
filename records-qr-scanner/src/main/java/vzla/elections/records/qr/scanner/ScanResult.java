package vzla.elections.records.qr.scanner;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ScanResult {

    private String filePath;
    private String qrCodeText;
    private Boolean isSuccess;
    private String errorMessage;

    public String getQrCodeText() {
        return (qrCodeText != null) ? qrCodeText : "";
    }
}
