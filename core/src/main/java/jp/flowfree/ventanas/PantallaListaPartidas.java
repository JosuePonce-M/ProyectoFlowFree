package jp.flowfree.ventanas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import jp.flowfree.Main;
import jp.flowfree.modelo.Partida;

/**
 * Lista de las 3 partidas. La misma pantalla sirve para Continuar y para Eliminar;
 * solo cambia el título y lo que pasa al tocar una partida.
 */
public class PantallaListaPartidas extends Pantalla {

    private boolean modoContinuar;
    private Table lista;
    private Label mensaje;
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

    public PantallaListaPartidas(Main juego, boolean modoContinuar) {
        super(juego);
        this.modoContinuar = modoContinuar;
    }

    @Override
    public void construir() {
        Skin skin = juego.getSkin();

        String textoTitulo = "Eliminar partida";
        if (modoContinuar) {
            textoTitulo = "Continuar partida";
        }

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.top().padTop(110);

        Label titulo = new Label(textoTitulo, skin);
        titulo.setFontScale(1.6f);
        tabla.add(titulo).padBottom(30).row();

        lista = new Table();
        tabla.add(lista).row();

        mensaje = new Label("", skin);
        mensaje.setColor(Color.RED);
        tabla.add(mensaje).padTop(15).row();
        escenario.addActor(tabla);

        // Abajo a la derecha: Volver
        Table abajo = new Table();
        abajo.setFillParent(true);
        abajo.bottom().right().pad(15);
        TextButton botonVolver = new TextButton("Volver", skin);
        abajo.add(botonVolver).width(110).height(40);
        escenario.addActor(abajo);

        botonVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                juego.regresar();
            }
        });
    }

    // Se vuelve a llenar cada vez que se muestra (por ejemplo, al volver del mapa)
    @Override
    public void show() {
        super.show();
        llenarLista();
    }

    private void llenarLista() {
        lista.clear();
        mensaje.setText("");
        for (int numero = 1; numero <= PantallaPartidas.MAX_PARTIDAS; numero++) {
            try {
                Partida partida = juego.getGestionUsuarios().leerPartida(numero);
                if (partida == null) {
                    lista.add(crearEspacioLibre(numero)).width(360).height(100).padBottom(15).row();
                } else {
                    lista.add(crearTarjeta(partida)).width(360).height(100).padBottom(15).row();
                }
            } catch (IOException e) {
                mensaje.setText("La partida " + numero + " no se pudo leer.");
            }
        }
    }

    // Tarjeta con la información básica que pide el PDF
    private Button crearTarjeta(final Partida partida) {
        Skin skin = juego.getSkin();
        Button tarjeta = new Button(skin);
        tarjeta.pad(10, 15, 10, 15);

        String fecha = formatoFecha.format(new Date(partida.getFechaCreacion()));
        long minutos = partida.getTiempoJugado() / 60;

        tarjeta.add(new Label("Partida " + partida.getNumero(), skin)).expandX().left();
        Label etiquetaFecha = new Label(fecha, skin);
        etiquetaFecha.setColor(Color.LIGHT_GRAY);
        tarjeta.add(etiquetaFecha).right().row();
        tarjeta.add(new Label("Nivel alcanzado: " + partida.getNivelAlcanzado(), skin)).left().padTop(6);
        tarjeta.add(new Label("Puntaje: " + partida.getPuntajeTotal(), skin)).right().padTop(6).row();
        tarjeta.add(new Label("Tiempo jugado: " + minutos + " min", skin)).left().padTop(4).colspan(2);

        tarjeta.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent evento, Actor actor) {
                tocarPartida(partida);
            }
        });
        return tarjeta;
    }

    // Espacio vacío: solo texto gris, no se puede tocar
    private Table crearEspacioLibre(int numero) {
        Table espacio = new Table();
        Label texto = new Label("Partida " + numero + " - espacio libre", juego.getSkin());
        texto.setColor(Color.GRAY);
        espacio.add(texto);
        return espacio;
    }

    private void tocarPartida(final Partida partida) {
        if (modoContinuar) {
            juego.AbrirPantalla(new PantallaMapaNiveles(juego, partida));
            return;
        }
        confirmar("Eliminar partida",
                "¿Seguro que quieres eliminar la Partida " + partida.getNumero() + "?", new Runnable() {
            @Override
            public void run() {
                try {
                    juego.getGestionUsuarios().borrarPartida(partida.getNumero());
                } catch (IOException e) {
                    llenarLista();
                    mensaje.setText("No se pudo eliminar la partida.");
                    return;
                }
                llenarLista();
            }
        });
    }
}
