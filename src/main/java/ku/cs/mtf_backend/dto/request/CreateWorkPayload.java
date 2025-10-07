package ku.cs.mtf_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateWorkPayload {

    @NotBlank(message = "Agent ID is required")
    @Size(min = 13, max = 13, message = "Agent ID must be 13 characters")
    private String agentId;

    @NotBlank(message = "Employer ID is required")
    @Size(min = 13, max = 13, message = "Employer ID must be 13 digits")
    @Pattern(regexp = "^[0-9]*$", message = "Employer ID must contain only digits")
    private String employerId;

    @NotBlank(message = "Work type is required")
    @Pattern(regexp = "ขึ้นทะเบียนใหม่|ต่ออายุใบอนุญาตทำงาน",
             message = "Work type must be 'ขึ้นทะเบียนใหม่' or 'ต่ออายุใบอนุญาตทำงาน'")
    private String workType;

    @NotBlank(message = "Current step is required")
    private String currentStep;

    @NotBlank(message = "Detail is required")
    private String detail;

    @NotNull(message = "Employee list is required")
    @Size(min = 1, message = "At least one employee is required")
    private List<String> employeeIds;  // List of passport numbers

    public void setAgentId(String agentId) {
        this.agentId = (agentId == null) ? null : agentId.strip();
    }

    public void setEmployerId(String employerId) {
        this.employerId = (employerId == null) ? null : employerId.strip();
    }

    public void setWorkType(String workType) {
        this.workType = (workType == null) ? null : workType.strip();
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = (currentStep == null) ? null : currentStep.strip();
    }

    public void setDetail(String detail) {
        this.detail = (detail == null) ? null : detail.strip();
    }
}
