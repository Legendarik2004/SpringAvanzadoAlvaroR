package org.example.springavanzadoalvaror.ui.pantallas.principal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.example.springavanzadoalvaror.common.Constantes;
import org.example.springavanzadoalvaror.data.modelo.User;
import org.example.springavanzadoalvaror.ui.pantallas.common.BaseScreenController;
import org.example.springavanzadoalvaror.ui.pantallas.common.Screens;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Data
@Component
public class PrincipalController {

    private final ApplicationContext context;
    private User actualUser;
    private Alert alert;

    @FXML
    private BorderPane root;
    private Stage primaryStage;

    public PrincipalController(ApplicationContext context) {
        this.context = context;

    }


    public void initialize() {
        cargarPantalla(Screens.LOGIN);
        actualUser = null;
        alert = new Alert(Alert.AlertType.NONE);
    }

    public void setStage(Stage stage) {
        primaryStage = stage;
    }

    public void onLoginDone(User user) {
        actualUser = user;
        cargarPantalla(Screens.RECURSOS);
    }

    public void exit() {
        root.getScene().getWindow().fireEvent(new WindowEvent(root.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public void logout() {
        this.actualUser = null;
        cargarPantalla(Screens.LOGIN);
    }

    public void showErrorAlert(String mensaje) {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setHeaderText(Constantes.ERROR);
        alert.setContentText(mensaje);
        alert.getDialogPane().setId(Constantes.ALERT);
        alert.getDialogPane().lookupButton(ButtonType.OK).setId(Constantes.BTN_OK);
        alert.showAndWait();
    }

    public void showConfirmationAlert(String mensaje) {
        alert.setAlertType(Alert.AlertType.INFORMATION);
        alert.setHeaderText(Constantes.CONFIRMACION);
        alert.setContentText(mensaje);
        alert.getDialogPane().lookupButton(ButtonType.OK).setId(Constantes.BTN_OK);
        alert.showAndWait();
    }

    private Pane cargarPantalla(String ruta) {
        Pane panePantalla = null;
        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setControllerFactory(context::getBean);
            panePantalla = fxmlLoader.load(getClass().getResourceAsStream(ruta));
            root.setCenter(panePantalla);
            BaseScreenController baseScreenController = fxmlLoader.getController();
            baseScreenController.setPrincipalController(this);
            baseScreenController.principalCargado();

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return panePantalla;
    }
}
