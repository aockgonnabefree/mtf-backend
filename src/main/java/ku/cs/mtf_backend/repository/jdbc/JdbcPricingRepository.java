package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.Pricing;
import ku.cs.mtf_backend.repository.PricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JdbcPricingRepository implements PricingRepository {

    private JdbcClient jdbcClient;

    @Autowired
    public JdbcPricingRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Pricing> findByWorkType(String workType) {
        String sql = "SELECT * FROM PRICING WHERE Work_type = CAST(:workType AS work_type)";
        return jdbcClient.sql(sql)
                .param("workType", workType)
                .query(Pricing.class)
                .optional();
    }
}
