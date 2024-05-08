package erenes.fight;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import erenes.fight.client.DefaultTestHero;
import erenes.fight.client.DefaultTestVillain;
import erenes.org.fight.Fighters;
import erenes.org.fight.client.Hero;
import erenes.org.fight.client.Villain;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(
    providerName = "rest-heroes",
    pactVersion = PactSpecVersion.V4,
    hostInterface = "localhost",
    port = "8093"
)
class FightResourceConsumerTest {

    private static final String HERO_API_BASE_URI = "/api/heroes";
    private static final String HERO_RANDOM_URI = HERO_API_BASE_URI + "/random";

    @Pact(consumer = "rest-fights")
    public V4Pact randomHeroFoundPact(PactDslWithProvider builder){
        return builder
            .uponReceiving("A request for a random hero")
            .path(HERO_RANDOM_URI)
            .method(HttpMethod.GET)
            .headers(HttpHeaders.ACCEPT, APPLICATION_JSON)
            .willRespondWith()
            .status(OK.getStatusCode())
            .headers(Map.of(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON))
            .body(newJsonBody(body ->
                    body
                        .stringType("name", DefaultTestHero.DEFAULT_HERO_NAME)
                        .integerType("level", DefaultTestHero.DEFAULT_HERO_LEVEL)
                        .stringType("picture", DefaultTestHero.DEFAULT_HERO_PICTURE)
                ).build()
            )
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "randomHeroFoundPact")
    void randomHeroFound() {

        Fighters fighters = given()
            .when()
            .get("/api/fights/randomfighters")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(Fighters.class);

        Hero hero = fighters.hero;
        assertEquals(hero.name, DefaultTestHero.DEFAULT_HERO_NAME);
        assertEquals(hero.picture, DefaultTestHero.DEFAULT_HERO_PICTURE);
        assertEquals(hero.level, DefaultTestHero.DEFAULT_HERO_LEVEL);

        // We're really trying to test the fighter, so we want to make sure it still passes back
        // a villain
        Villain villain = fighters.villain;
        assertEquals(villain.name, DefaultTestVillain.DEFAULT_VILLAIN_NAME);
        assertEquals(villain.picture, DefaultTestVillain.DEFAULT_VILLAIN_PICTURE);
        assertEquals(villain.level, DefaultTestVillain.DEFAULT_VILLAIN_LEVEL);
    }

    @Pact(consumer = "rest-fights")
    public V4Pact randomHeroNotFoundPact(PactDslWithProvider builder) {
        return builder
            .given("No random hero found")
            .uponReceiving("A request for a random hero")
            .path(HERO_RANDOM_URI)
            .method(HttpMethod.GET)
            .headers(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .willRespondWith()
            .status(Response.Status.NOT_FOUND.getStatusCode())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "randomHeroNotFoundPact")
    void shouldGetRandomFighters() {
        Fighters fighters = given()
            .when()
            .get("/api/fights/randomfighters")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(Fighters.class);

        Hero hero = fighters.hero;

        assertEquals(hero.name, "Fallback hero");
        assertEquals(hero.picture,
            "https://dummyimage.com/240x320/1e8fff/ffffff&text=Fallback+Hero");
        assertEquals(hero.level, 1);
    }
}
