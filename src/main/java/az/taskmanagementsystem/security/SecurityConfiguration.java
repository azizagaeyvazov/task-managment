package az.taskmanagementsystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JWTAuthenticationFilter jwtAuthFilter;

    private final AuthenticationProvider authenticationProvider;

    private static final String[] WHITE_LIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/v1/auth/**",
            "/api/v1/tasks/hello"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers(WHITE_LIST).permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/tasks").hasAuthority("MANAGER")
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/tasks").hasAuthority("MANAGER")
                                .requestMatchers(HttpMethod.POST, "/api/v1/tasks/assign-task").hasAuthority("MANAGER")
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/tasks/{taskId}/status").hasAuthority("EMPLOYEE")
                                .requestMatchers(WHITE_LIST).permitAll()
                                .anyRequest().hasAuthority("ADMIN"))
                .sessionManagement((sessionManagement) ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
