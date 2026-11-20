
package jp.flowfree.ventanas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.io.IOException;
import jp.flowfree.excepciones.FlowFreeException;
import jp.flowfree.modelo.Usuario;
import jp.flowfree.Main;

public class PantallaLogin extends Pantalla {

    private TextField campoUsuario;
    private TextField campoContrasena;
    private TextButton botonVer;
    private Label mensaje;

    public PantallaLogin(Main juego) {
        super(juego);
    }

    @Override
    public void construir() {
        Skin skin = juego.getSkin();

        // La tabla acomoda todo en filas, centrado en la pantalla
        Table tabla = new Table();
        tabla.setFillParent(true);

        // Título
        Label titulo = new Label("Flow Free", skin);
        tabla.add(titulo).colspan(2).padBottom(40).row();

        // Usuario
        tabla.add(new Label("Usuario", skin)).colspan(2).left().row();
        campoUsuario = new TextField("", skin);
        tabla.add(campoUsuario).colspan(2).width(300).height(40).padBottom(15).row();

        // Contraseña + botón Ver
        tabla.add(new Label("Contraseña", skin)).colspan(2).left().row();
        campoContrasena = new TextField("", skin);
        campoContrasena.setPasswordMode(true);
        campoContrasena.setPasswordCharacter('*');
        botonVer = new TextButton("Ver", skin);
        tabla.add(campoContrasena).width(220).height(40);
        tabla.add(botonVer).width(80).height(40).row();

        // Aquí se mostrarán los mensajes de error
        mensaje = new Label("", skin);
        tabla.add(mensaje).colspan(2).padTop(10).padBottom(10).row();

        // Botones
        TextButton botonIngresar = new TextButton("Ingresar", skin);
        TextButton botonCrearCuenta = new TextButton("Crear cuenta", skin);
        TextButton botonSalir = new TextButton("Salir", skin);
        tabla.add(botonIngresar).colspan(2).width(300).height(45).padBottom(10).row();
        tabla.add(botonCrearCuenta).colspan(2).width(300).height(45).padBottom(30).row();
        tabla.add(botonSalir).colspan(2).width(150).height(40).row();

        // Qué hace cada botón al darle clic
        botonVer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                if (campoContrasena.isPasswordMode()) {
                    campoContrasena.setPasswordMode(false);
                    botonVer.setText("Ocultar");
                } else {
                    campoContrasena.setPasswordMode(true);
                    botonVer.setText("Ver");
                }
            }
        });

        botonIngresar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
               ingresar();
            }
        });

        botonCrearCuenta.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
              juego.AbrirPantalla(new PantallaRegistro(juego));
            }
        });

        botonSalir.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                Gdx.app.exit();
            }
        });

        escenario.addActor(tabla);
        
    }
    private void ingresar() {
        try {
            Usuario usuario = juego.getGestionUsuarios().iniciarSesion(
                    campoUsuario.getText(), campoContrasena.getText());
            // Por ahora solo saluda; cuando exista el menú principal, aquí se abre
            mensaje.setColor(0.42f, 0.80f, 0.47f, 1);
            mensaje.setText("Bienvenido, " + usuario.getNombreCompleto());
        } catch (FlowFreeException e) {
            mensaje.setColor(1f, 0.42f, 0.42f, 1);
            mensaje.setText(e.getMessage());
        } catch (IOException e) {
            mensaje.setColor(1f, 0.42f, 0.42f, 1);
            mensaje.setText("Error al leer los archivos del usuario.");
        }
    }
    
}