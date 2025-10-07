package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.config.WorkStepConfig;
import ku.cs.mtf_backend.dto.request.CreateWorkPayload;
import ku.cs.mtf_backend.dto.response.WorkDetailResponse;
import ku.cs.mtf_backend.entity.*;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkService {

    private WorkRepository workRepository;
    private BillRepository billRepository;
    private WorkDetailRepository workDetailRepository;
    private PricingRepository pricingRepository;
    private EmployeeRepository employeeRepository;
    private EmployerRepository employerRepository;
    private AgentRepository agentRepository;

    public WorkService(WorkRepository workRepository,
                       BillRepository billRepository,
                       WorkDetailRepository workDetailRepository,
                       PricingRepository pricingRepository,
                       EmployeeRepository employeeRepository,
                       EmployerRepository employerRepository,
                       AgentRepository agentRepository) {
        this.workRepository = workRepository;
        this.billRepository = billRepository;
        this.workDetailRepository = workDetailRepository;
        this.pricingRepository = pricingRepository;
        this.employeeRepository = employeeRepository;
        this.employerRepository = employerRepository;
        this.agentRepository = agentRepository;
    }

    @Transactional
    public Work createWork(CreateWorkPayload payload) {
        // 1. Validate employees exist
        for (String employeeId : payload.getEmployeeIds()) {
            if (!employeeRepository.existsByPassportNumber(employeeId)) {
                throw new ResourceNotFoundException("Employee not found with passport number: " + employeeId);
            }
        }

        // 2. Validate employer exists
        if (!employerRepository.existsById(payload.getEmployerId())) {
            throw new ResourceNotFoundException("Employer not found with ID: " + payload.getEmployerId());
        }

        // 3. Get pricing for this work type
        Pricing pricing = pricingRepository.findByWorkType(payload.getWorkType())
                .orElseThrow(() -> new RuntimeException("Pricing not found for work type: " + payload.getWorkType()));

        // 4. Calculate total price
        int employeeCount = payload.getEmployeeIds().size();
        List<String> steps = WorkStepConfig.getStepsByWorkType(payload.getWorkType());
        int totalSteps = steps.size();

        BigDecimal totalWorkPrice = pricing.getPricePerEmployee().multiply(BigDecimal.valueOf(employeeCount));
        BigDecimal pricePerBill = totalWorkPrice.divide(BigDecimal.valueOf(totalSteps), 2, RoundingMode.HALF_UP);

        // 5. Validate current step index (1-based)
        int currentStepIndex = payload.getCurrentStepIndex();
        if (currentStepIndex < 1 || currentStepIndex > totalSteps) {
            throw new IllegalArgumentException("Invalid step index: " + currentStepIndex +
                    ". Must be between 1 and " + totalSteps);
        }

        // Get step name from index (convert to 0-based for list access)
        String currentStepName = steps.get(currentStepIndex - 1);

        // 6. Create Work
        Work work = Work.builder()
                .id(UUID.randomUUID().toString())
                .currentStepIndex(currentStepIndex)
                .currentStepName(currentStepName)
                .workType(payload.getWorkType())
                .detail(payload.getDetail())
                .status("NOT_FINISHED")
                .totalPrice(totalWorkPrice)
                .employerId(payload.getEmployerId())
                .underRespAgent(payload.getAgentId())
                .build();

        Work savedWork = workRepository.save(work);

        // 7. Create Work Details (junction table)
        List<WorkDetail> workDetails = payload.getEmployeeIds().stream()
                .map(empId -> WorkDetail.builder()
                        .workId(savedWork.getId())
                        .employeeId(empId)
                        .build())
                .collect(Collectors.toList());

        workDetailRepository.batchInsert(workDetails);

        // 8. Create Bill for current step
        String billId = billRepository.generateBillId(LocalDateTime.now().getYear());
        Bill currentBill = Bill.builder()
                .id(billId)
                .stepIndex(currentStepIndex)
                .stepName(currentStepName)
                .price(pricePerBill)
                .status("NOT_PAID")
                .createdAt(LocalDateTime.now())
                .paidAt(null)
                .workId(savedWork.getId())
                .build();

        billRepository.save(currentBill);

        return savedWork;
    }

    public WorkDetailResponse getWorkDetailById(String workId) {
        // 1. Find work
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new ResourceNotFoundException("Work not found with ID: " + workId));

        // 2. Find employer
        Employer employer = employerRepository.findById(work.getEmployerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found with ID: " + work.getEmployerId()));

        // 3. Find employees in this work
        List<String> employeeIds = workDetailRepository.findEmployeeIdsByWorkId(workId);
        List<WorkDetailResponse.EmployeeInWork> employees = employeeIds.stream()
                .map(empId -> {
                    Employee emp = employeeRepository.findById(empId)
                            .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + empId));
                    return WorkDetailResponse.EmployeeInWork.builder()
                            .passportNumber(emp.getPassportNumber())
                            .fullName(emp.getFirstname() + " " + emp.getLastname())
                            .nationality(emp.getNationality())
                            .build();
                })
                .collect(Collectors.toList());

        // 4. Find agent
        WorkDetailResponse.AgentSummary agentSummary = null;
        if (work.getUnderRespAgent() != null) {
            agentSummary = agentRepository.findById(work.getUnderRespAgent())
                    .map(agent -> WorkDetailResponse.AgentSummary.builder()
                            .id(agent.getId())
                            .fullName(agent.getFirstname() + " " + agent.getLastname())
                            .email(agent.getEmail())
                            .build())
                    .orElse(null);
        }

        return WorkDetailResponse.builder()
                .id(work.getId())
                .currentStep(work.getCurrentStepName())
                .currentStepIndex(work.getCurrentStepIndex())
                .workType(work.getWorkType())
                .detail(work.getDetail())
                .status(work.getStatus())
                .totalPrice(work.getTotalPrice())
                .employer(WorkDetailResponse.EmployerSummary.builder()
                        .id(employer.getId())
                        .fullName(employer.getFirstname() + " " + employer.getLastname())
                        .companyName(employer.getCompanyName())
                        .phoneNumber(employer.getPhoneNumber())
                        .build())
                .employeesInWork(employees)
                .agent(agentSummary)
                .build();
    }

    @Transactional
    public Work advanceToNextStep(String workId) {
        // 1. Find work
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new ResourceNotFoundException("Work not found with ID: " + workId));

        // 2. Check if work is already finished
        if ("FINISHED".equals(work.getStatus())) {
            throw new IllegalStateException("Work is already finished");
        }

        // 3. Get steps for this work type
        List<String> steps = WorkStepConfig.getStepsByWorkType(work.getWorkType());
        int totalSteps = steps.size();

        // 4. Check if current step is the last step
        if (work.getCurrentStepIndex() >= totalSteps) {
            throw new IllegalStateException("Work is already at the last step");
        }

        // 5. Advance to next step (1-based)
        int nextStepIndex = work.getCurrentStepIndex() + 1;
        String nextStepName = steps.get(nextStepIndex - 1); // Convert to 0-based for list access

        // 6. Calculate price per bill
        BigDecimal pricePerBill = work.getTotalPrice().divide(BigDecimal.valueOf(totalSteps), 2, RoundingMode.HALF_UP);

        // 7. Create new bill for next step
        String billId = billRepository.generateBillId(LocalDateTime.now().getYear());
        Bill nextBill = Bill.builder()
                .id(billId)
                .stepIndex(nextStepIndex)
                .stepName(nextStepName)
                .price(pricePerBill)
                .status("NOT_PAID")
                .createdAt(LocalDateTime.now())
                .paidAt(null)
                .workId(workId)
                .build();

        billRepository.save(nextBill);

        // 8. Update work to next step
        workRepository.updateStep(workId, nextStepIndex, nextStepName);

        // Return updated work
        return workRepository.findById(workId).orElseThrow();
    }

    public Work getWorkById(String workId) {
        return workRepository.findById(workId)
                .orElseThrow(() -> new ResourceNotFoundException("Work not found with ID: " + workId));
    }

    public boolean isLastStep(String workId) {
        Work work = getWorkById(workId);
        List<String> steps = WorkStepConfig.getStepsByWorkType(work.getWorkType());
        return work.getCurrentStepIndex() == steps.size();
    }

    @Transactional
    public Work markWorkAsFinished(String workId) {
        Work work = getWorkById(workId);

        if ("FINISHED".equals(work.getStatus())) {
            throw new IllegalStateException("Work is already finished");
        }

        workRepository.updateStatus(workId, "FINISHED");
        return workRepository.findById(workId).orElseThrow();
    }
}
