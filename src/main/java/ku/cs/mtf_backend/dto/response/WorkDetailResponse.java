package ku.cs.mtf_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class WorkDetailResponse {
    private String id;
    private Integer currentStepIndex;
    private String currentStep;
    private String workType;
    private String detail;
    private String status;
    private BigDecimal totalPrice;
    private EmployerSummary employer;
    private List<EmployeeInWork> employeesInWork;
    private AgentSummary agent;

    @Data
    @Builder
    public static class EmployerSummary {
        private String id;
        private String fullName;
        private String companyName;
        private String phoneNumber;
    }

    @Data
    @Builder
    public static class EmployeeInWork {
        private String passportNumber;
        private String fullName;
        private String nationality;
    }

    @Data
    @Builder
    public static class AgentSummary {
        private String id;
        private String fullName;
        private String email;
    }
}
