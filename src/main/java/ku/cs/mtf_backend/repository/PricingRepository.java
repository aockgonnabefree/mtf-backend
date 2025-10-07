package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.Pricing;

import java.util.Optional;

public interface PricingRepository {
    Optional<Pricing> findByWorkType(String workType);
}
