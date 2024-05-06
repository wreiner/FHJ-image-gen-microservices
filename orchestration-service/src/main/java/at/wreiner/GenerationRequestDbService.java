package at.wreiner;

import at.wreiner.entity.GenerationRequest;
import at.wreiner.entity.GenerationRequestStatus;
import at.wreiner.repository.GenerationRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GenerationRequestDbService {

    private static final Logger log = LoggerFactory.getLogger(GenerationRequestDbService.class);
    @Autowired
    private GenerationRequestRepository repository;

    @Transactional
    public void updateRequestStatus(UUID uuid, GenerationRequestStatus status) {
        GenerationRequest request = repository.findByUuidLocked(uuid);
        if (request == null) {
            log.warn("Request with UUID {} not found", uuid);
            return;
        }

        request.setStatus(status);
        repository.save(request);
    }
}


