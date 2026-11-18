package jp.flowfree;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import java.util.Stack;
import jp.flowfree.ventanas.Pantalla;
import jp.flowfree.ventanas.PantallaLogin;


public class Main extends Game {

    private Skin skin; 

    
    private Stack<Pantalla> pila = new Stack<>();

    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        
        TextField.TextFieldStyle estiloCaja = skin.get(TextField.TextFieldStyle.class);
        estiloCaja.background.setLeftWidth(10);
        estiloCaja.background.setRightWidth(10);
        estiloCaja.focusedBackground.setLeftWidth(10);
        estiloCaja.focusedBackground.setRightWidth(10);
        estiloCaja.disabledBackground.setLeftWidth(10);
        estiloCaja.disabledBackground.setRightWidth(10);
        
        AbrirPantalla(new PantallaLogin(this));
    }

    //Avanza a una pantalla nueva
    public void AbrirPantalla(Pantalla nueva) {
        pila.push(nueva);
        setScreen(nueva);
    }

    // Regresa a la pantalla anterior
    public void regresar() {
        if (pila.size() <= 1) {
            return; 
        }
        final Pantalla actual = pila.pop();
        setScreen(pila.peek());
        
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                actual.dispose();
            }
        });
    }

    public Skin getSkin() {
        return skin;
    }

    @Override
    public void dispose() {
        while (pila.isEmpty() == false) {
            pila.pop().dispose();
        }
        skin.dispose();
    }
}