package rs.raf.notificationservis.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import rs.raf.notificationservis.dto.UserEmailResponse;

@Component
public class UserClient {

    private final RestTemplate restTemplate;

    @Value("${app.userservice.url}")
    private String userServiceUrl;

    public UserClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserEmailResponse getEmail(Long userId, String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserEmailResponse> resp = restTemplate.exchange(
                userServiceUrl + "/internal/users/" + userId + "/email",
                HttpMethod.GET,
                entity,
                UserEmailResponse.class
        );

        return resp.getBody();
    }
}
