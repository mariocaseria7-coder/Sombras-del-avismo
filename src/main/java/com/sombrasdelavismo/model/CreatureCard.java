package com.sombrasdelavismo.model;

public class CreatureCard extends Card {
    private int attack;
    private int life;
    private int maxLife;
    private boolean canAttack;
    private boolean canDefend;
    private boolean tapped;
    private boolean summoningSickness;

    public CreatureCard(String name, int costMana, int attack, int life, String description, String imagePath) {
        super(name, costMana, description, imagePath);
        this.attack = attack;
        this.life = life;
        this.maxLife = life;
        this.canAttack = false;
        this.canDefend = true;
        this.tapped = false;
        this.summoningSickness = true;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = Math.max(0, life);
    }

    public int getMaxLife() {
        return maxLife;
    }

    public void setMaxLife(int maxLife) {
        this.maxLife = maxLife;
    }

    public boolean isCanAttack() {
        return canAttack;
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public boolean isCanDefend() {
        return canDefend;
    }

    public void setCanDefend(boolean canDefend) {
        this.canDefend = canDefend;
    }

    public boolean isTapped() {
        return tapped;
    }

    public void setTapped(boolean tapped) {
        this.tapped = tapped;
        this.canDefend = !tapped;
    }

    public boolean isSummoningSickness() {
        return summoningSickness;
    }

    public void setSummoningSickness(boolean summoningSickness) {
        this.summoningSickness = summoningSickness;
    }

    public void prepararInvocacion() {
        this.canAttack = false;
        this.canDefend = true;
        this.tapped = false;
        this.summoningSickness = true;
    }

    public void enderezarParaTurnoPropio() {
        if (summoningSickness) {
            summoningSickness = false;
            canAttack = true;
        } else {
            canAttack = true;
        }
        tapped = false;
        canDefend = true;
    }

    public void girarPorAtaque() {
        tapped = true;
        canAttack = false;
        canDefend = false;
    }

    public void atacar(CreatureCard objetivo) {
        if (objetivo != null) {
            objetivo.recibirDanio(attack);
        }
    }

    public void atacar(Player objetivo) {
        if (objetivo != null) {
            objetivo.receiveDamage(attack);
        }
    }

    public void defender(CreatureCard atacante) {
        if (atacante != null) {
            atacante.recibirDanio(attack);
        }
    }

    public void recibirDanio(int amount) {
        life = Math.max(0, life - amount);
    }

    public void restaurarVidaCompleta() {
        this.life = this.maxLife;
    }

    public boolean estaMuerta() {
        return life <= 0;
    }

    public void aplicarBuff(int attackBonus, int lifeBonus) {
        attack += attackBonus;
        maxLife += lifeBonus;
        life += lifeBonus;
    }

    @Override
    public void usar() {
        // La resolucion real se gestiona desde Game.
    }

    @Override
    public Card copy() {
        return new CreatureCard(getName(), getCostMana(), attack, maxLife, getDescription(), getImagePath());
    }

    @Override
    public String toString() {
        return getName() + " [" + attack + "/" + life + "] Mana " + getCostMana();
    }
}