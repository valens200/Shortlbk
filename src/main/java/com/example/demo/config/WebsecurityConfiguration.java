package com.example.demo.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebsecurityConfiguration extends WebSecurityConfigurerAdapter {
    public  void configure(HttpSecurity http)  throws Exception{
        http.csrf().disable();
        http.authorizeHttpRequests()
                .anyRequest().permitAll();
    }

}
