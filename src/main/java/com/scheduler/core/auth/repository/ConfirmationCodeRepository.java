package com.scheduler.core.auth.repository;

import com.scheduler.core.auth.model.ConfirmationCode;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConfirmationCodeRepository implements PanacheRepository<ConfirmationCode> {

   public ConfirmationCode findConfirmationCodeByCodeAndUserEmail(String code, String email) {
       return find("code ?1 = code and user.email = ?2", code, email).firstResult();
   }
}
