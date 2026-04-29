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
        this.phase = "Preparacion";
        this.lastAction = "La partida todavia no ha comenzado.";
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
        if (finished) {
            return ActionResult.failure("La partida ya termino.");
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
        if (finished || card == null || !currentPlayer.getHand().contains(card) || !currentPlayer.canPlay(card)) {
            return false;
        }

        if (card instanceof CreatureCard) {
            return currentPlayer.getBattlefield().size() < BOARD_LIMIT;
        }

        SpellCard spellCard = (SpellCard) card;
        return canPlaySpell(spellCard);
    }

    public ActionResult playCard(Card card) {
        return playCard(card, null);
    }

    public ActionResult playCard(Card card, CreatureCard target) {
        if (finished) {
            return ActionResult.failure("La partida ya termino.");
        }
        if (card == null) {
            return ActionResult.failure("Selecciona una carta.");
        }
        if (!currentPlayer.getHand().contains(card)) {
            return ActionResult.failure("Solo puedes jugar cartas de tu mano.");
        }
        if (!canPlaySelectedCard(card)) {
            return ActionResult.failure("No puedes jugar esa carta ahora mismo.");
        }

        if (card instanceof SpellCard spellCard && spellCard.requiresFriendlyTarget()) {
            if (target == null || !currentPlayer.getBattlefield().contains(target)) {
                return ActionResult.failure("Ese hechizo necesita una criatura propia como objetivo.");
            }
        }

        if (!currentPlayer.spendMana(card.getManaCost())) {
            return ActionResult.failure("No tienes mana suficiente.");
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
        return !finished
                && attacker != null
                && currentPlayer.getBattlefield().contains(attacker)
                && attacker.canAttack();
    }

    public List<CreatureCard> getAvailableAttackers() {
        List<CreatureCard> attackers = new ArrayList<>();
        for (CreatureCard creature : currentPlayer.getBattlefield()) {
            if (creature.canAttack()) {
                attackers.add(creature);
            }
        }
        return attackers;
    }

    public List<CreatureCard> getAvailableBlockers() {
        List<CreatureCard> blockers = new ArrayList<>();
        for (CreatureCard creature : waitingPlayer.getBattlefield()) {
            if (creature.canDefend()) {
                blockers.add(creature);
            }
        }
        return blockers;
    }

    public ActionResult attackPlayer(CreatureCard attacker, CreatureCard blocker) {
        if (!canAttackWith(attacker)) {
            return ActionResult.failure("La criatura seleccionada no puede atacar.");
        }
        if (blocker != null && !waitingPlayer.getBattlefield().contains(blocker)) {
            return ActionResult.failure("El defensor elegido no esta en el tablero rival.");
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
                            + " y hace " + attacker.getAttack() + " de danio a " + waitingPlayer.getName() + ".");
        }

        blocker.reveal();
        return resolveCombat(
                attacker,
                blocker,
                currentPlayer.getName() + " ataca con " + attacker.getName()
                        + " y " + waitingPlayer.getName() + " se defiende con " + blocker.getName() + ".");
    }

    public ActionResult attackCreature(CreatureCard attacker, CreatureCard defender) {
        if (!canAttackWith(attacker)) {
            return ActionResult.failure("La criatura atacante no esta lista.");
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
                    currentPlayer.getName() + " deja una criatura oculta envuelta en humo.",
                    "La criatura oculta era " + creatureCard.getName() + ".");
        }

        return recordSuccess(currentPlayer.getName() + " invoca a " + creatureCard.getName() + ".");
    }

    private ActionResult resolveSpell(SpellCard spellCard, CreatureCard target, boolean hiddenBySmoke) {
        String publicName = hiddenBySmoke ? "una carta oculta" : spellCard.getName();
        String privateMessage = null;

        switch (spellCard.getEffect()) {
            case BUFF -> {
                int attackBonus = spellCard.getPrimaryValue() + spellCard.getColor().getAttackBonus();
                int healthBonus = spellCard.getSecondaryValue();
                if ("MARCO".equals(target.getId())) {
                    attackBonus += 5;
                }
                target.addStats(attackBonus, healthBonus);
                return recordSuccess(
                        currentPlayer.getName() + " lanza " + publicName + " sobre " + target.getName()
                                + " y le da +" + attackBonus + "/+" + healthBonus + ".",
                        hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case ENEMY_BOARD_DAMAGE -> {
                applyDamageToBoard(waitingPlayer, spellCard.getPrimaryValue(), true);
                List<String> deadCreatures = cleanupDeadCreatures();
                return recordSuccess(withDeaths(
                        currentPlayer.getName() + " usa " + publicName + " y golpea toda la mesa rival por "
                                + spellCard.getPrimaryValue() + ".",
                        deadCreatures), hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case ALL_BOARD_DAMAGE -> {
                applyDamageToBoard(currentPlayer, spellCard.getPrimaryValue(), false);
                applyDamageToBoard(waitingPlayer, spellCard.getPrimaryValue(), true);
                List<String> deadCreatures = cleanupDeadCreatures();
                return recordSuccess(withDeaths(
                        currentPlayer.getName() + " desata " + publicName + " y todo el tablero recibe "
                                + spellCard.getPrimaryValue() + " de danio.",
                        deadCreatures), hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case SUMMON_LIN -> {
                CreatureCard lin = CardCatalog.createCreature("LIN");
                lin.markSummoned();
                currentPlayer.addToBattlefield(lin);
                return recordSuccess(
                        currentPlayer.getName() + " lanza " + publicName + " y saca a Lin al tablero.",
                        hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case SUMMON_MONO_A -> {
                CreatureCard mono = CardCatalog.createCreature(spellCard.getRelatedCardId());
                mono.markSummoned();
                currentPlayer.addToBattlefield(mono);
                return recordSuccess(
                        currentPlayer.getName() + " lanza " + publicName + " e invoca a " + mono.getName() + ".",
                        hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case REVEAL_HAND -> {
                currentPlayer.setRevealOpponentHand(true);
                privateMessage = waitingPlayer.getName() + " tiene en mano: " + waitingPlayer.describeHand();
                return recordSuccess(
                        currentPlayer.getName() + " activa " + publicName + " y examina la mano rival.",
                        privateMessage);
            }
            case GAIN_MANA -> {
                currentPlayer.boostMana(spellCard.getPrimaryValue(), MAX_MANA);
                return recordSuccess(
                        currentPlayer.getName() + " juega " + publicName + " y gana "
                                + spellCard.getPrimaryValue() + " de mana extra.",
                        hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case SMOKE -> {
                currentPlayer.setHideNextPlay(true);
                return recordSuccess(currentPlayer.getName() + " prepara una cortina de humo.");
            }
            case STEAL_CARD -> {
                if (waitingPlayer.getHand().isEmpty()) {
                    return recordSuccess(
                            currentPlayer.getName() + " usa " + publicName + " pero el rival no tenia cartas.");
                }
                Card stolen = stealRandomCard();
                privateMessage = "Carta robada: " + stolen.getName() + ".";
                return recordSuccess(
                        currentPlayer.getName() + " usa " + publicName + " y roba una carta del rival.",
                        privateMessage);
            }
            case SET_ATTACK_TO_TEN -> {
                int attackBefore = target.getAttack();
                target.setAttackToAtLeast(spellCard.getPrimaryValue());
                return recordSuccess(
                        currentPlayer.getName() + " lanza " + publicName + " y sube a " + target.getName()
                                + " de " + attackBefore + " a " + target.getAttack() + " de ataque.",
                        hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case FIRE_PULSE -> {
                applyDamageToBoard(currentPlayer, spellCard.getPrimaryValue(), false);
                applyDamageToBoard(waitingPlayer, spellCard.getPrimaryValue(), true);
                List<String> deadCreatures = cleanupDeadCreatures();
                return recordSuccess(withDeaths(
                        currentPlayer.getName() + " libera " + publicName + " y cada criatura recibe "
                                + spellCard.getPrimaryValue() + " de danio.",
                        deadCreatures), hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case PROTECT_ALLIES -> {
                currentPlayer.setAlliesProtected(true);
                return recordSuccess(
                        currentPlayer.getName() + " usa " + publicName
                                + " y protege a todas sus criaturas durante la siguiente ronda rival.",
                        hiddenBySmoke ? "La carta oculta era " + spellCard.getName() + "." : null);
            }
            case UNLOCK_SMOKE -> {
                currentPlayer.setSmokeUnlocked(true);
                return recordSuccess(
                        currentPlayer.getName() + " juega " + publicName + " y desbloquea Humo para el resto de la partida.");
            }
            default -> {
                return ActionResult.failure("Ese hechizo aun no tiene implementacion.");
            }
        }
    }

    private ActionResult resolveCombat(CreatureCard attacker, CreatureCard defender, String intro) {
        dealDamageToCreature(waitingPlayer, defender, attacker.getAttack(), true);
        dealDamageToCreature(currentPlayer, attacker, defender.getAttack(), true);
        List<String> deadCreatures = cleanupDeadCreatures();
        return recordSuccess(withDeaths(intro, deadCreatures));
    }

    private void applyDamageToBoard(Player player, int amount, boolean fromEnemy) {
        for (CreatureCard creature : new ArrayList<>(player.getBattlefield())) {
            dealDamageToCreature(player, creature, amount, fromEnemy);
        }
    }

    private int dealDamageToCreature(Player owner, CreatureCard creature, int damage, boolean fromEnemy) {
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
        List<CreatureCard> toRemove = new ArrayList<>();
        for (CreatureCard creature : player.getBattlefield()) {
            if (creature.isDead()) {
                toRemove.add(creature);
            }
        }
        for (CreatureCard dead : toRemove) {
            player.removeFromBattlefield(dead);
            player.getDeck().sendToGraveyard(dead);
            deadCreatures.add(dead.getName());
        }
    }

    private Card stealRandomCard() {
        List<Card> rivalHand = new ArrayList<>(waitingPlayer.getHand());
        Card stolen = rivalHand.get(random.nextInt(rivalHand.size()));
        waitingPlayer.removeFromHand(stolen);
        currentPlayer.addCardToHand(stolen);
        return stolen;
    }

    private boolean canPlaySpell(SpellCard spellCard) {
        if (spellCard.getEffect() == SpellEffect.BUFF || spellCard.getEffect() == SpellEffect.SET_ATTACK_TO_TEN) {
            return !currentPlayer.getBattlefield().isEmpty();
        }
        if ((spellCard.getEffect() == SpellEffect.SUMMON_LIN || spellCard.getEffect() == SpellEffect.SUMMON_MONO_A)
                && currentPlayer.getBattlefield().size() >= BOARD_LIMIT) {
            return false;
        }
        if (spellCard.getEffect() == SpellEffect.SMOKE && !currentPlayer.isSmokeUnlocked()) {
            return false;
        }
        if (spellCard.getEffect() == SpellEffect.UNLOCK_SMOKE && !currentPlayer.hasBathroomCrewOnBoard()) {
            return false;
        }
        return true;
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
        phase = "Inicio";
        currentPlayer.setAlliesProtected(false);
        currentPlayer.setRevealOpponentHand(false);
        currentPlayer.increaseMaxMana(MANA_STEP, MAX_MANA);
        currentPlayer.refillMana();

        for (CreatureCard creature : currentPlayer.getBattlefield()) {
            creature.startOwnerTurn();
        }

        Card drawnCard = currentPlayer.drawCard();
        if (drawnCard == null) {
            return loseByFatigue(currentPlayer);
        }

        phase = "Accion";
        return recordSuccess(
                currentPlayer.getName() + " empieza el turno " + turnNumber + ", roba " + drawnCard.getName()
                        + " y queda con " + currentPlayer.getCurrentMana() + "/" + currentPlayer.getMaxMana() + " de mana.");
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
        return recordSuccess(player.getName() + " pierde por fatiga magica al intentar robar con el mazo vacio.");
    }

    private ActionResult recordSuccess(String publicMessage) {
        return recordSuccess(publicMessage, null);
    }

    private ActionResult recordSuccess(String publicMessage, String privateMessage) {
        lastAction = publicMessage;
        log.add(publicMessage);
        return ActionResult.success(publicMessage, privateMessage);
    }

    private String withDeaths(String baseMessage, List<String> deadCreatures) {
        if (deadCreatures.isEmpty()) {
            return baseMessage;
        }
        return baseMessage + " Mueren: " + String.join(", ", deadCreatures) + ".";
    }
}
