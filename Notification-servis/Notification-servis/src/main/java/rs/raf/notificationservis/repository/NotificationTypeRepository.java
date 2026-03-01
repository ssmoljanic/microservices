package rs.raf.notificationservis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.raf.notificationservis.domain.NotificationType;

import java.util.Optional;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {
    Optional<NotificationType> findByCode(String code);
}

