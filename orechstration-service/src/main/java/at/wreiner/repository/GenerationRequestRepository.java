package at.wreiner.repository;

import at.wreiner.entity.GenerationRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface GenerationRequestRepository extends JpaRepository<GenerationRequest, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM GenerationRequest g WHERE g.uuid = :uuid")
    GenerationRequest findByUuidLocked(UUID uuid);
}
