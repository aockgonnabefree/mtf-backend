package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.WorkDetail;

import java.util.List;

public interface WorkDetailRepository {
    void save(WorkDetail workDetail);
    void batchInsert(List<WorkDetail> workDetails);
    List<WorkDetail> findAllByWorkId(String workId);
    List<String> findEmployeeIdsByWorkId(String workId);
}
