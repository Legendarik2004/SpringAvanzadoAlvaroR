package org.example.springavanzadoalvaror.ui.main;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.springavanzadoalvaror.SpringAvanzadoAlvaroRApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;


public class DIJavafx extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() throws Exception {
        super.init();
        applicationContext = new SpringApplicationBuilder(SpringAvanzadoAlvaroRApplication.class).run();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        applicationContext.close();
        Platform.exit();
    }

    @Override
    public void start(Stage primaryStage){
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setResizable(true);
        applicationContext.publishEvent(new StageReadyEvent(primaryStage));
    }

    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }
}
