package at.wreiner.repository;

import at.wreiner.entity.GenerationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenerationRequestRepository extends JpaRepository<GenerationRequest, Long> { }
