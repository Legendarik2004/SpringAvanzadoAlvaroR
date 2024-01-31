package org.example.springavanzadoalvaror;

import javafx.application.Application;
import org.example.springavanzadoalvaror.ui.main.DIJavafx;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAvanzadoAlvaroRApplication {

    public static void main(String[] args) {

        Application.launch(DIJavafx.class, args);
    }

}
