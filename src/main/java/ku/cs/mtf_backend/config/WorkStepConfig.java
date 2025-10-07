package ku.cs.mtf_backend.config;

import java.util.List;

public class WorkStepConfig {

    public static final List<String> NEW_REGISTRATION_STEPS = List.of(
            "รวบรวมเอกสารเพิ่มเติม",
            "ตรวจสอบโรคและซื้อประกันสุขภาพ",
            "ทำบัตรประจำตัวคนซึ่งไม่มีสัญชาติไทย (เล่มชมพู)",
            "ทำเอกสารรับรองบุคคลเข้าออกระหว่างประเทศ (เล่ม CI)"
    );

    public static final List<String> WORK_PERMIT_RENEWAL_STEPS = List.of(
            "รวบรวมเอกสารเพิ่มเติม",
            "ตรวจสอบโรคและซื้อประกันสุขภาพ",
            "ยื่น Calling Visa กับกรมแรงงาน",
            "ซื้อใบอนุญาตการทำงานกับกรมแรงงาน",
            "ตีซ่าตรวจคนเข้าเมือง"
    );

    public static List<String> getStepsByWorkType(String workType) {
        return switch (workType) {
            case "ขึ้นทะเบียนใหม่" -> NEW_REGISTRATION_STEPS;
            case "ต่ออายุใบอนุญาตทำงาน" -> WORK_PERMIT_RENEWAL_STEPS;
            default -> throw new IllegalArgumentException("Invalid work type: " + workType);
        };
    }

    public static int getTotalSteps(String workType) {
        return getStepsByWorkType(workType).size();
    }

    public static String getStepByIndex(String workType, int index) {
        List<String> steps = getStepsByWorkType(workType);
        if (index < 0 || index >= steps.size()) {
            throw new IllegalArgumentException("Invalid step index: " + index);
        }
        return steps.get(index);
    }
}
