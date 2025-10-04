package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.dto.projection.EmployerSummary;
import ku.cs.mtf_backend.entity.Employer;
import ku.cs.mtf_backend.repository.EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcEmployerRepository implements EmployerRepository {

    private JdbcClient jdbcClient;

    @Autowired
    public JdbcEmployerRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM EMPLOYER WHERE Id = :id";
        Integer count = jdbcClient.sql(sql).param("id", id).query(Integer.class).single();
        return count > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM EMPLOYER WHERE Email = :email";
        Integer count = jdbcClient.sql(sql).param("email", email).query(Integer.class).single();
        return count > 0;
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        String sql = "SELECT COUNT(*) FROM EMPLOYER WHERE Phone_number = :phoneNumber";
        Integer count = jdbcClient.sql(sql).param("phoneNumber", phoneNumber).query(Integer.class).single();
        return count > 0;
    }

    @Override
    public Employer save(Employer employer) {
        String sql = """
            INSERT INTO EMPLOYER (Id, Firstname, Lastname, Email, Phone_number, Business_type,
                                  Financial_status_year, Financial_status_income, Financial_status_tax,
                                  Current_income, Income_duration, Status, Company_name, Address_id)
            VALUES (:id, :firstname, :lastname, :email, :phoneNumber, :businessType,
                    :fsYear, :fsIncome, :fsTax, :currentIncome, :incomeDuration,
                    CAST(:status AS active_status_type), :companyName, :addressId)
            """;

        jdbcClient.sql(sql)
                .param("id", employer.getId())
                .param("firstname", employer.getFirstname())
                .param("lastname", employer.getLastname())
                .param("email", employer.getEmail())
                .param("phoneNumber", employer.getPhoneNumber())
                .param("businessType", employer.getBusinessType())
                .param("fsYear", employer.getFinancialStatusYear())
                .param("fsIncome", employer.getFinancialStatusIncome())
                .param("fsTax", employer.getFinancialStatusTax())
                .param("currentIncome", employer.getCurrentIncome())
                .param("incomeDuration", employer.getIncomeDuration())
                .param("status", employer.getStatus())
                .param("companyName", employer.getCompanyName())
                .param("addressId", employer.getAddressId())
                .update();
        return employer;
    }

    @Override
    public Optional<Employer> findById(String id) {
        String sql = "SELECT * FROM EMPLOYER WHERE Id = :id";
        return jdbcClient.sql(sql).param("id", id).query(Employer.class).optional();
    }

    @Override
    public Optional<Employer> findByEmail(String email) {
        String sql = "SELECT * FROM EMPLOYER WHERE Email = :email";
        return jdbcClient.sql(sql).param("email", email).query(Employer.class).optional();
    }

    @Override
    public Optional<Employer> findByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM EMPLOYER WHERE Phone_number = :phoneNumber";
        return jdbcClient.sql(sql).param("phoneNumber", phoneNumber).query(Employer.class).optional();
    }

    @Override
    public Employer update(Employer employer) {
        String sql = """
            UPDATE EMPLOYER SET
                Firstname = :firstname,
                Lastname = :lastname,
                Email = :email,
                Phone_number = :phoneNumber,
                Business_type = :businessType,
                Financial_status_year = :fsYear,
                Financial_status_income = :fsIncome,
                Financial_status_tax = :fsTax,
                Current_income = :currentIncome,
                Income_duration = :incomeDuration,
                Status = CAST(:status AS active_status_type),
                Company_name = :companyName,
                Address_id = :addressId
            WHERE Id = :id
            """;

        jdbcClient.sql(sql)
                .param("firstname", employer.getFirstname())
                .param("lastname", employer.getLastname())
                .param("email", employer.getEmail())
                .param("phoneNumber", employer.getPhoneNumber())
                .param("businessType", employer.getBusinessType())
                .param("fsYear", employer.getFinancialStatusYear())
                .param("fsIncome", employer.getFinancialStatusIncome())
                .param("fsTax", employer.getFinancialStatusTax())
                .param("currentIncome", employer.getCurrentIncome())
                .param("incomeDuration", employer.getIncomeDuration())
                .param("status", employer.getStatus())
                .param("companyName", employer.getCompanyName())
                .param("addressId", employer.getAddressId())
                .param("id", employer.getId())
                .update();
        return employer;
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM EMPLOYER";
        Long count = jdbcClient.sql(sql).query(Long.class).single();
        return count != null ? count : 0L;
    }

    @Override
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM EMPLOYER WHERE Status = CAST(:status AS active_status_type)";
        Long count = jdbcClient.sql(sql).param("status", status).query(Long.class).single();
        return count != null ? count : 0L;
    }

    @Override
    public List<EmployerSummary> findAllWithPagination(Integer page, Integer size, String nameFilter, String statusFilter) {
        String sql = """
            SELECT
                e.Id,
                e.Firstname || ' ' || e.Lastname AS fullName,
                e.Phone_number AS phoneNumber,
                e.Email,
                CAST(e.Status AS TEXT) AS status,
                COALESCE(COUNT(emp.Employee_id), 0) AS activeEmployeeCount
            FROM EMPLOYER e
            LEFT JOIN EMPLOYMENT emp
                ON e.Id = emp.Employer_id
                AND emp.Status = CAST('ACTIVE' AS active_status_type)
            WHERE
                (CAST(:nameFilter AS TEXT) IS NULL OR
                 LOWER(e.Firstname || ' ' || e.Lastname) LIKE LOWER('%' || CAST(:nameFilter AS TEXT) || '%'))
                AND (CAST(:statusFilter AS TEXT) IS NULL OR e.Status = CAST(:statusFilter AS active_status_type))
            GROUP BY e.Id, e.Firstname, e.Lastname, e.Phone_number, e.Email, e.Status
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
                .query(EmployerSummary.class)
                .list();
    }
}
