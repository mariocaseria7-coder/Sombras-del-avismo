package com.sombrasdelavismo.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CardCatalog {
    private static final Map<String, Card> PROTOTYPES = new LinkedHashMap<>();

    static {
        registerCreatures();
        registerSpells();
    }

    private CardCatalog() {
    }

    public static Card createCard(String id) {
        Card prototype = PROTOTYPES.get(id);
        if (prototype == null) {
            throw new IllegalArgumentException("Carta desconocida: " + id);
        }
        return prototype.copy();
    }

    public static CreatureCard createCreature(String id) {
        return (CreatureCard) createCard(id);
    }

    public static SpellCard createSpell(String id) {
        return (SpellCard) createCard(id);
    }

    public static List<Card> createDefaultDeck() {
        List<Card> deck = new ArrayList<>();

        add(deck, "ADRIAN", 1);
        add(deck, "FABIO", 1);
        add(deck, "FERNANDO", 1);
        add(deck, "ANTONIO", 1);
        add(deck, "INES", 1);
        add(deck, "URBANO", 1);
        add(deck, "CARMEN", 1);
        add(deck, "LIN", 1);
        add(deck, "QUEMO", 1);
        add(deck, "FRAN_A", 1);
        add(deck, "JAVI_B", 1);
        add(deck, "JAVI", 1);
        add(deck, "PEDRO", 1);
        add(deck, "FRAN", 1);
        add(deck, "MARIO_A", 1);
        add(deck, "MARIO_R", 1);
        add(deck, "ALEJANDRO", 1);
        add(deck, "LUIS", 1);
        add(deck, "MAMEN", 1);
        add(deck, "DIEGO", 1);
        add(deck, "JUANMA", 1);
        add(deck, "MARCO", 1);

        add(deck, "PANDA", 1);
        add(deck, "CARPAZO", 3);
        add(deck, "EN_CHINO", 1);
        add(deck, "EL_GRITO", 3);
        add(deck, "MONO_A", 1);
        add(deck, "SCANNER", 1);
        add(deck, "APUESTA", 2);
        add(deck, "HUMO", 2);
        add(deck, "HACKER", 2);
        add(deck, "KUNG_FU", 1);
        add(deck, "FUEGO", 1);
        add(deck, "SIMEONE", 1);
        add(deck, "BANO", 1);

        return deck;
    }

    private static void add(List<Card> deck, String id, int amount) {
        for (int i = 0; i < amount; i++) {
            deck.add(createCard(id));
        }
    }

    private static void registerCreatures() {
        register(new CreatureCard(
                "MARCO",
                "Marco",
                30,
                7,
                6,
                "Complicado de tocar. Reduce en 2 el danio que recibe.",
                "images/carta_marco.jpg",
                true,
                false));
        register(new CreatureCard(
                "JUANMA",
                "Juanma",
                20,
                7,
                4,
                "Pega fuerte y combina con Carpazo.",
                "images/carta_juanma.jpg"));
        register(new CreatureCard(
                "DIEGO",
                "Diego",
                18,
                5,
                6,
                "El maestro de En Chino. Puede traer a Lin al tablero.",
                "images/carta_dieguito.jpg"));
        register(new CreatureCard(
                "MAMEN",
                "Mamen",
                15,
                3,
                8,
                "Aguanta la mesa mientras prepara El Grito.",
                "images/carta_mamen.jpg"));
        register(new CreatureCard(
                "LUIS",
                "Luis",
                10,
                2,
                5,
                "Genera apoyo con Mono A.",
                "images/carta_luis.jpg"));
        register(new CreatureCard(
                "ALEJANDRO",
                "Alejandro",
                10,
                3,
                4,
                "Controla la informacion del rival con Scanner.",
                null));
        register(new CreatureCard(
                "MARIO_R",
                "Mario R",
                9,
                4,
                3,
                "Gana mana extra si aparece Apuesta.",
                "images/carta_marito.jpg"));
        register(new CreatureCard(
                "MARIO_A",
                "Mario A",
                9,
                1,
                6,
                "Resiste mucho y ayuda a rentabilizar Apuesta.",
                "images/carta_marito.jpg"));
        register(new CreatureCard(
                "FRAN",
                "Fran",
                9,
                5,
                2,
                "Aprovecha el humo para confundir al rival.",
                "images/carta_fran.jpg"));
        register(new CreatureCard(
                "PEDRO",
                "Pedro",
                9,
                3,
                4,
                "Aprovecha el humo para ocultar jugadas.",
                "images/carta_pedro.jpg"));
        register(new CreatureCard(
                "JAVI",
                "Javi",
                9,
                4,
                3,
                "Acompania al hechizo Hacker.",
                "images/carta_javi.jpg"));
        register(new CreatureCard(
                "LIN",
                "Lin",
                8,
                6,
                4,
                "Kung Fu puede subir su ataque hasta 10.",
                "images/carta_lin.jpg"));
        register(new CreatureCard(
                "QUEMO",
                "Quemo",
                8,
                2,
                5,
                "Su fuego desgasta toda la mesa.",
                "images/carta_quemao.jpg"));
        register(new CreatureCard(
                "JAVI_B",
                "Javi B",
                8,
                5,
                2,
                "Otra via para lanzar Hacker.",
                "images/carta_javi.jpg"));
        register(new CreatureCard(
                "FRAN_A",
                "Fran A",
                8,
                4,
                6,
                "Simeone protege al equipo durante una ronda.",
                "images/carta_fran.jpg"));
        register(new CreatureCard(
                "CARMEN",
                "Carmen",
                8,
                5,
                6,
                "Una de las criaturas mas solidas del mazo.",
                "images/carta_carmen.jpg"));
        register(new CreatureCard(
                "URBANO",
                "Urbano",
                3,
                3,
                1,
                "Sale rapido para presionar en los primeros turnos.",
                "images/carta_urbano.jpg"));
        register(new CreatureCard(
                "INES",
                "Ines",
                2,
                2,
                2,
                "Criatura de apoyo para comenzar la partida.",
                "images/carta_ines.jpg"));
        register(new CreatureCard(
                "ANTONIO",
                "Antonio",
                2,
                2,
                2,
                "Criatura equilibrada de apertura.",
                "images/carta_antonio.jpg"));
        register(new CreatureCard(
                "ADRIAN",
                "Adrian",
                1,
                1,
                3,
                "Miembro del combo de Bano.",
                "images/carta_adrian.jpg",
                false,
                true));
        register(new CreatureCard(
                "FABIO",
                "Fabio",
                1,
                1,
                3,
                "Miembro del combo de Bano.",
                "images/carta_fabio.jpg",
                false,
                true));
        register(new CreatureCard(
                "FERNANDO",
                "Fernando",
                1,
                1,
                3,
                "Miembro del combo de Bano.",
                "images/carta_fernando.jpg",
                false,
                true));
        register(new CreatureCard(
                "MONO_A_TOKEN",
                "Mono A",
                0,
                3,
                2,
                "Ficha invocada por el hechizo Mono A.",
                null));
    }

    private static void registerSpells() {
        register(new SpellCard(
                "PANDA",
                "Panda",
                4,
                SpellEffect.BUFF,
                SpellColor.RED,
                0,
                2,
                null,
                "Hechizo rojo: da +3 de ataque y +2 de vida. En Marco pega mucho mas.",
                "images/hechizo_panda.jpg"));
        register(new SpellCard(
                "CARPAZO",
                "Carpazo",
                4,
                SpellEffect.ENEMY_BOARD_DAMAGE,
                SpellColor.RED,
                3,
                0,
                null,
                "Hace 3 de danio a todas las criaturas enemigas.",
                "images/hechizo_carpazo.jpg"));
        register(new SpellCard(
                "EN_CHINO",
                "En Chino",
                2,
                SpellEffect.SUMMON_LIN,
                SpellColor.BLUE,
                0,
                0,
                "LIN",
                "Invoca a Lin directamente en tu tablero.",
                "images/hechizo_enchino.jpg"));
        register(new SpellCard(
                "EL_GRITO",
                "El Grito",
                6,
                SpellEffect.ALL_BOARD_DAMAGE,
                SpellColor.RED,
                3,
                0,
                null,
                "Hace 3 de danio a todas las criaturas de ambos jugadores.",
                "images/hechizo_elgrito.jpg"));
        register(new SpellCard(
                "MONO_A",
                "Mono A",
                2,
                SpellEffect.SUMMON_MONO_A,
                SpellColor.BLUE,
                0,
                0,
                "MONO_A_TOKEN",
                "Invoca una ficha Mono A 3/2.",
                "images/hechizo_f1.jpg"));
        register(new SpellCard(
                "SCANNER",
                "Scanner",
                2,
                SpellEffect.REVEAL_HAND,
                SpellColor.BLUE,
                0,
                0,
                null,
                "Muestra la mano del rival durante tu turno.",
                null));
        register(new SpellCard(
                "APUESTA",
                "Apuesta",
                2,
                SpellEffect.GAIN_MANA,
                SpellColor.BLUE,
                3,
                0,
                null,
                "Ganas 3 de mana actual y 3 de mana maximo.",
                "images/hechizo_apuestas.jpg"));
        register(new SpellCard(
                "HUMO",
                "Humo",
                2,
                SpellEffect.SMOKE,
                SpellColor.BLUE,
                0,
                0,
                null,
                "La siguiente carta que juegues quedara oculta para el rival.",
                "images/hechizo_humo.jpg"));
        register(new SpellCard(
                "HACKER",
                "Hacker",
                2,
                SpellEffect.STEAL_CARD,
                SpellColor.BLUE,
                0,
                0,
                null,
                "Roba una carta aleatoria de la mano rival.",
                "images/hechizo_hackear.jpg"));
        register(new SpellCard(
                "KUNG_FU",
                "Kung Fu",
                3,
                SpellEffect.SET_ATTACK_TO_TEN,
                SpellColor.BLUE,
                10,
                0,
                null,
                "Sube el ataque de una criatura propia hasta 10.",
                "images/hechizo_kungfu.jpg"));
        register(new SpellCard(
                "FUEGO",
                "Fuego",
                3,
                SpellEffect.FIRE_PULSE,
                SpellColor.RED,
                1,
                0,
                null,
                "Hace 1 de danio a todas las criaturas del tablero.",
                "images/hechizo_fueguito.jpg"));
        register(new SpellCard(
                "SIMEONE",
                "Simeone",
                3,
                SpellEffect.PROTECT_ALLIES,
                SpellColor.BLUE,
                0,
                0,
                null,
                "Tus criaturas no reciben danio enemigo durante la siguiente ronda rival.",
                null));
        register(new SpellCard(
                "BANO",
                "Bano",
                2,
                SpellEffect.UNLOCK_SMOKE,
                SpellColor.GRAY,
                0,
                0,
                null,
                "Si Adrian, Fabio y Fernando estan en mesa, desbloquea Humo para el resto de la partida.",
                null));
    }

    private static void register(Card card) {
        PROTOTYPES.put(card.getId(), card);
    }
}
