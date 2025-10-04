package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.entity.Employment;
import ku.cs.mtf_backend.repository.EmploymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EmploymentService {
    private final EmploymentRepository employmentRepository;

    public EmploymentService(EmploymentRepository employmentRepository) {
        this.employmentRepository = employmentRepository;
    }

    public void createEmploymentRelationship(String employerId, String employeeId, String status) {
        Employment employment = Employment.builder()
                .employerId(employerId)
                .employeeId(employeeId)
                .status(status)
                .build();

        employmentRepository.save(employment);
    }
    @Transactional
    public void changeEmployerForEmployee(String employeeId, String newEmployerId) {
        // 1. ปิดการจ้างงานเก่า (Deactivate Old Employment)
        // ค้นหาการจ้างงานปัจจุบันที่ยัง active อยู่ของลูกจ้างคนนี้
        Optional<Employment> currentActiveEmploymentOpt = employmentRepository.findByEmployeeIdAndStatus(employeeId, "ACTIVE");

        currentActiveEmploymentOpt.ifPresent(employment -> {
            // ถ้าเจอ, และนายจ้างคนปัจจุบันไม่ใช่คนใหม่ที่เราจะย้ายไป
            if (!employment.getEmployerId().equals(newEmployerId)) {
                employmentRepository.updateStatus(employment.getEmployerId(), employeeId, "INACTIVE");
            }
        });

        // 2. จัดการการจ้างงานใหม่ (Handle New Employment)
        // ตรวจสอบว่าเคยมีความสัมพันธ์ระหว่างลูกจ้างกับนายจ้างคนใหม่นี้มาก่อนหรือไม่
        Optional<Employment> existingRelationshipWithNewEmployerOpt = employmentRepository.findByEmployerIdAndEmployeeId(newEmployerId, employeeId);

        if (existingRelationshipWithNewEmployerOpt.isPresent()) {
            // ถ้าเคยมี (เป็นเคสย้ายกลับมา) -> ให้อัปเดต status กลับมาเป็น active
            employmentRepository.updateStatus(newEmployerId, employeeId, "ACTIVE");
        } else {
            // ถ้าไม่เคยมี -> สร้างความสัมพันธ์ใหม่ขึ้นมาเลย
            createEmploymentRelationship(newEmployerId, employeeId, "ACTIVE");
        }
    }
}
