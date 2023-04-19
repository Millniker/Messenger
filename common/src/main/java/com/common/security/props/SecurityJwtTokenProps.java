package com.common.security.props;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Свойства security для аутентификации JWT
 */
@Getter
@Setter
@ToString
@Configuration
@Component
public class SecurityJwtTokenProps {

    private String[] permitAll;

    private String secret;

    private Long expiration;

    private String rootPath;

}
