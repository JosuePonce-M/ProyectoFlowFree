package jp.flowfree.ventanas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.io.IOException;
import jp.flowfree.excepciones.FlowFreeException;
import jp.flowfree.Main;
import jp.flowfree.util.ValidarContrasena;

public class PantallaRegistro extends Pantalla {

    private static final Color VERDE = new Color(0.42f, 0.80f, 0.47f, 1);
    private static final Color GRIS = new Color(0.6f, 0.6f, 0.6f, 1);
    private static final Color ROJO = new Color(1f, 0.42f, 0.42f, 1);

    private TextField campoUsuario;
    private TextField campoNombre;
    private TextField campoContrasena;
    private TextField campoConfirmar;
    private TextButton botonVer;

    // Etiquetas de los 5 requisitos
    private Label reqLongitud;
    private Label reqMayuscula;
    private Label reqMinuscula;
    private Label reqNumero;
    private Label reqSimbolo;

    private Label coincidencia; // dice si las contraseñas coinciden
    private Label mensaje;      // mensajes de error

    public PantallaRegistro(Main juego) {
        super(juego);
    }

    @Override
    public void construir() {
        Skin skin = juego.getSkin();

        // ===== Tabla principal (centrada) =====
        Table tabla = new Table();
        tabla.setFillParent(true);

        // Título
        tabla.add(new Label("Crear cuenta", skin)).colspan(2).padBottom(20).row();

        // Usuario
        tabla.add(new Label("Usuario (sin espacios)", skin)).colspan(2).left().row();
        campoUsuario = new TextField("", skin);
        tabla.add(campoUsuario).colspan(2).width(300).height(30).padBottom(10).row();

        // Nombre completo
        tabla.add(new Label("Nombre completo", skin)).colspan(2).left().row();
        campoNombre = new TextField("", skin);
        tabla.add(campoNombre).colspan(2).width(300).height(30).padBottom(10).row();

        // Contraseña + botón Ver
        tabla.add(new Label("Contraseña", skin)).colspan(2).left().row();
        campoContrasena = new TextField("", skin);
        campoContrasena.setPasswordMode(true);
        campoContrasena.setPasswordCharacter('*');
        botonVer = new TextButton("Ver", skin);
        tabla.add(campoContrasena).width(220).height(30);
        tabla.add(botonVer).width(80).height(30).row();

        // Requisitos (se marcan mientras el usuario escribe)
        reqLongitud = new Label("", skin);
        reqMayuscula = new Label("", skin);
        reqMinuscula = new Label("", skin);
        reqNumero = new Label("", skin);
        reqSimbolo = new Label("", skin);
        tabla.add(reqLongitud).colspan(2).left().padTop(5).row();
        tabla.add(reqMayuscula).colspan(2).left().row();
        tabla.add(reqMinuscula).colspan(2).left().row();
        tabla.add(reqNumero).colspan(2).left().row();
        tabla.add(reqSimbolo).colspan(2).left().padBottom(10).row();

        // Confirmar contraseña
        tabla.add(new Label("Confirmar contraseña", skin)).colspan(2).left().row();
        campoConfirmar = new TextField("", skin);
        campoConfirmar.setPasswordMode(true);
        campoConfirmar.setPasswordCharacter('*');
        tabla.add(campoConfirmar).colspan(2).width(300).height(30).row();

        coincidencia = new Label("", skin);
        tabla.add(coincidencia).colspan(2).left().padTop(3).row();

        // Mensajes de error
        mensaje = new Label("", skin);
        mensaje.setColor(ROJO);
        tabla.add(mensaje).colspan(2).padTop(10).padBottom(10).row();

        // Botón para registrarse
        TextButton botonRegistrar = new TextButton("Registrarme", skin);
        tabla.add(botonRegistrar).colspan(2).width(300).height(40).row();

        escenario.addActor(tabla);

        // ===== Botón Volver (abajo a la derecha) =====
        Table esquina = new Table();
        esquina.setFillParent(true);
        esquina.bottom().right().pad(15);
        TextButton botonVolver = new TextButton("Volver", skin);
        esquina.add(botonVolver).width(90).height(30);
        escenario.addActor(esquina);

        // ===== Acciones =====

        // Cada vez que cambia el texto de las contraseñas, se revisan los requisitos
        ChangeListener alEscribir = new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                actualizarRequisitos();
            }
        };
        campoContrasena.addListener(alEscribir);
        campoConfirmar.addListener(alEscribir);

        botonVer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                if (campoContrasena.isPasswordMode()) {
                    campoContrasena.setPasswordMode(false);
                    campoConfirmar.setPasswordMode(false);
                    botonVer.setText("Ocultar");
                } else {
                    campoContrasena.setPasswordMode(true);
                    campoConfirmar.setPasswordMode(true);
                    botonVer.setText("Ver");
                }
            }
        });

        botonRegistrar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
               registrar();
            }
        });

        botonVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                juego.regresar();
            }
        });

        actualizarRequisitos(); // para que al abrir se vean los 5 requisitos en gris
    }

    /** Revisa la contraseña y marca cada requisito en verde [x] o gris [ ]. */
    private void actualizarRequisitos() {
        String contrasena = campoContrasena.getText();

        marcar(reqLongitud, "Mínimo 8 caracteres", ValidarContrasena.tieneLongitud(contrasena));
        marcar(reqMayuscula, "Una letra mayúscula", ValidarContrasena.tieneMayuscula(contrasena));
        marcar(reqMinuscula, "Una letra minúscula", ValidarContrasena.tieneMinuscula(contrasena));
        marcar(reqNumero, "Un número", ValidarContrasena.tieneNumero(contrasena));
        marcar(reqSimbolo, "Un símbolo", ValidarContrasena.tieneSimbolo(contrasena));

        String confirmacion = campoConfirmar.getText();
        if (confirmacion.isEmpty()) {
            coincidencia.setText("");
        } else if (confirmacion.equals(contrasena)) {
            coincidencia.setText("Las contraseñas coinciden");
            coincidencia.setColor(VERDE);
        } else {
            coincidencia.setText("Las contraseñas no coinciden");
            coincidencia.setColor(ROJO);
        }
    }

    private void marcar(Label etiqueta, String texto, boolean cumple) {
        if (cumple) {
            etiqueta.setText("[x] " + texto);
            etiqueta.setColor(VERDE);
        } else {
            etiqueta.setText("[ ] " + texto);
            etiqueta.setColor(GRIS);
        }
    }
    private void registrar() {
        try {
            juego.getGestionUsuarios().registrar(
                    campoUsuario.getText(),
                    campoNombre.getText(),
                    campoContrasena.getText(),
                    campoConfirmar.getText());
            mensaje.setColor(VERDE);
            mensaje.setText("Cuenta creada. Ya puedes volver e iniciar sesión.");
        } catch (FlowFreeException e) {
            mensaje.setColor(ROJO);
            mensaje.setText(e.getMessage());
        } catch (IOException e) {
            mensaje.setColor(ROJO);
            mensaje.setText("Error al guardar los archivos del usuario.");
        }
    }
}