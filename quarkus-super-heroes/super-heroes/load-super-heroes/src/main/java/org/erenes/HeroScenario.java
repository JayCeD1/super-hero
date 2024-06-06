package org.erenes;

import com.github.javafaker.Superhero;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.client.Entity.json;
import static org.erenes.Endpoint.*;

public class HeroScenario extends ScenarioInvoker {

    private static final int NB_HEROES = 941;

    @Override
    protected String getTargetUrl() {
        return targetUrl;
    }

    // tag::adocScenario[]
    private static final String targetUrl = "http://localhost:8083";

    private static final String contextRoot = "/api/heroes";

    @Override
    protected List<Endpoint> getEndpoints() {
        return Stream.of(
            endpoint(contextRoot, "GET"),
            endpoint(contextRoot + "/hello", "GET"),
            endpoint(contextRoot + "/random", "GET"),
            endpointWithTemplates(contextRoot + "/{id}", "GET", this::idParam),
            endpointWithTemplates(contextRoot + "/{id}", "DELETE", this::idParam),
            endpointWithEntity(contextRoot, "POST", this::createHero)
        ).collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }
    // end::adocScenario[]

    private Entity createHero() {
        final Superhero hero = faker.superhero();
        JsonObject json;
        if (Math.random() * 100 < 95) {
            json = Json.createObjectBuilder()
                .add("name", hero.name())
                .add("otherName", faker.funnyName().name())
                .add("level", faker.number().numberBetween(1, 20))
                .add("picture", faker.internet().url())
                .add("powers", hero.power())
                .build();
        } else {
            json = Json.createObjectBuilder()
                .add("otherName", faker.funnyName().name())
                .add("level", 0)
                .add("picture", faker.internet().url())
                .add("powers", hero.power())
                .build();
        }
        return json(json.toString());
    }

    private Map<String, Object> idParam() {
        final HashMap<String, Object> templates = new HashMap<>();
        templates.put("id",  ThreadLocalRandom.current().nextInt(0, NB_HEROES + 1));
        return templates;
    }
}
