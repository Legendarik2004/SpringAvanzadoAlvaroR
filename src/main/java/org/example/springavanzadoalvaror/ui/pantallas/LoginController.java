package org.example.springavanzadoalvaror.ui.pantallas;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.example.springavanzadoalvaror.common.Constantes;
import org.example.springavanzadoalvaror.data.modelo.User;
import org.example.springavanzadoalvaror.servicios.LoginService;
import org.example.springavanzadoalvaror.ui.pantallas.common.BaseScreenController;
import org.springframework.stereotype.Component;

@Component
public class LoginController extends BaseScreenController {

    private final LoginService loginService;

    @FXML
    private TextField username;
    @FXML
    private TextField password;


    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @FXML
    private void doLogin() {
        User u = new User();
        loginService.doLogin(username.getText(), password.getText()).peek(success -> {
                    if (Boolean.TRUE.equals(success)) {
                        u.setName(username.getText());
                        getPrincipalController().onLoginDone(u);
                    }
                })
                .peekLeft(loginError -> getPrincipalController().showErrorAlert(loginError.getErrorMessage()));
    }

    @FXML
    private void doRegister() {
        loginService.doRegister(username.getText(), password.getText()).peek(user -> {
                    getPrincipalController().showConfirmationAlert(Constantes.USUARIO_REGISTRADO_CORRECTAMENTE);
                    getPrincipalController().onLoginDone(user);
                })
                .peekLeft(loginError -> getPrincipalController().showErrorAlert(loginError.getErrorMessage()));
    }
}
