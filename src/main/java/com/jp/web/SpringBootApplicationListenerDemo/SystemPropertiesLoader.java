package com.jp.web.SpringBootApplicationListenerDemo;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

public class SystemPropertiesLoader implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent) {
        // Get the spring Environment from event
        ConfigurableEnvironment environment = applicationEnvironmentPreparedEvent.getEnvironment();

        // read the desired property from configurable environment and then set that as System property
        String desiredPropertyValue = environment.getProperty("desiredPropertyName");
        System.setProperty("desiredPropertyName", desiredPropertyValue);
    }
}
