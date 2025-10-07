package ku.cs.mtf_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BillDetailResponse {
    private String id;
    private String step;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private EmployerInfo employer;
    private List<EmployeeInfo> employees;
    private WorkInfo workDetail;

    @Data
    @Builder
    public static class EmployerInfo {
        private String id;
        private String fullName;
        private String companyName;
        private String phoneNumber;
        private String email;
    }

    @Data
    @Builder
    public static class EmployeeInfo {
        private String passportNumber;
        private String fullName;
        private String nationality;
    }

    @Data
    @Builder
    public static class WorkInfo {
        private String id;
        private String workType;
        private String detail;
        private String currentStep;
    }
}
