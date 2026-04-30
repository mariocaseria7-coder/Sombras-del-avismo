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

    public static List<Card> createAlbumCards() {
        List<Card> cards = new ArrayList<>();
        for (Card card : PROTOTYPES.values()) {
            if ("MONO_A_TOKEN".equals(card.getId())) {
                continue; // Ocultar ficha del album
            }
            cards.add(card.copy());
        }
        return cards;
    }

    public static List<Card> createDefaultDeck() {
        List<Card> deck = new ArrayList<>();

        add(deck, "ADRIAN", 3);
        add(deck, "FABIO", 3);
        add(deck, "FERNANDO", 3);
        add(deck, "ANTONIO", 4);
        add(deck, "INES", 4);
        add(deck, "URBANO", 3);
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
        add(deck, "MAMEN", 2);
        add(deck, "DIEGO", 1);
        add(deck, "JUANMA", 1);
        add(deck, "MARCO", 1);

        add(deck, "PANDA", 1);
        add(deck, "CARPAZO", 2);
        add(deck, "EN_CHINO", 1);
        add(deck, "EL_GRITO", 2);
        add(deck, "MONO_A", 1);
        add(deck, "SCANNER", 1);
        add(deck, "APUESTA", 2);
        add(deck, "HUMO", 2);
        add(deck, "HACKER", 1);
        add(deck, "KUNG_FU", 1);
        add(deck, "FUEGO", 1);
        add(deck, "SIMEONE", 1);
        add(deck, "BANO", 2);

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
                "Complicado de tocar. Reduce en 2 el daño que recibe.",
                "cards/createcard marco.jpg",
                CardRarity.MYTHIC,
                true,
                false));
        register(new CreatureCard(
                "JUANMA",
                "Juanma",
                20,
                7,
                4,
                "Pega fuerte y combina con Carpazo.",
                "cards/createcard juanma.jpg",
                CardRarity.EPIC));
        register(new CreatureCard(
                "DIEGO",
                "Dieguito",
                18,
                5,
                6,
                "El maestro de En Chino. Puede traer a Lin al tablero.",
                "cards/createcard dieguitoo.jpg",
                CardRarity.EPIC));
        register(new CreatureCard(
                "MAMEN",
                "Mamén",
                15,
                3,
                8,
                "Aguanta la mesa mientras prepara El Grito.",
                "cards/createcard mamen.jpg",
                CardRarity.EPIC));
        register(new CreatureCard(
                "LUIS",
                "Luismi el Nano",
                10,
                2,
                5,
                "Genera apoyo con Mono A.",
                "cards/createcard luismiii.jpg",
                CardRarity.RARE));
        register(new CreatureCard(
                "ALEJANDRO",
                "Crespo",
                10,
                3,
                4,
                "Controla la información del rival con Scanner.",
                "cards/createcard crespo.jpg",
                CardRarity.RARE));
        register(new CreatureCard(
                "MARIO_R",
                "Rodri el ludopata",
                9,
                4,
                3,
                "Gana maná extra si aparece Apuesta.",
                "cards/createcard rodri.jpg",
                CardRarity.RARE));
        register(new CreatureCard(
                "MARIO_A",
                "Marito el Tipster",
                9,
                1,
                6,
                "Resiste mucho y ayuda a rentabilizar Apuesta.",
                "cards/createcard marito.jpg",
                CardRarity.RARE));
        register(new CreatureCard(
                "FRAN",
                "Fran el Vapeador",
                9,
                5,
                2,
                "Aprovecha el humo para confundir al rival.",
                "cards/createcard fran.jpg",
                CardRarity.RARE));
        register(new CreatureCard(
                "PEDRO",
                "Pedrito sin prisas",
                9,
                3,
                4,
                "Aprovecha el humo para ocultar jugadas.",
                "cards/createcard pedro.jpg",
                CardRarity.RARE));
        register(new CreatureCard(
                "JAVI",
                "Javi",
                9,
                4,
                3,
                "Acompaña al hechizo Hacker.",
                "cards/createcard javi medio.jpg",
                CardRarity.RARE));
        register(new CreatureCard(
                "LIN",
                "Lin el Matador",
                8,
                6,
                4,
                "Kung Fu puede subir su ataque hasta 10.",
                "cards/createcard Lin.jpg",
                CardRarity.RARE));
        register(new CreatureCard(
                "QUEMO",
                "El Quemao",
                8,
                2,
                5,
                "Su fuego desgasta toda la mesa.",
                "cards/createcard quemao.jpg",
                CardRarity.RARE));
        register(new CreatureCard(
                "JAVI_B",
                "Javi B",
                8,
                5,
                2,
                "Otra vía para lanzar Hacker.",
                "cards/createcard javi medio.jpg",
                CardRarity.RARE));
        register(new CreatureCard(
                "FRAN_A",
                "Alonso",
                8,
                4,
                6,
                "Simeone protege al equipo durante una ronda.",
                "cards/createcard alonsoo.jpg",
                CardRarity.RARE));
        register(new CreatureCard(
                "CARMEN",
                "Carmen",
                8,
                5,
                6,
                "Una de las criaturas más sólidas del mazo.",
                "cards/createcard carmenn.jpg",
                CardRarity.RARE));
        register(new CreatureCard(
                "URBANO",
                "Urbano",
                3,
                3,
                1,
                "Sale rápido para presionar en los primeros turnos.",
                "cards/createcard urbano.jpg",
                CardRarity.COMMON));
        register(new CreatureCard(
                "INES",
                "Inés",
                2,
                2,
                2,
                "Criatura de apoyo para comenzar la partida.",
                "cards/createcard inees.jpg",
                CardRarity.COMMON));
        register(new CreatureCard(
                "ANTONIO",
                "Antonio",
                2,
                2,
                2,
                "Criatura equilibrada de apertura.",
                "cards/createcard antonioo.jpg",
                CardRarity.COMMON));
        register(new CreatureCard(
                "ADRIAN",
                "Adrian",
                1,
                1,
                3,
                "Miembro del combo de Baño.",
                "cards/createcard adrian.jpg",
                CardRarity.COMMON,
                false,
                true));
        register(new CreatureCard(
                "FABIO",
                "Fabio",
                1,
                1,
                3,
                "Miembro del combo de Baño.",
                "cards/createcard fabio.jpg",
                CardRarity.COMMON,
                false,
                true));
        register(new CreatureCard(
                "FERNANDO",
                "Fernando",
                1,
                1,
                3,
                "Miembro del combo de Baño.",
                "cards/createcard Fernando.jpg",
                CardRarity.COMMON,
                false,
                true));
        register(new CreatureCard(
                "MONO_A_TOKEN",
                "Mono A",
                0,
                3,
                2,
                "Ficha invocada por el hechizo Mono A.",
                null,
                CardRarity.COMMON));
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
                "Hechizo rojo: da +3 de ataque y +2 de vida. En Marco pega mucho más.",
                "cards/createcard Pandaa.jpg"));
        register(new SpellCard(
                "CARPAZO",
                "Carpazo",
                4,
                SpellEffect.ENEMY_BOARD_DAMAGE,
                SpellColor.RED,
                3,
                0,
                null,
                "Hace 3 de daño a todas las criaturas enemigas.",
                "cards/createcard carpazoo.jpg"));
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
                "cards/createcard enchino.jpg"));
        register(new SpellCard(
                "EL_GRITO",
                "El Grito",
                6,
                SpellEffect.ALL_BOARD_DAMAGE,
                SpellColor.RED,
                3,
                0,
                null,
                "Hace 3 de daño a todas las criaturas de ambos jugadores.",
                "cards/createcard elgrito.jpg"));
        register(new SpellCard(
                "MONO_A",
                "Mono F1",
                2,
                SpellEffect.SUMMON_MONO_A,
                SpellColor.BLUE,
                0,
                0,
                "MONO_A_TOKEN",
                "Invoca una ficha Mono A 3/2.",
                "cards/createcard monof1.jpg"));
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
                "cards/createcard scanner.jpg"));
        register(new SpellCard(
                "APUESTA",
                "Apuestas",
                2,
                SpellEffect.GAIN_MANA,
                SpellColor.BLUE,
                3,
                0,
                null,
                "Ganas 3 de maná actual y 3 de maná máximo.",
                "cards/createcard apuestas.jpg"));
        register(new SpellCard(
                "HUMO",
                "Humo",
                2,
                SpellEffect.SMOKE,
                SpellColor.BLUE,
                0,
                0,
                null,
                "La siguiente carta que juegues quedará oculta para el rival.",
                "cards/createcard Humoo.jpg"));
        register(new SpellCard(
                "HACKER",
                "Hackear",
                2,
                SpellEffect.STEAL_CARD,
                SpellColor.BLUE,
                0,
                0,
                null,
                "Roba una carta aleatoria de la mano rival.",
                "cards/createcard hackear.jpg"));
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
                "cards/createcard kung fu.jpg"));
        register(new SpellCard(
                "FUEGO",
                "Fueguito",
                3,
                SpellEffect.FIRE_PULSE,
                SpellColor.RED,
                1,
                0,
                null,
                "Hace 1 de daño a todas las criaturas del tablero.",
                "cards/createcard fueguitoo.jpg"));
        register(new SpellCard(
                "SIMEONE",
                "Simeone",
                3,
                SpellEffect.PROTECT_ALLIES,
                SpellColor.BLUE,
                0,
                0,
                null,
                "Tus criaturas no reciben daño enemigo durante la siguiente ronda rival.",
                "cards/createcard simeone.jpg"));
        register(new SpellCard(
                "BANO",
                "Enredaderas",
                2,
                SpellEffect.UNLOCK_SMOKE,
                SpellColor.GRAY,
                0,
                0,
                null,
                "Si Adrian, Fabio y Fernando están en mesa, desbloquea Humo para el resto de la partida.",
                "cards/createcard enredaderas.jpg"));
    }

    private static void register(Card card) {
        PROTOTYPES.put(card.getId(), card);
    }
}
