package me.matamor.hundirlaflota.cliente.game;

import me.matamor.hundirlaflota.cliente.ClientHandler;
import me.matamor.hundirlaflota.juego.barcos.OrientacionBarco;
import me.matamor.hundirlaflota.juego.tablero.CoordenadaUtil;
import me.matamor.hundirlaflota.juego.tablero.TableroException;
import me.matamor.hundirlaflota.messages.Message;
import me.matamor.hundirlaflota.util.Constantes;
import me.matamor.hundirlaflota.util.Posicion;

import java.util.Arrays;

public class BarcoPlacer {

    private final ClientHandler clientHandler;

    public BarcoPlacer(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void handleInput(String message) {
        String[] args = message.split(" ");
        if (args.length != 3) {
            this.clientHandler.printMessage(Message.BARCO_PLACER_FORMATO, Arrays.toString(OrientacionBarco.values()));
        } else {
            String coordenada = args[0];

            try {
                //Parseamos la coordenada
                Posicion posicion = CoordenadaUtil.parseCoordenada(coordenada);

                try {
                    int longitud = Integer.parseInt(args[1]);

                    //Comprobamos si la longitud es valida
                    if (longitud < 0 || longitud > Constantes.BARCOS_MAX_LONGITUD) {
                        this.clientHandler.printMessage(Message.BARCO_LONGITUD_INVALIDA, longitud, 1, Constantes.BARCOS_MAX_LONGITUD);
                    } else {
                        if (this.clientHandler.getGameClient().getTableroPersonal().tieneBarco(longitud)) {
                            this.clientHandler.printMessage(Message.BARCO_ALREADY_PLACED, longitud);
                        } else {
                            //Leemos la orientación introducida
                            OrientacionBarco orientacionBarco = OrientacionBarco.buscarOrientacion(args[2]);
                            if (orientacionBarco == null) {
                                //La orientación introducida es inválida
                                this.clientHandler.printMessage(Message.ORIENTACION_BARCO_INVALIDA, args[2]);
                            } else {
                                try {
                                    //Creamos el barco con los parameters introducidos
                                    this.clientHandler.getGameClient().getTableroPersonal().crearBarco(posicion, longitud, orientacionBarco);

                                    this.clientHandler.updateInterface();

                                    if (this.clientHandler.getGameClient().isPlayerReady()) {
                                        this.clientHandler.getGameClient().enviarTablero();
                                    }
                                } catch (TableroException e) {
                                    //Ha habido un error al crear el barco
                                    this.clientHandler.printMessage(e.getMessage());
                                }
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    //La longitud introducida no es un número válido
                    this.clientHandler.printMessage(Message.NUMERO_INVALIDO, args[1]);
                }
            } catch (CoordenadaUtil.CoordenadaException e) {
                //La coordenada introducida no válida
                this.clientHandler.printMessage(e.getMessage());
            }
        }
    }
}
