package com.sombrasdelavismo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
    public static final int INITIAL_HAND_SIZE = 4;
    public static final int BOARD_LIMIT = 7;
    public static final int MAX_MANA = 30;
    public static final int MANA_STEP = 5;

    private final Player player1;
    private final Player player2;
    private final Random random;
    private final List<String> log;
    private Player currentPlayer;
    private Player waitingPlayer;
    private int turnNumber;
    private int actionCounter;
    private String phase;
    private String lastAction;
    private Player winner;
    private boolean finished;

    public Game(Player player1, Player player2) {
        this(player1, player2, new Random());
    }

    public Game(Player player1, Player player2, Random random) {
        this.player1 = player1;
        this.player2 = player2;
        this.random = random;
        this.log = new ArrayList<>();
        this.currentPlayer = player1;
        this.waitingPlayer = player2;
        this.phase = "Preparación";
        this.lastAction = "La partida todavía no ha comenzado.";
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getWaitingPlayer() {
        return waitingPlayer;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public int getActionCounter() {
        return actionCounter;
    }

    public String getPhase() {
        return phase;
    }

    public String getLastAction() {
        return lastAction;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean isFinished() {
        return finished;
    }

    public List<String> getLog() {
        return Collections.unmodifiableList(log);
    }

    public ActionResult startGame() {
        if (player1 == null || player2 == null) {
            return ActionResult.failure("Error: los jugadores no están inicializados.");
        }
        resetPlayer(player1);
        resetPlayer(player2);
        finished = false;
        winner = null;
        lastAction = "La partida acaba de empezar.";

        for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
            if (!drawOpeningCard(player1) || !drawOpeningCard(player2)) {
                return ActionResult.success(lastAction);
            }
        }

        currentPlayer = player1;
        waitingPlayer = player2;
        turnNumber = 1;
        actionCounter = 0;
        log.clear();
        return startTurn();
    }

    public ActionResult nextTurn() {
        if (currentPlayer == null || waitingPlayer == null) {
            return ActionResult.failure("Error crítico: los jugadores no están inicializados.");
        }
        if (finished) {
            return ActionResult.failure("La partida ya terminó.");
        }

        currentPlayer.setRevealOpponentHand(false);
        Player previousCurrent = currentPlayer;
        currentPlayer = waitingPlayer;
        waitingPlayer = previousCurrent;
        turnNumber++;
        actionCounter = 0;
        return startTurn();
    }

    public boolean canPlaySelectedCard(Card card) {
        return explainWhyCannotPlay(card) == null;
    }

    public String explainWhyCannotPlay(Card card) {
        if (finished) {
            return "La partida ya terminó.";
        }
        if (card == null) {
            return "Selecciona una carta de tu mano.";
        }
        if (currentPlayer == null || !currentPlayer.getHand().contains(card)) {
            return "Solo puedes jugar cartas que esten en tu mano.";
        }
        if (currentPlayer.getCurrentMana() < card.getManaCost()) {
            return "Necesitas " + card.getManaCost() + " de maná y solo tienes "
                    + currentPlayer.getCurrentMana() + ".";
        }
        if (card instanceof CreatureCard) {
            if (currentPlayer.getBattlefield().size() >= BOARD_LIMIT) {
                return "Tu tablero ya está lleno. El límite es de " + BOARD_LIMIT + " criaturas.";
            }
            return null;
        }
        return explainSpellRestriction((SpellCard) card);
    }

    public ActionResult playCard(Card card) {
        return playCard(card, null);
    }

    public ActionResult playCard(Card card, CreatureCard target) {
        if (finished) {
            return ActionResult.failure("La partida ya terminó.");
        }
        if (card == null) {
            return ActionResult.failure("Selecciona una carta.");
        }
        if (currentPlayer == null || !currentPlayer.getHand().contains(card)) {
            return ActionResult.failure("Solo puedes jugar cartas de tu mano.");
        }
        String playRestriction = explainWhyCannotPlay(card);
        if (playRestriction != null) {
            return ActionResult.failure(playRestriction);
        }

        if (card instanceof SpellCard spellCard && spellCard.requiresFriendlyTarget()) {
            if (target == null || !currentPlayer.getBattlefield().contains(target)) {
                return ActionResult.failure("Ese hechizo necesita una criatura propia como objetivo.");
            }
        }

        if (!currentPlayer.spendMana(card.getManaCost())) {
            return ActionResult.failure("No tienes maná suficiente.");
        }

        currentPlayer.removeFromHand(card);
        actionCounter++;

        if (card instanceof CreatureCard creatureCard) {
            return playCreature(creatureCard);
        }

        SpellCard spellCard = (SpellCard) card;
        boolean hiddenBySmoke = currentPlayer.isHideNextPlay();
        currentPlayer.setHideNextPlay(false);
        ActionResult result = resolveSpell(spellCard, target, hiddenBySmoke);
        currentPlayer.getDeck().sendToGraveyard(spellCard);
        return result;
    }

    public boolean canAttackWith(CreatureCard attacker) {
        return explainWhyCannotAttack(attacker) == null;
    }

    public String explainWhyCannotAttack(CreatureCard attacker) {
        if (finished) {
            return "La partida ya terminó.";
        }
        if (attacker == null) {
            return "Selecciona una criatura de tu campo.";
        }
        if (!currentPlayer.getBattlefield().contains(attacker)) {
            return "La criatura debe estar en tu campo para poder atacar.";
        }
        if (attacker.getHealth() <= 0) {
            return attacker.getName() + " ya no está disponible para combatir.";
        }
        if (attacker.hasSummoningSickness()) {
            return attacker.getName() + " tiene mareo de invocación y no puede atacar este turno.";
        }
        if (attacker.isExhausted()) {
            return attacker.getName() + " ya está girado y no puede atacar ahora.";
        }
        return null;
    }

    public List<CreatureCard> getAvailableAttackers() {
        List<CreatureCard> attackers = new ArrayList<>();
        if (currentPlayer == null) {
            return attackers;
        }
        for (CreatureCard creature : currentPlayer.getBattlefield()) {
            if (creature != null && creature.canAttack()) {
                attackers.add(creature);
            }
        }
        return attackers;
    }

    public List<CreatureCard> getAvailableBlockers() {
        List<CreatureCard> blockers = new ArrayList<>();
        if (waitingPlayer == null) {
            return blockers;
        }
        for (CreatureCard creature : waitingPlayer.getBattlefield()) {
            if (creature != null && creature.canDefend()) {
                blockers.add(creature);
            }
        }
        return blockers;
    }

    public ActionResult attackPlayer(CreatureCard attacker, CreatureCard blocker) {
        if (attacker == null) {
            return ActionResult.failure("Selecciona una criatura para atacar.");
        }
        String attackRestriction = explainWhyCannotAttack(attacker);
        if (attackRestriction != null) {
            return ActionResult.failure(attackRestriction);
        }
        if (blocker != null && !waitingPlayer.getBattlefield().contains(blocker)) {
            return ActionResult.failure("El defensor elegido no está en el tablero rival.");
        }

        phase = "Combate";
        actionCounter++;
        attacker.exhaust();
        attacker.reveal();

        if (blocker == null) {
            waitingPlayer.receiveDamage(attacker.getAttack());
            ActionResult victory = updateWinnerByLife();
            if (victory != null) {
                return victory;
            }
            return recordSuccess(
                    currentPlayer.getName() + " ataca directamente con " + attacker.getName()
                            + " y hace " + attacker.getAttack() + " de daño a " + waitingPlayer.getName()
                            + ", que baja a " + waitingPlayer.getLife() + " de vida.");
        }

        blocker.reveal();
        return resolveCombat(
                attacker,
                blocker,
                currentPlayer.getName() + " ataca con " + attacker.getName()
                        + " y " + waitingPlayer.getName() + " se defiende con " + blocker.getName() + ".");
    }

    public ActionResult attackCreature(CreatureCard attacker, CreatureCard defender) {
        if (attacker == null) {
            return ActionResult.failure("Selecciona una criatura para atacar.");
        }
        String attackRestriction = explainWhyCannotAttack(attacker);
        if (attackRestriction != null) {
            return ActionResult.failure(attackRestriction);
        }
        if (defender == null || !waitingPlayer.getBattlefield().contains(defender)) {
            return ActionResult.failure("Debes seleccionar una criatura rival.");
        }

        phase = "Combate";
        actionCounter++;
        attacker.exhaust();
        attacker.reveal();
        defender.reveal();

        return resolveCombat(
                attacker,
                defender,
                currentPlayer.getName() + " hace combatir a " + attacker.getName()
                        + " contra " + defender.getName() + ".");
    }

    private ActionResult playCreature(CreatureCard creatureCard) {
        creatureCard.markSummoned();
        boolean hidden = currentPlayer.isHideNextPlay();
        currentPlayer.setHideNextPlay(false);
        if (hidden) {
            creatureCard.setHiddenFromOpponent(true);
        }
        currentPlayer.addToBattlefield(creatureCard);

        if (hidden) {
            return recordSuccess(
                    withManaInfo(currentPlayer.getName() + " deja una criatura oculta envuelta en humo."),
                    "La criatura oculta era " + creatureCard.getName() + " [" + creatureCard.getAttack()
                            + "/" + creatureCard.getHealth() + "].");
        }

        return recordSuccess(withManaInfo(
                currentPlayer.getName() + " invoca a " + creatureCard.getName() + " ["
                        + creatureCard.getAttack() + "/" + creatureCard.getHealth() + "]."));
    }

    private ActionResult resolveSpell(SpellCard spellCard, CreatureCard target, boolean hiddenBySmoke) {
        if (spellCard == null) {
            return ActionResult.failure("Error: hechizo inválido.");
        }
        String publicName = hiddenBySmoke ? "una carta oculta" : spellCard.getName();
        String privateMessage = null;

        switch (spellCard.getEffect()) {
            case BUFF -> {
                if (target == null) {
                    return ActionResult.failure("Error: objetivo inválido para el hechizo.");
                }
                int attackBonus = spellCard.getPrimaryValue() + spellCard.getColor().getAttackBonus();
                int healthBonus = spellCard.getSecondaryValue();
                if ("MARCO".equals(target.getId())) {
                    attackBonus += 5;
                }
                target.addStats(attackBonus, healthBonus);
                return recordSuccess(withManaInfo(
                        currentPlayer.getName() + " lanza " + publicName + " sobre " + target.getName()
                                + " y le da +" + attackBonus + "/+" + healthBonus
                                + ". Ahora queda en " + target.getAttack() + "/" + target.getHealth() + "."),
                        hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case ENEMY_BOARD_DAMAGE -> {
                applyDamageToBoard(waitingPlayer, spellCard.getPrimaryValue(), true);
                List<String> deadCreatures = cleanupDeadCreatures();
                return recordSuccess(withManaInfo(withDeaths(
                        currentPlayer.getName() + " usa " + publicName + " y golpea toda la mesa rival por "
                                + spellCard.getPrimaryValue() + ".",
                        deadCreatures)), hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case ALL_BOARD_DAMAGE -> {
                applyDamageToBoard(currentPlayer, spellCard.getPrimaryValue(), false);
                applyDamageToBoard(waitingPlayer, spellCard.getPrimaryValue(), true);
                List<String> deadCreatures = cleanupDeadCreatures();
                return recordSuccess(withManaInfo(withDeaths(
                        currentPlayer.getName() + " desata " + publicName + " y todo el tablero recibe "
                                + spellCard.getPrimaryValue() + " de daño.",
                        deadCreatures)), hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case SUMMON_LIN -> {
                if (currentPlayer.getBattlefield().size() >= BOARD_LIMIT) {
                    return ActionResult.failure("No puedes invocar a Lin porque tu tablero ya está lleno.");
                }
                CreatureCard lin = CardCatalog.createCreature("LIN");
                lin.markSummoned();
                currentPlayer.addToBattlefield(lin);
                return recordSuccess(
                        withManaInfo(currentPlayer.getName() + " lanza " + publicName
                                + " y saca a Lin al tablero [6/4]."),
                        hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case SUMMON_MONO_A -> {
                if (currentPlayer.getBattlefield().size() >= BOARD_LIMIT) {
                    return ActionResult.failure("No puedes invocar más criaturas porque tu tablero ya está lleno.");
                }
                CreatureCard mono = CardCatalog.createCreature(spellCard.getRelatedCardId());
                if (mono == null) {
                    return ActionResult.failure("Error al invocar criatura: ID inválido.");
                }
                mono.markSummoned();
                currentPlayer.addToBattlefield(mono);
                return recordSuccess(
                        withManaInfo(currentPlayer.getName() + " lanza " + publicName + " e invoca a "
                                + mono.getName() + " [" + mono.getAttack() + "/" + mono.getHealth() + "]."),
                        hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case REVEAL_HAND -> {
                currentPlayer.setRevealOpponentHand(true);
                privateMessage = waitingPlayer.getName() + " tiene en mano: " + waitingPlayer.describeHand();
                return recordSuccess(
                        withManaInfo(currentPlayer.getName() + " activa " + publicName + " y examina la mano rival."),
                        privateMessage);
            }
            case GAIN_MANA -> {
                currentPlayer.boostMana(spellCard.getPrimaryValue(), MAX_MANA);
                return recordSuccess(
                        currentPlayer.getName() + " juega " + publicName + " y gana "
                                + spellCard.getPrimaryValue() + " de maná extra. Ahora queda con "
                                + currentPlayer.getCurrentMana() + "/" + currentPlayer.getMaxMana() + " de maná.",
                        hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case SMOKE -> {
                currentPlayer.setHideNextPlay(true);
                return recordSuccess(withManaInfo(
                        currentPlayer.getName() + " prepara una cortina de humo para ocultar su siguiente jugada."));
            }
            case STEAL_CARD -> {
                if (waitingPlayer.getHand().isEmpty()) {
                    return recordSuccess(
                            withManaInfo(currentPlayer.getName() + " usa " + publicName
                                    + " pero el rival no tenia cartas."));
                }
                Card stolen = stealRandomCard();
                if (stolen == null) {
                    return recordSuccess(
                            withManaInfo(currentPlayer.getName() + " usa " + publicName
                                    + " pero no pudo robar ninguna carta."));
                }
                privateMessage = "Carta robada: " + stolen.getName() + ".";
                return recordSuccess(
                        withManaInfo(currentPlayer.getName() + " usa " + publicName + " y roba una carta del rival."),
                        privateMessage);
            }
            case SET_ATTACK_TO_TEN -> {
                if (target == null) {
                    return ActionResult.failure("Error: objetivo inválido para el hechizo.");
                }
                int attackBefore = target.getAttack();
                target.setAttackToAtLeast(spellCard.getPrimaryValue());
                return recordSuccess(
                        withManaInfo(
                        currentPlayer.getName() + " lanza " + publicName + " y sube a " + target.getName()
                                + " de " + attackBefore + " a " + target.getAttack() + " de ataque."),
                        hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case FIRE_PULSE -> {
                applyDamageToBoard(currentPlayer, spellCard.getPrimaryValue(), false);
                applyDamageToBoard(waitingPlayer, spellCard.getPrimaryValue(), true);
                List<String> deadCreatures = cleanupDeadCreatures();
                return recordSuccess(withManaInfo(withDeaths(
                        currentPlayer.getName() + " libera " + publicName + " y cada criatura recibe "
                                + spellCard.getPrimaryValue() + " de daño.",
                        deadCreatures)), hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case PROTECT_ALLIES -> {
                currentPlayer.setAlliesProtected(true);
                return recordSuccess(
                        withManaInfo(currentPlayer.getName() + " usa " + publicName
                                + " y protege a todas sus criaturas durante la siguiente ronda rival."),
                        hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case UNLOCK_SMOKE -> {
                currentPlayer.setSmokeUnlocked(true);
                return recordSuccess(
                        withManaInfo(currentPlayer.getName() + " juega " + publicName
                                + " y desbloquea Humo para el resto de la partida."));
            }
            default -> {
                return ActionResult.failure("Ese hechizo aún no tiene implementación.");
            }
        }
    }

    private ActionResult resolveCombat(CreatureCard attacker, CreatureCard defender, String intro) {
        if (attacker == null || defender == null) {
            return ActionResult.failure("Error: una de las criaturas en combate es inválida.");
        }
        int damageToDefender = dealDamageToCreature(waitingPlayer, defender, attacker.getAttack(), true);
        int damageToAttacker = dealDamageToCreature(currentPlayer, attacker, defender.getAttack(), true);
        List<String> deadCreatures = cleanupDeadCreatures();
        String combatDetails = intro + " "
                + describeDamage(attacker.getName(), defender.getName(), damageToDefender)
                + " " + describeDamage(defender.getName(), attacker.getName(), damageToAttacker)
                + " Vida restante: " + attacker.getName() + "=" + attacker.getHealth()
                + ", " + defender.getName() + "=" + defender.getHealth() + ".";
        return recordSuccess(withDeaths(combatDetails, deadCreatures));
    }

    private void applyDamageToBoard(Player player, int amount, boolean fromEnemy) {
        if (player == null || amount <= 0) {
            return;
        }
        for (CreatureCard creature : new ArrayList<>(player.getBattlefield())) {
            if (creature != null) {
                dealDamageToCreature(player, creature, amount, fromEnemy);
            }
        }
    }

    private int dealDamageToCreature(Player owner, CreatureCard creature, int damage, boolean fromEnemy) {
        if (damage < 0) {
            damage = 0;
        }
        if (creature == null || owner == null) {
            return 0;
        }
        if (damage <= 0) {
            return 0;
        }
        if (fromEnemy && owner.isAlliesProtected()) {
            return 0;
        }
        creature.reveal();
        return creature.receiveDamage(damage);
    }

    private List<String> cleanupDeadCreatures() {
        List<String> deadCreatures = new ArrayList<>();
        moveDeadCreatures(player1, deadCreatures);
        moveDeadCreatures(player2, deadCreatures);
        return deadCreatures;
    }

    private void moveDeadCreatures(Player player, List<String> deadCreatures) {
        if (player == null || deadCreatures == null) {
            return;
        }
        List<CreatureCard> toRemove = new ArrayList<>();
        for (CreatureCard creature : player.getBattlefield()) {
            if (creature != null && creature.isDead()) {
                toRemove.add(creature);
            }
        }
        for (CreatureCard dead : toRemove) {
            player.removeFromBattlefield(dead);
            player.getDeck().sendToGraveyard(dead);
            if (dead.getName() != null) {
                deadCreatures.add(dead.getName());
            }
        }
    }

    private Card stealRandomCard() {
        List<Card> rivalHand = new ArrayList<>(waitingPlayer.getHand());
        if (rivalHand.isEmpty()) {
            return null;
        }
        Card stolen = rivalHand.get(random.nextInt(rivalHand.size()));
        waitingPlayer.removeFromHand(stolen);
        currentPlayer.addCardToHand(stolen);
        return stolen;
    }

    private String explainSpellRestriction(SpellCard spellCard) {
        if (spellCard == null) {
            return "Ese hechizo es inválido.";
        }
        if (spellCard.getEffect() == SpellEffect.BUFF || spellCard.getEffect() == SpellEffect.SET_ATTACK_TO_TEN) {
            if (currentPlayer.getBattlefield().isEmpty()) {
                return "Ese hechizo necesita una criatura propia en el tablero.";
            }
            return null;
        }
        if ((spellCard.getEffect() == SpellEffect.SUMMON_LIN || spellCard.getEffect() == SpellEffect.SUMMON_MONO_A)
                && currentPlayer.getBattlefield().size() >= BOARD_LIMIT) {
            return "No puedes invocar más criaturas porque tu tablero ya está lleno.";
        }
        if (spellCard.getEffect() == SpellEffect.SMOKE && !currentPlayer.isSmokeUnlocked()) {
            return "Humo está bloqueado. Antes debes usar Baño con Adrian, Fabio y Fernando en tu campo.";
        }
        if (spellCard.getEffect() == SpellEffect.UNLOCK_SMOKE && !currentPlayer.hasBathroomCrewOnBoard()) {
            return "Baño solo funciona si Adrian, Fabio y Fernando están en tu campo.";
        }
        return null;
    }

    private void resetPlayer(Player player) {
        player.resetForNewGame();
        player.shuffleDeck();
    }

    private boolean drawOpeningCard(Player player) {
        Card drawnCard = player.drawCard();
        if (drawnCard == null) {
            loseByFatigue(player);
            return false;
        }
        return true;
    }

    private ActionResult startTurn() {
        if (currentPlayer == null || waitingPlayer == null) {
            return ActionResult.failure("Error crítico: los jugadores no están inicializados.");
        }
        
        phase = "Inicio";
        currentPlayer.setAlliesProtected(false);
        currentPlayer.setRevealOpponentHand(false);
        currentPlayer.increaseMaxMana(MANA_STEP, MAX_MANA);
        currentPlayer.refillMana();

        for (CreatureCard creature : new ArrayList<>(currentPlayer.getBattlefield())) {
            if (creature != null) {
                creature.startOwnerTurn();
            }
        }

        Card drawnCard = currentPlayer.drawCard();
        if (drawnCard == null) {
            return loseByFatigue(currentPlayer);
        }

        phase = "Acción";
        return recordSuccess(
                currentPlayer.getName() + " empieza el turno " + turnNumber + ", roba " + drawnCard.getName()
                        + " y queda con " + currentPlayer.getCurrentMana() + "/" + currentPlayer.getMaxMana() + " de maná.");
    }

    private ActionResult updateWinnerByLife() {
        if (player1.getLife() <= 0) {
            finished = true;
            winner = player2;
            return recordSuccess(player2.getName() + " gana porque " + player1.getName() + " se quedo sin vida.");
        }
        if (player2.getLife() <= 0) {
            finished = true;
            winner = player1;
            return recordSuccess(player1.getName() + " gana porque " + player2.getName() + " se quedo sin vida.");
        }
        return null;
    }

    private ActionResult loseByFatigue(Player player) {
        finished = true;
        winner = player == player1 ? player2 : player1;
        return recordSuccess(player.getName() + " pierde por fatiga mágica al intentar robar con el mazo vacío.");
    }

    private ActionResult recordSuccess(String publicMessage) {
        return recordSuccess(publicMessage, null);
    }

    private ActionResult recordSuccess(String publicMessage, String privateMessage) {
        lastAction = publicMessage;
        log.add(publicMessage);
        return ActionResult.success(publicMessage, privateMessage);
    }

    private String withManaInfo(String message) {
        if (message == null) {
            message = "";
        }
        if (currentPlayer == null) {
            return message;
        }
        return message + " Maná restante: " + currentPlayer.getCurrentMana() + "/" + currentPlayer.getMaxMana() + ".";
    }

    private String describeDamage(String sourceName, String targetName, int damage) {
        if (sourceName == null) {
            sourceName = "Una criatura";
        }
        if (targetName == null) {
            targetName = "su objetivo";
        }
        if (damage <= 0) {
            return sourceName + " no consigue hacer daño a " + targetName + ".";
        }
        return sourceName + " hace " + damage + " de daño a " + targetName + ".";
    }

    private String withDeaths(String baseMessage, List<String> deadCreatures) {
        if (baseMessage == null) {
            baseMessage = "";
        }
        if (deadCreatures == null || deadCreatures.isEmpty()) {
            return baseMessage;
        }
        return baseMessage + " Mueren: " + String.join(", ", deadCreatures) + ".";
    }
}
