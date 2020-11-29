package com._98point6.droptoken;

import com._98point6.droptoken.model.GameStatusResponse;
import com._98point6.droptoken.serializer.GameStatusResponseSerializer;
import com._98point6.droptoken.service.DropTokenService;
import com._98point6.droptoken.service.DropTokenServiceImpl;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.dropwizard.Application;
import io.dropwizard.jersey.errors.EarlyEofExceptionMapper;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.jersey.validation.JerseyViolationExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 *
 */
public class DropTokenApplication extends Application<DropTokenConfiguration> {
        public static void main(String[] args) throws Exception {
            new DropTokenApplication().run(args);
        }

        @Override
        public String getName() {
            return "98Point6 - Drop Token";
        }

        @Override
        public void initialize(Bootstrap<DropTokenConfiguration> bootstrap) {
        }

        @Override
        public void run(DropTokenConfiguration configuration,
                Environment environment) {

            // set up jackson serialization
            setUpJackson(environment.getObjectMapper());

            environment.jersey().register(new DropTokenExceptionMapper());
            environment.jersey().register(new JerseyViolationExceptionMapper());
            environment.jersey().register(new JsonProcessingExceptionMapper());
            environment.jersey().register(new EarlyEofExceptionMapper());

            final DropTokenService dropTokenService = new DropTokenServiceImpl();
            final DropTokenResource resource = new DropTokenResource(dropTokenService);
            environment.jersey().register(resource);
        }

        // so that we can use the same setup in testing the resource
        public static ObjectMapper setUpJackson(ObjectMapper objectMapper) {
            final SimpleModule gameStatusResponseModule = new SimpleModule().
                    addSerializer(GameStatusResponse.class, new GameStatusResponseSerializer(GameStatusResponse.class));

            objectMapper
                    .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                    .registerModule(gameStatusResponseModule)
                    .registerModule(new Jdk8Module());
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            return objectMapper;
        }

}
