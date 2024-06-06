package org.erenes;

import javax.ws.rs.client.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class Endpoint {

    private String path;
    private String method;
    private Supplier<Map<String, Object>> templates;
    private Supplier<Entity> entity;

    private Endpoint(final String path,
                     final String method,
                     final Supplier<Map<String, Object>> templates,
                     final Supplier<Entity> entity) {
        this.path = path;
        this.method = method;
        this.templates = templates;
        this.entity = entity;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, Object> getTemplates() {
        return templates.get();
    }

    public Entity getEntity() {
        return Optional.ofNullable(entity).map(Supplier::get).orElse(null);
    }

    public static Endpoint endpoint(final String endpoint, final String method) {
        return new Endpoint(endpoint, method, HashMap::new, null);
    }

    public static Endpoint endpointWithTemplates(final String endpoint, final String method,
                                                 final Supplier<Map<String, Object>> templates) {
        return new Endpoint(endpoint, method, templates, null);
    }

    public static Endpoint endpointWithEntity(final String endpoint, final String method,
                                              final Supplier<Entity> entity) {
        return new Endpoint(endpoint, method, HashMap::new, entity);
    }
}
