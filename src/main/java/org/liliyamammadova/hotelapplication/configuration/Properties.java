package org.liliyamammadova.hotelapplication.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "reservation")
@Configuration("properties")
public class Properties {
    private boolean statusChangeAvailability;

    public boolean isStatusChangeAvailability() {
        return statusChangeAvailability;
    }

    public void setStatusChangeAvailability(boolean statusChangeAvailability) {
        this.statusChangeAvailability = statusChangeAvailability;
    }
}
