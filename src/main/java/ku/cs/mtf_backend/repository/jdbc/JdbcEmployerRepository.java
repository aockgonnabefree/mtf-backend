package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.Employer;
import ku.cs.mtf_backend.repository.EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

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
}
