package rs.raf.notificationservis.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import rs.raf.notificationservis.domain.NotificationLog;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    Optional<NotificationLog> findByCorrelationId(String correlationId);

    Page<NotificationLog> findAllByRecipientUserId(Long recipientUserId, Pageable pageable);
}
