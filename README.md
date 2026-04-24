# Sombras del Avismo

Juego de cartas en Java con Swing, pensado para ser sencillo, rápido de entender y jugable desde el primer turno.

## Estado actual

El juego incluye:

- Turnos alternos entre dos jugadores
- Mana que crece turno a turno
- Criaturas que entran cansadas y atacan a partir de tu siguiente turno
- Hechizos de dano, curacion y robo
- Vista previa de cartas, registro de partida y coleccion visual
- Tests automáticos para la lógica principal del juego

## Cómo ejecutar

Con Maven:

1. `mvn compile`
2. `mvn exec:java`

Sin Maven:

1. `javac src/main/java/com/sombrasdelavismo/*.java`
2. `java -cp src/main/java com.sombrasdelavismo.Main`

## Cómo validar

- `mvn test`

## Reglas rápidas

1. Al empezar tu turno robas una carta y recuperas todo el mana.
2. Puedes jugar varias cartas si te alcanza el mana.
3. Las criaturas no pueden atacar el turno en que se juegan.
4. En tu siguiente turno, esas criaturas ya podrán atacar.
5. Los hechizos se resuelven al instante.
6. Gana quien deje al rival a 0 vidas.
