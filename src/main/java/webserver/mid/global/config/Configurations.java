package webserver.mid.global.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import webserver.mid.global.filter.LoginFilter;

import java.util.Collections;

@Configuration
public class Configurations implements WebMvcConfigurer {
    @Bean
    public ServletContextInitializer clearJsession() {
        return new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
                SessionCookieConfig sessionCookieConfig=servletContext.getSessionCookieConfig();
                sessionCookieConfig.setHttpOnly(true);
            }
        };
    }
}
