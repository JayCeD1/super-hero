package org.erenes;

import com.github.javafaker.Faker;
import org.jboss.logging.Logger;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

public abstract class ScenarioInvoker implements Runnable {

    private static Logger LOGGER = Logger.getLogger(ScenarioInvoker.class.getName());

    protected Faker faker = new Faker();

    private WeightedRandomResult<Endpoint> endpointsToExecute = new WeightedRandomResult<>(getEndpoints());

    protected abstract String getTargetUrl();

    protected abstract List<Endpoint> getEndpoints();

    @Override
    public void run() {
        while (true) {
            try {
                final Endpoint endpoint = endpointsToExecute.get();
                final WebTarget webTarget = ClientBuilder.newClient().target(getTargetUrl())
                    .path(endpoint.getPath())
                    .resolveTemplates(endpoint.getTemplates());
                final Response response = webTarget.request().method(endpoint.getMethod(), endpoint.getEntity());
                LOGGER.info(format("%s - %s - %d", endpoint.getMethod(), webTarget.getUri(), response.getStatus()));
                sleep();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
