package devPilot.backend.services.indexing;

import java.time.Instant;
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import devPilot.backend.entity.IndexStatus;
import devPilot.backend.entity.Repository;
import devPilot.backend.repository.RepositoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Indexing runs in-memory, so a restart kills any job in progress and leaves its repo stuck
 * in INDEXING (which also blocks re-indexing). On startup, mark those repos FAILED so users can retry.
 *
 * <p>Assumes a single backend instance: with several, this would also fail jobs running elsewhere.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IndexingRecovery {

    private static final String INTERRUPTED_MESSAGE =
            "Indexing was interrupted by a server restart. Please re-index this repository.";

    private final RepositoryRepository repositoryRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void failInterruptedIndexing() {
        List<Repository> stuck = repositoryRepository.findByIndexStatus(IndexStatus.INDEXING);
        if (stuck.isEmpty()) {
            return;
        }
        Instant now = Instant.now();
        for (Repository repo : stuck) {
            repo.setIndexStatus(IndexStatus.FAILED);
            repo.setErrorMessage(INTERRUPTED_MESSAGE);
            repo.setUpdatedAt(now);
        }
        repositoryRepository.saveAll(stuck);
        log.warn("Marked {} interrupted indexing job(s) as FAILED", stuck.size());
    }
}
