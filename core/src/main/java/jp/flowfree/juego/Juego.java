package jp.flowfree.juego;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Juego {

  
    public abstract void iniciar();

  
    public abstract void actualizar(float delta);

  
    public abstract void dibujar(ShapeRenderer pincel);

  
    public abstract boolean verificarVictoria();
}
