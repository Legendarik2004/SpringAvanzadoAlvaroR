package org.example.springavanzadoalvaror.ui.pantallas.common;



import lombok.Getter;
import org.example.springavanzadoalvaror.ui.pantallas.principal.PrincipalController;

import java.io.IOException;


@Getter
public class BaseScreenController {

    private PrincipalController principalController;

    public void setPrincipalController(PrincipalController principalController) {
        this.principalController = principalController;
    }

    public void principalCargado() throws IOException {
        //NOP
    }
}
