package org.example.springavanzadoalvaror.ui.pantallas;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.example.springavanzadoalvaror.common.Constantes;
import org.example.springavanzadoalvaror.data.modelo.Asymmetric;
import org.example.springavanzadoalvaror.data.modelo.Symmetric;
import org.example.springavanzadoalvaror.data.modelo.User;
import org.example.springavanzadoalvaror.servicios.UserService;
import org.example.springavanzadoalvaror.ui.pantallas.common.BaseScreenController;
import org.springframework.stereotype.Component;

@Component
public class GestionRecursosController extends BaseScreenController {
    private final UserService userService;
    @FXML
    private TextField newPassword;
    @FXML
    private TableView<Symmetric> recursosTable;
    @FXML
    private TableColumn<String, Symmetric> recursoColumn;
    @FXML
    private TableColumn<String, Symmetric> recursoPasswordColumn;
    @FXML
    private TableView<Asymmetric> compartidosTable;
    @FXML
    private TableColumn<String, Asymmetric> compartidosColumn;
    @FXML
    private ComboBox<String> compartirRecurso;
    @FXML
    private TextField nombreRecurso;
    @FXML
    private TextField passwordRecurso;
    private User actualUser;
    private Symmetric selectedRecurso;


    public GestionRecursosController(UserService userService) {
        this.userService = userService;
    }

    public void initialize() {
        recursoColumn.setCellValueFactory(new PropertyValueFactory<>(Constantes.PROGRAM_NAME));
        recursoPasswordColumn.setCellValueFactory(new PropertyValueFactory<>(Constantes.PASSWORD));
        compartidosColumn.setCellValueFactory(new PropertyValueFactory<>(Constantes.USERNAME));
        recursosTable.setOnMouseClicked(this::handleTableClick);
        compartirRecurso.setVisible(false);
    }

    @Override
    public void principalCargado() {
        actualUser = getPrincipalController().getActualUser();
        setRecursosTable();
    }

    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Symmetric newSelectedRecurso = recursosTable.getSelectionModel().getSelectedItem();
            if (newSelectedRecurso != null) {
                this.selectedRecurso = newSelectedRecurso;
                compartirRecurso.getItems().addAll(userService.getPossibleCompartidos(selectedRecurso));
                compartirRecurso.setVisible(true);
            }
        }
        setCompartidosTable();
    }

    private void setRecursosTable() {
        recursosTable.getItems().clear();
        recursosTable.getItems().addAll(userService.getRecursos(actualUser.getName()));
    }

    private void setCompartidosTable() {
        compartidosTable.getItems().clear();
        compartidosTable.getItems().addAll(userService.getCompartidos(selectedRecurso.getId()));
    }

    @FXML
    public void crearRecurso() {
        if (nombreRecurso.getText().isEmpty() || passwordRecurso.getText().isEmpty()) {
            getPrincipalController().showErrorAlert(Constantes.RELLENA_TODOS_LOS_CAMPOS);
        } else {
            userService.crearRecurso(actualUser.getName(), nombreRecurso.getText(), passwordRecurso.getText()).peek(symmetric ->
                            getPrincipalController().showConfirmationAlert(Constantes.RECURSO + symmetric.getProgramName() + Constantes.CREADO_CORRECTAMENTE))
                    .peekLeft(errorApp ->
                            getPrincipalController().showErrorAlert(errorApp.getErrorMessage()));
            setRecursosTable();
        }
    }


    @FXML
    public void compartir() {
        if (selectedRecurso == null) {
            getPrincipalController().showErrorAlert(Constantes.SELECCIONA_UN_RECURSO);
        } else if (compartirRecurso.getValue() == null) {
            getPrincipalController().showErrorAlert(Constantes.SELECCIONA_UN_USUARIO);
        } else {
            userService.compartirRecurso(actualUser.getName(), compartirRecurso.getValue(), selectedRecurso).peek(success -> {
                        if (Boolean.TRUE.equals(success)) {
                            getPrincipalController().showConfirmationAlert(Constantes.RECURSO_COMPARTIDO_CORRECTAMENTE);
                        }
                    })
                    .peekLeft(errorApp -> getPrincipalController().showErrorAlert(errorApp.getErrorMessage()));
            setRecursosTable();
            setCompartidosTable();
        }
    }

    @FXML
    public void changePassword() {
        if (selectedRecurso == null) {
            getPrincipalController().showErrorAlert(Constantes.SELECCIONA_UN_RECURSO);

        } else if (newPassword.getText().isEmpty()) {
            getPrincipalController().showErrorAlert(Constantes.RELLENA_TODOS_LOS_CAMPOS);
        } else {
            if (userService.changePassword(actualUser.getName(), passwordRecurso.getText(), selectedRecurso)) {
                getPrincipalController().showConfirmationAlert(Constantes.PASSWORD_CAMBIADA_CORRECTAMENTE);
                setRecursosTable();
            } else {
                getPrincipalController().showErrorAlert(Constantes.ERROR_AL_CAMBIAR_LA_PASSWORD);
            }
        }
    }

    @FXML
    public void checkFirma() {
        if (selectedRecurso == null) {
            getPrincipalController().showErrorAlert(Constantes.SELECCIONA_UN_RECURSO);
        } else {
            userService.checkFirma(actualUser.getName(), selectedRecurso).peek(success -> {
                        if (Boolean.TRUE.equals(success)) {
                            getPrincipalController().showConfirmationAlert(Constantes.FIRMA_CORRECTA);
                        }
                        else{
                            getPrincipalController().showErrorAlert(Constantes.FIRMA_INCORRECTA);
                        }
                    })
                    .peekLeft(errorApp -> getPrincipalController().showErrorAlert(errorApp.getErrorMessage()));
            setRecursosTable();
        }
    }
}
