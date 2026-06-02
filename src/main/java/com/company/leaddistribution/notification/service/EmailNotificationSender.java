package com.company.leaddistribution.notification.service;

import com.company.leaddistribution.notification.entity.Notification;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EmailNotificationSender {

    private static final Logger LOG = Logger.getLogger(EmailNotificationSender.class);

    public void send(Notification notification) {
        LOG.infof("Simulated email notification to %s: %s", notification.recipient, notification.payload);
    }
}
