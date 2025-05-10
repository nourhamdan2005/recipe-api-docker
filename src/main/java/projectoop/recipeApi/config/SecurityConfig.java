package projectoop.recipeApi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import projectoop.recipeApi.util.JwtUtil;

import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll() // Allow login and register
                .requestMatchers(HttpMethod.GET, "/recipes/**").permitAll() // Anyone can view recipes
                    .requestMatchers(
    "/swagger-ui/**",
    "/v3/api-docs/**",
    "/swagger-ui.html"
).permitAll()
                .anyRequest().authenticated() // All other requests need JWT
            )
            .addFilterBefore(new JwtAuthenticationFilter(), BasicAuthenticationFilter.class); // ✅ Register filter
        return http.build();
    }

    // ✅ JWT Authentication Filter with ROLE support
    public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
        public JwtAuthenticationFilter() {
            super(authentication -> null); // dummy AuthenticationManager
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain chain) throws IOException, ServletException {
            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                try {
                    String username = JwtUtil.extractUsername(token);
                    String role = JwtUtil.extractRole(token); // ✅ Extract role from JWT

                    if (username != null && role != null) {
                        // ✅ Add role as authority so @PreAuthorize works
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception e) {
                    System.out.println("❌ Invalid token: " + e.getMessage());
                }
            }

            chain.doFilter(request, response);
        }
    }
}