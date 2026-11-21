package jp.flowfree.ventanas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import java.io.IOException;
import jp.flowfree.Main;
import jp.flowfree.modelo.Usuario;

public class MenuPrincipal extends Pantalla {

    private Texture texturaAvatar; 
    private Label mensaje;

    public MenuPrincipal(Main juego) {
        super(juego);
    }

    @Override
    public void construir() {
        Skin skin = juego.getSkin();
        Usuario usuario = juego.getGestionUsuarios().getUsuarioActual();

       
        Table esquina = new Table();
        esquina.setFillParent(true);
        esquina.top().right().pad(15);

        Label nombre = new Label(usuario.getNombreCompleto(), skin);
        esquina.add(nombre).padRight(8);
        esquina.add(crearAvatarTemporal(usuario, skin)).size(36);
        escenario.addActor(esquina);

        // ===== Botones (centrados) =====
        Table tabla = new Table();
        tabla.setFillParent(true);

        tabla.add(new Label("Flow Free", skin)).padBottom(40).row();

        TextButton botonJugar = new TextButton("Jugar", skin);
        TextButton botonConfiguracion = new TextButton("Configuración", skin);
        TextButton botonEstadisticas = new TextButton("Estadísticas y ranking", skin);
        TextButton botonCerrarSesion = new TextButton("Cerrar sesión", skin);
        TextButton botonSalir = new TextButton("Salir", skin);

        tabla.add(botonJugar).width(300).height(40).padBottom(15).row();
        tabla.add(botonConfiguracion).width(300).height(40).padBottom(15).row();
        tabla.add(botonEstadisticas).width(300).height(40).padBottom(15).row();
        tabla.add(botonCerrarSesion).width(300).height(40).padBottom(35).row();
        tabla.add(botonSalir).width(150).height(35).row();

        mensaje = new Label("", skin);
        tabla.add(mensaje).padTop(20).row();

        escenario.addActor(tabla);

     
        ChangeListener proximamente = new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                mensaje.setText("Esta opción la hacemos más adelante");
            }
        };
        botonJugar.addListener(proximamente);
        botonConfiguracion.addListener(proximamente);
        botonEstadisticas.addListener(proximamente);

        botonCerrarSesion.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                confirmar("Cerrar sesión", "¿Seguro que quieres cerrar sesión?", new Runnable() {
                    @Override
                    public void run() {
                        cerrarSesion();
                    }
                });
            }
        });

        botonSalir.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                confirmar("Salir", "¿Seguro que quieres salir del juego?", new Runnable() {
                    @Override
                    public void run() {
                        Gdx.app.exit();
                    }
                });
            }
        });
    }

    /** avatar. */
    private Stack crearAvatarTemporal(Usuario usuario, Skin skin) {
        Pixmap dibujo = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        dibujo.setColor(0.22f, 0.54f, 0.87f, 1); // azul
        dibujo.fillCircle(32, 32, 31);
        texturaAvatar = new Texture(dibujo);
        dibujo.dispose();

        String inicial = usuario.getNombreCompleto().substring(0, 1).toUpperCase();
        Label letra = new Label(inicial, skin);
        letra.setAlignment(Align.center);

        Stack avatar = new Stack();
        avatar.add(new Image(texturaAvatar));
        avatar.add(letra);
        return avatar;
    }

    /** Guarda todo, cierra la sesión  */
    private void cerrarSesion() {
        try {
            juego.getGestionUsuarios().cerrarSesion();
        } catch (IOException e) {
            Gdx.app.error("FlowFree", "No se pudo guardar al cerrar sesión", e);
        }
        juego.reiniciarPantallas(new PantallaLogin(juego));
    }

    @Override
    public void dispose() {
        super.dispose(); 
        if (texturaAvatar == null) {
            return;
        }
        texturaAvatar.dispose();
    }
}
