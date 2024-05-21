package erenes.org.hero.health;

import erenes.org.hero.HeroResource;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
public class PingHeroResourceHealthCheck implements HealthCheck {

    @Inject
    HeroResource heroResource;

    /**
     * Invokes the health check procedure provided by the implementation of this interface.
     *
     * @return {@link HealthCheckResponse} object containing information about the health check result
     */
    @Override
    public HealthCheckResponse call() {
        String response = heroResource.hello();
        return HealthCheckResponse.named("Ping Hero REST Endpoint").withData("Response", response).up().build();
    }
}
