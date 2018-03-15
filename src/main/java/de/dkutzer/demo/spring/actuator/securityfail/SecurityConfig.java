package de.dkutzer.demo.spring.actuator.securityfail;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .anonymous().disable()//Do not allow anonymous access.
            .authorizeRequests()
            .requestMatchers(EndpointRequest.to(InfoEndpoint.class, HealthEndpoint.class)).permitAll()//Everybody and his grandma should be allowed -- not anonymous
            .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")//special actuator user for all other endpoints
            .and()
            .formLogin().disable()
            .httpBasic()
            .and()
            .csrf().disable()
            .cors().disable();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        final User admin = new User("admin", "{noop}admin",
            Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_ACTUATOR")));
        auth
            .userDetailsService(
                new InMemoryUserDetailsManager(
                    Arrays.asList(
                        admin
                    )));

    }

}
