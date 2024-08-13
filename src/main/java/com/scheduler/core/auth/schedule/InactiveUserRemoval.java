package com.scheduler.core.auth.schedule;

import com.scheduler.core.auth.model.User;
import com.scheduler.core.auth.repository.UserRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class InactiveUserRemoval {

    @Inject
    UserRepository userRepository;

    @Transactional
    @Scheduled(timeZone = "America/Sao_Paulo", cron = "0 0 0 * * ?")
    public void removeInactiveUsers() {

        final var listInactiveUsers = userRepository.getInactiveUsersWithoutEmailConfirmation();

        for (User user : listInactiveUsers) {
            user.delete();
        }
    }
}
