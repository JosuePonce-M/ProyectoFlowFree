package jp.flowfree;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jp.flowfree.ventanas.PantallaLogin;

public class Main extends Game {

    private Skin skin; // estilos de botones, textos y cajas (compartido por todas las pantallas)

    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        setScreen(new PantallaLogin(this));
    }

    public Skin getSkin() {
        return skin;
    }

    @Override
    public void dispose() {
        getScreen().dispose();
        skin.dispose();
    }
}