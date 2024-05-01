package at.wreiner.repository;

import at.wreiner.entity.GenerationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GenerationRequestRepository extends JpaRepository<GenerationRequest, Long> {
    Optional<GenerationRequest> findByUuid(UUID uuid);
}
