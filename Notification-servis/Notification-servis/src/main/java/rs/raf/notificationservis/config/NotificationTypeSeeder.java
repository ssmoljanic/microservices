package rs.raf.notificationservis.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.raf.notificationservis.domain.NotificationType;
import rs.raf.notificationservis.repository.NotificationTypeRepository;

@Component
public class NotificationTypeSeeder implements CommandLineRunner {

    private final NotificationTypeRepository notificationTypeRepository;

    public NotificationTypeSeeder(NotificationTypeRepository notificationTypeRepository) {
        this.notificationTypeRepository = notificationTypeRepository;
    }

    @Override
    public void run(String... args) {
        seed(
                "ACCOUNT_ACTIVATION",
                "Aktivacija naloga",
                "Zdravo {{firstName}},\n\nKlikni na link da aktiviraš nalog: {{activationLink}}\n\nPozdrav."
        );

        seed(
                "PASSWORD_RESET",
                "Reset lozinke",
                "Zdravo {{firstName}},\n\nKlikni na link da resetuješ lozinku: {{resetLink}}\n\nAko nisi tražio reset, ignoriši ovu poruku."
        );

        seed(
                "SESSION_INVITE",
                "Pozivnica za sesiju: {{sessionName}}",
                "Pozvan si u sesiju {{sessionName}}.\nPočetak: {{startTime}}\nLink: {{sessionLink}}"
        );

        seed(
                "SESSION_REGISTRATION_CONFIRMED",
                "Potvrda prijave za sesiju: {{sessionName}}",
                "Tvoja prijava za sesiju {{sessionName}} je potvrđena.\nPočetak: {{startTime}}"
        );

        seed(
                "SESSION_CANCELLED",
                "Otkazivanje sesije: {{sessionName}}",
                "Sesija {{sessionName}} je otkazana.\nRazlog: {{reason}}"
        );

        seed(
                "SESSION_REMINDER_60M",
                "Podsetnik: sesija {{sessionName}} počinje za 60 minuta",
                "Podsetnik: sesija {{sessionName}} počinje u {{startTime}}.\nLink: {{sessionLink}}"
        );

        seed(
                "SESSION_CREATION_REJECTED",
                "Odbijeno kreiranje sesije",
                "Kreiranje sesije je odbijeno jer je procenat prisustva manji od 90%.\nTvoj procenat: {{attendancePct}}%"
        );
    }

    private void seed(String code, String subjectTemplate, String bodyTemplate) {
        if (notificationTypeRepository.findByCode(code).isPresent()) return;

        NotificationType t = new NotificationType();
        t.setCode(code);
        t.setSubjectTemplate(subjectTemplate);
        t.setBodyTemplate(bodyTemplate);
        t.setActive(true);

        notificationTypeRepository.save(t);
    }
}
