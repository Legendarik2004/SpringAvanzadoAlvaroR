package org.example.springavanzadoalvaror.ui.main;

import javafx.fxml.FXMLLoader;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.example.springavanzadoalvaror.common.Constantes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Properties;

@Data
@Log4j2
@org.springframework.context.annotation.Configuration
public class Configuration {

    private static Configuration instance = null;
    private Properties p;

    public Configuration() {
        try {
            p = new Properties();
            p.load(Configuration.class.getClassLoader().getResourceAsStream(Constantes.CONFIG_PROPERTIES));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public String getProperty(String key) {
        return p.getProperty(key);
    }

    @Bean
    public FXMLLoader createLoader(ApplicationContext context) {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);
        return loader;
    }

    @Bean
    public PasswordEncoder createPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
