package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.Employee;
import ku.cs.mtf_backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

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
}
