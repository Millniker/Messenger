package com.common.security.props;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Configuration;

/**
 * Настройки security для интеграционного взаимодействия
 */
@Getter
@Setter
@ToString
@Configuration
public class SecurityIntegrationsProps {

    private String apiKey;

    private String rootPath;

}
