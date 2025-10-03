package ku.cs.mtf_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class DocumentPayload {

    @NotBlank(message = "Document type is required")
    @Pattern(
            regexp = "ใบรับรองแพทย์|ใบอนุญาตทำงาน|ประกันสุขภาพ|เอกสาร CI|บัตรชมพู",
            message = "Invalid document type"
    )
    private String type;

    @NotNull(message = "Expiry date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String expiryDate;

    public void setType(String type) {
        this.type = (type == null) ? null : type.strip();
    }
}
