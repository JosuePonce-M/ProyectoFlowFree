
package jp.flowfree.ventanas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
}