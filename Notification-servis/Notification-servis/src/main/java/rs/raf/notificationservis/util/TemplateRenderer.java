package rs.raf.notificationservis.util;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TemplateRenderer {

    public String render(String template, Map<String, Object> vars) {
        if (template == null || vars == null) {
            return template;
        }

        String result = template;

        for (Map.Entry<String, Object> entry : vars.entrySet()) {
            String key = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() == null ? "" : entry.getValue().toString();
            result = result.replace(key, value);
        }

        return result;
    }
}
