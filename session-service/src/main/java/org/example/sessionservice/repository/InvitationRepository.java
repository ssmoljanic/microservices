package org.example.sessionservice.repository;

import org.example.sessionservice.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    Optional<Invitation> findByToken(String token);
    boolean existsBySessionIdAndInvitedUserIdAndUsedFalse(Long sessionId, Long invitedUserId);
}
