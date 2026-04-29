# Sombras del Abismo

Juego de cartas por turnos en Java POO con interfaz grafica Swing.

## Que incluye

- Partida hot-seat para 2 jugadores.
- Tablero grafico con mano, criaturas, log, vida y contador de mana.
- Mana adaptado al mazo del proyecto: sube de 5 en 5 hasta 30.
- Mareo de invocacion, combate, defensa y fatiga por robar sin mazo.
- Cartas y hechizos personalizados del proyecto: `Panda`, `Carpazo`, `En Chino`, `El Grito`, `Humo`, `Hacker`, `Kung Fu`, `Fuego`, `Simeone`, `Bano`, etc.
- Combo de `Adrian + Fabio + Fernando + Bano` para desbloquear `Humo`.

## Estructura

- `src/main/java/com/sombrasdelavismo/model`: logica del juego.
- `src/main/java/com/sombrasdelavismo/ui`: interfaz Swing.
- `src/main/java/com/sombrasdelavismo/Main.java`: punto de entrada.

## Como ejecutarlo en VSCode

1. Abre la carpeta del proyecto.
2. Asegurate de tener una extension de Java y un JDK configurado en VSCode.
3. Ejecuta `Main.java`.

## Tests

Los tests cubren reglas clave:

- mareo de invocacion
- invocacion de Lin con `En Chino`
- desbloqueo de `Humo` con `Bano`
- ocultacion de criaturas con `Humo`
- robo de cartas con `Hacker`

## Suposiciones tomadas

- El mazo de ambos jugadores es identico para que la partida sea jugable desde el primer momento.
- `Marco` reduce en 2 el danio que recibe para reflejar que es dificil de tocar.
- `Panda` potencia especialmente a `Marco`.
- `Mono A` invoca una ficha `Mono A` 3/2.
- `Humo` necesita haberse desbloqueado antes con `Bano`.
