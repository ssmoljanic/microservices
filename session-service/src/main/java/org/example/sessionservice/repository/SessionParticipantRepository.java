package org.example.sessionservice.repository;

import org.example.sessionservice.domain.SessionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionParticipantRepository extends JpaRepository<SessionParticipant, Long> {

    Optional<SessionParticipant> findBySessionIdAndUserId(Long sessionId, Long userId);

    long countBySessionId(Long sessionId);

    List<SessionParticipant> findAllBySessionId(Long sessionId);

    List<SessionParticipant> findAllByUserId(Long userId);

    boolean existsBySessionIdAndUserId(Long sessionId, Long userId);

}
