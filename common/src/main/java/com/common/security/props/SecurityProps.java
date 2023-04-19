package com.common.security.props;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

/**
 * Java-модель конфига по пути app.security в application.yml
 */
@ConfigurationProperties("app.security")
@Getter
@Setter
@ToString
@Configuration
@ConstructorBinding
public class SecurityProps {

    private SecurityJwtTokenProps jwtToken;

    private SecurityIntegrationsProps integrations;

}
