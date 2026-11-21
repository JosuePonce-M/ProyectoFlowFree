
package jp.flowfree.ventanas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import jp.flowfree.Main;


public abstract class Pantalla implements Screen {

    protected Main juego;       
    protected Stage escenario;  
    private boolean construida = false;

    public Pantalla(Main juego) {
        this.juego = juego;
        this.escenario = new Stage(new FitViewport(450, 800));
    }

  
    public abstract void construir();

    
   
    public void show() {
        if (construida == false) {
            construir();
            construida = true;
        }
        Gdx.input.setInputProcessor(escenario);
    }

    
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);
        escenario.act(delta);
        escenario.draw();
    }

    @Override
    public void resize(int ancho, int alto) {
        if (ancho <= 0 || alto <= 0) {
            return; 
        }
        escenario.getViewport().update(ancho, alto, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

  
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    
    @Override
    public void dispose() {
        escenario.dispose();
    }

    /**
     * Muestra una ventana de confirmación con los botones "Sí" y "Cancelar".
     * Si el usuario elige "Sí", se ejecuta lo que viene en alAceptar.
     * Cualquier pantalla hija la puede usar.
     */
    protected void confirmar(String titulo, String pregunta, final Runnable alAceptar) {
        Dialog dialogo = new Dialog(titulo, juego.getSkin()) {
            @Override
            protected void result(Object respuesta) {
                if (Boolean.TRUE.equals(respuesta)) {
                    alAceptar.run();
                }
            }
        };
        dialogo.getContentTable().pad(15);
        dialogo.getButtonTable().pad(0, 15, 15, 15);
        dialogo.text(pregunta);
        // Tamaño de los botones y espacio entre ellos
        dialogo.getButtonTable().defaults().width(100).height(32).padLeft(15).padRight(15);
        dialogo.button("Sí", true);
        dialogo.button("Cancelar", false);
        dialogo.show(escenario);
    }

    /**
     * Muestra una ventana con un mensaje y un solo botón "Aceptar".
     * Al darle Aceptar se ejecuta lo que viene en alAceptar.
     */
    protected void avisar(String titulo, String mensaje, final Runnable alAceptar) {
        Dialog dialogo = new Dialog(titulo, juego.getSkin()) {
            @Override
            protected void result(Object respuesta) {
                alAceptar.run();
            }
        };
        dialogo.getContentTable().pad(15);
        dialogo.getButtonTable().pad(0, 15, 15, 15);
        dialogo.text(mensaje);
        dialogo.getButtonTable().defaults().width(100).height(32);
        dialogo.button("Aceptar", true);
        dialogo.show(escenario);
    }
}
