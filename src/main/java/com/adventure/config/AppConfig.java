package com.adventure.config;

import java.util.Arrays;
import java.util.Collections;
import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class AppConfig implements WebMvcConfigurer {

	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }

	@Bean
	public SecurityFilterChain configuration(HttpSecurity http) throws Exception {
	    http.sessionManagement(se -> se.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .cors(cors -> {
	            cors.configurationSource(new org.springframework.web.cors.CorsConfigurationSource() {
	                @Override
	                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
	                    CorsConfiguration configuration = new CorsConfiguration();
	                    configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
	                    configuration.setAllowedMethods(Collections.singletonList("*"));
	                    configuration.setAllowCredentials(true);
	                    configuration.setAllowedHeaders(Collections.singletonList("*"));
	                    configuration.setExposedHeaders(Arrays.asList("Authorization"));
	                    return configuration;
	                }
	            });
	        })
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/adventureZone/admin/signIn", "/adventureZone/customer/signIn", "/adventureZone/addCustomer").permitAll()
	            .requestMatchers("/adventureZone/customers/{customerId}", "/adventureZone/tickets",
	            		"/adventureZone/ticket/{ticketId}", "/adventureZone/categoryList").hasRole("USER")
	            .requestMatchers("/adventureZone/customerList", "/adventureZone/customers/{customerId}", "/adventureZone/categories/**"
	            		, "/adventureZone/activities/**", "/adventureZone/admins/**", "/adventureZone/customers/**").hasRole("ADMIN")
	            .anyRequest().authenticated())
				.csrf(csrf -> csrf.disable())
				.addFilterAfter(new JwtTokenGeneratorFilter(), BasicAuthenticationFilter.class)
				.addFilterBefore(new JwtTokenValidatorFilter(), BasicAuthenticationFilter.class)
				.formLogin(Customizer.withDefaults())
				.httpBasic(Customizer.withDefaults());

	    return http.build();
	}

   @Bean
   public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }

  
}
