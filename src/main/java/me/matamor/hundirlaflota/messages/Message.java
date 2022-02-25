package me.matamor.hundirlaflota.messages;

import me.matamor.hundirlaflota.util.ColoredMessage;

import java.awt.*;

public enum Message {

    /* MENSAJES DE CONTROLADOR */
    NO_HANDLER_PACKET(Color.RED,"El paquete que has enviado no tiene ningún handler, has sido expulsado!"),
    ALIVE_KICK(Color.RED,"Tu cliente ha fallado en enviar el packet alive, has sido desconectado!"),
    ON_PACKET_ERROR(Color.RED,"Ha habido un error al leer tu paquete, has sido expulsado!"),
    /* MENSAJES DE SALA */
    ALREADY_IN_SALA(Color.RED,"Ya estas dentro de una sala! Primero debes salir con el comando: /sala leave"),
    NOT_IN_SALA(Color.RED,"No estas en ninguna sala!"),
    JOIN_SALA(Color.GRAY,"%s se ha unido a la sala!"),
    JOIN_SALA_SELF(Color.GRAY,"Te has unido a la sala: %s"),
    FULL_SALA(Color.RED,"Esta sala ya está llena"),
    SALA_READY(Color.CYAN,"Sala lista! El dueño de la sala puede empezar la partida con el comando: /sala start"),
    SALA_NOT_READY(Color.RED,"La sala no está lista! Deben haber 2 usuarios para poder empezar una partida!"),
    LEFT_SALA(Color.GRAY, "Has salido de la sala!"),
    SALA_NOT_OWNER(Color.RED, "No eres el dueño de la sala!"),
    /* MENSAJES GENERALES */
    SPEAK(Color.BLACK, "%s: %s"),
    NUMERO_INVALIDO(Color.RED,"El valor introducido '%s' no es un número valido!"),
    /* MENSAJES DE PARTIDA */
    LEFT_PARTIDA(Color.RED,"'%s' ha abandonado la partida!"),
    PARTIDA_CANCELADA(Color.RED,"La partida ha sido cancelada!"),
    GAME_BEGIN(Color.GREEN,"La partida ha sido iniciada! Ahora debes poner los '%s' barcos en tu tablero!"),
    TABLERO_PERSONAL(Color.GREEN,"TU TABLERO"),
    TABLERO_ENEMIGO(Color.RED,"TABLERO ENEMIGO"),
    BARCO_LENGTH_INFO(Color.GRAY,"Puedes crear un barco de la siguiente longitud: %s"),
    BARCO_PLACE_INFO(Color.CYAN,"Puedes crear un barco escribiendo: <coordenada> (A1) <longitud> (5) <orientación> (%s)"),
    BARCO_PLACER_FORMATO(Color.RED,"Formato incorrecto de barco! Formato: <coordenada> <longitud> <orientación> (%s)"),
    BARCO_LONGITUD_INVALIDA(Color.RED,"La longitud introducida '%s' está fuera del rango valido %s-%s"),
    ORIENTACION_BARCO_INVALIDA(Color.RED,"La orientación introducida '%s' es invalida!"),
    BARCO_ALREADY_PLACED(Color.RED,"Ya has puesto un barco con la longitud '%s'!"),
    TABLERO_READY(Color.GREEN,"Tu tablero está listo! Esperando al rival..."),
    NO_TABLERO_PLAYERS(Color.RED,"Uno o varios jugadores no han enviado su tablero, partida cancelada!"),
    GAME_START(Color.GREEN,"La partida ha empezado!"),
    GAME_TURN_SELF(Color.CYAN,"Es tu turno de atacar! Puedes atacar escribiendo: <coordenada> (A1)"),
    GAME_TURN_ENEMY(Color.GRAY,"Es turno del enemigo atacar"),
    NO_MOVE_PLAYER(Color.RED,"El jugador atacante no ha hecho su movimiento... Partida cancelada!"),
    ALREADY_ATTACKED(Color.RED,"Ya has atacado esa casilla!"),
    ATTACK_SENT(Color.CYAN, "Has atacado!"),
    ATTACK_MISS_SELF(Color.RED,"Tu ataque ha fallado!"),
    ATTACK_MISS_ENEMY(Color.GREEN,"El ataque del enemigo ha fallado!"),
    ATTACK_HIT_SELF(Color.GREEN,"Tu ataque ha tocado un barco!"),
    ATTACK_HIT_ENEMY(Color.RED,"El ataque enemigo ha tocado tu barco!"),
    BARCO_HUNDIDO_SELF(Color.CYAN,"El barco enemigo ha sido hundido!"),
    BARCO_HUNDIDO_ENEMY(Color.RED,"Tu barco ha sido hundido!"),
    WIN(Color.GREEN,"Has ganado la partida!"),
    LOSE(Color.RED,"Has perdido la partida!");

    private final Color color;
    private final String text;

    Message(String mensaje) {
        this(null, mensaje);
    }

    Message(Color color, String mensaje) {
        this.color = color;
        this.text = mensaje;
    }

    public boolean tieneColor() {
        return this.color != null;
    }

    public Color getColor() {
        return this.color;
    }

    public String getText() {
        return this.text;
    }

    public String getMessage(Object... args) {
        return String.format(this.text, args);
    }

    public ColoredMessage toMessage() {
        return new ColoredMessage(this.text, this.color);
    }

    @Override
    public String toString() {
        return this.text;
    }
}
