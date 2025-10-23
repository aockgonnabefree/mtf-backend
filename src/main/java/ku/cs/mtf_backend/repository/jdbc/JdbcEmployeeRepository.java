package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.dto.projection.EmployeeSummary;
import ku.cs.mtf_backend.dto.response.EmployeeSelectDTO;
import ku.cs.mtf_backend.entity.Employee;
import ku.cs.mtf_backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcEmployeeRepository implements EmployeeRepository {

    private JdbcClient jdbcClient;

    @Autowired
    public JdbcEmployeeRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public boolean existsByPassportNumber(String passportNumber) {
        String sql = "SELECT COUNT(*) FROM EMPLOYEE WHERE Passport_number = :passportNumber";
        Integer count = jdbcClient.sql(sql)
                .param("passportNumber", passportNumber)
                .query(Integer.class)
                .single();
        return count > 0;
    }

    @Override
    public Employee save(Employee employee) {
        String sql = """
            INSERT INTO EMPLOYEE (Passport_number, Firstname, Lastname, Nationality, Blood_type, Status, Address_id)
            VALUES (:passportNumber, :firstname, :lastname, CAST(:nationality AS nationality), CAST(:bloodType AS blood_type), CAST(:status AS active_status_type), :addressId)
            """;

        jdbcClient.sql(sql)
                .param("passportNumber", employee.getPassportNumber())
                .param("firstname", employee.getFirstname())
                .param("lastname", employee.getLastname())
                .param("nationality", employee.getNationality())
                .param("bloodType", employee.getBloodType())
                .param("status", employee.getStatus())
                .param("addressId", employee.getAddressId())
                .update();

        return employee;
    }

    @Override
    public boolean existsById(String passportNo) {
        String sql = "SELECT COUNT(*) FROM EMPLOYEE WHERE Passport_number = :passportNo";
        Integer count = jdbcClient.sql(sql).param("passportNo", passportNo).query(Integer.class).single();
        return count > 0;
    }

    @Override
    public Optional<Employee> findById(String passportNo) {
        String sql = "SELECT * FROM EMPLOYEE WHERE Passport_number = :passportNo";
        return jdbcClient.sql(sql)
                .param("passportNo", passportNo)
                .query(Employee.class)
                .optional();
    }

    @Override
    public Employee update(Employee employee) {
        String sql = """
            UPDATE EMPLOYEE SET
                Firstname = :firstname,
                Lastname = :lastname,
                Nationality = CAST(:nationality AS nationality),
                Blood_type = CAST(:bloodType AS blood_type),
                Status = CAST(:status AS active_status_type),
                Address_id = :addressId
            WHERE Passport_number = :passportNo
            """;

        jdbcClient.sql(sql)
                .param("firstname", employee.getFirstname())
                .param("lastname", employee.getLastname())
                .param("nationality", employee.getNationality())
                .param("bloodType", employee.getBloodType())
                .param("status", employee.getStatus())
                .param("addressId", employee.getAddressId())
                .param("passportNo", employee.getPassportNumber())
                .update();

        return employee;
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM EMPLOYEE";
        Long count = jdbcClient.sql(sql).query(Long.class).single();
        return count != null ? count : 0L;
    }

    @Override
    public long countWithExpiredDocuments() {
        String sql = """
            SELECT COUNT(DISTINCT e.Passport_number)
            FROM EMPLOYEE e
            INNER JOIN DOCUMENT d ON e.Passport_number = d.Employee_id
            WHERE d.Expiry_date < CURRENT_DATE
            """;
        Long count = jdbcClient.sql(sql).query(Long.class).single();
        return count != null ? count : 0L;
    }

    @Override
    public long countWithExpiringSoonDocuments(int daysThreshold) {
        String sql = """
            SELECT COUNT(DISTINCT e.Passport_number)
            FROM EMPLOYEE e
            INNER JOIN DOCUMENT d ON e.Passport_number = d.Employee_id
            WHERE d.Expiry_date >= CURRENT_DATE
              AND d.Expiry_date <= CURRENT_DATE + CAST(:daysThreshold AS INTEGER)
              AND e.Passport_number NOT IN (
                  SELECT DISTINCT Employee_id
                  FROM DOCUMENT
                  WHERE Expiry_date < CURRENT_DATE
              )
            """;
        Long count = jdbcClient.sql(sql)
                .param("daysThreshold", daysThreshold)
                .query(Long.class)
                .single();
        return count != null ? count : 0L;
    }

    @Override
    public List<EmployeeSummary> findAllWithPagination(Integer page, Integer size, String nameFilter, String statusFilter) {
        String sql = """
            SELECT
                e.Passport_number AS passportNumber,
                e.Firstname || ' ' || e.Lastname AS fullName,
                COALESCE(emp.Employer_id || ' - ' || empr.Firstname || ' ' || empr.Lastname, '-') AS currentEmployer,
                CAST(e.Status AS TEXT) AS status
            FROM EMPLOYEE e
            LEFT JOIN EMPLOYMENT emp
                ON e.Passport_number = emp.Employee_id
                AND emp.Status = CAST('ACTIVE' AS active_status_type)
            LEFT JOIN EMPLOYER empr
                ON emp.Employer_id = empr.Id
            WHERE
                (CAST(:nameFilter AS TEXT) IS NULL OR
                 LOWER(e.Firstname || ' ' || e.Lastname) LIKE LOWER('%' || CAST(:nameFilter AS TEXT) || '%'))
                AND (CAST(:statusFilter AS TEXT) IS NULL OR e.Status = CAST(:statusFilter AS active_status_type))
            ORDER BY
                CASE WHEN e.Status = CAST('ACTIVE' AS active_status_type) THEN 0 ELSE 1 END,
                e.Firstname || ' ' || e.Lastname ASC
            LIMIT :size OFFSET :offset
            """;

        int offset = page * size;

        return jdbcClient.sql(sql)
                .param("nameFilter", nameFilter)
                .param("statusFilter", statusFilter)
                .param("size", size)
                .param("offset", offset)
                .query(EmployeeSummary.class)
                .list();
    }

    @Override
    public List<EmployeeSelectDTO> findEmployeesByEmployerId(String employerId, String statusFilter, String nameFilter, Integer size, Integer offset) {
        String sql = """
            SELECT
                e.Passport_number AS id,
                e.Firstname || ' ' || e.Lastname AS fullName,
                CAST(e.Status AS TEXT) AS status
            FROM EMPLOYEE e
            INNER JOIN EMPLOYMENT emp ON e.Passport_number = emp.Employee_id
            WHERE emp.Employer_id = :employerId
                AND (CAST(:statusFilter AS TEXT) IS NULL OR e.Status = CAST(:statusFilter AS active_status_type))
                AND (CAST(:nameFilter AS TEXT) IS NULL OR
                     LOWER(e.Firstname || ' ' || e.Lastname) LIKE LOWER('%' || CAST(:nameFilter AS TEXT) || '%'))
            ORDER BY
                CASE WHEN e.Status = CAST('ACTIVE' AS active_status_type) THEN 0 ELSE 1 END,
                e.Firstname || ' ' || e.Lastname ASC
            LIMIT :size OFFSET :offset
            """;

        return jdbcClient.sql(sql)
                .param("employerId", employerId)
                .param("statusFilter", statusFilter)
                .param("nameFilter", nameFilter)
                .param("size", size)
                .param("offset", offset)
                .query(EmployeeSelectDTO.class)
                .list();
    }

    @Override
    public Long countEmployeesByEmployerId(String employerId, String statusFilter, String nameFilter) {
        String sql = """
            SELECT COUNT(e.Passport_number)
            FROM EMPLOYEE e
            INNER JOIN EMPLOYMENT emp ON e.Passport_number = emp.Employee_id
            WHERE emp.Employer_id = :employerId
                AND (CAST(:statusFilter AS TEXT) IS NULL OR e.Status = CAST(:statusFilter AS active_status_type))
                AND (CAST(:nameFilter AS TEXT) IS NULL OR
                     LOWER(e.Firstname || ' ' || e.Lastname) LIKE LOWER('%' || CAST(:nameFilter AS TEXT) || '%'))
            """;

        Long count = jdbcClient.sql(sql)
                .param("employerId", employerId)
                .param("statusFilter", statusFilter)
                .param("nameFilter", nameFilter)
                .query(Long.class)
                .single();
        return count != null ? count : 0L;
    }
}
