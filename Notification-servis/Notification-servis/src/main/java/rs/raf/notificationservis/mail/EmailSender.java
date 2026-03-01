package rs.raf.notificationservis.mail;

public interface EmailSender {
    void send(String to, String subject, String body);
}
