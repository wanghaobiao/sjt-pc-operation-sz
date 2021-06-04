package com.acrabsoft.web.service.sjt.pc.operation.web.util;

import com.acrabsoft.web.pojo.user.BasicUser;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * @author Administrator
 */
@Configuration
@EnableJpaAuditing
public class UserAuditing extends BaseController implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        BasicUser basicUser = getBaseUser();
        if (basicUser != null){
            String userId = basicUser.getUserid();
            return Optional.of(userId);
        } else {
            return Optional.empty();
        }
    }
}
