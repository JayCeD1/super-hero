package org.erenes;

public class SuperHeroesLoad {

    public static void main(String[] args) {
        Thread heroScenario = new Thread(new HeroScenario());
        heroScenario.start();
        Thread villainScenario = new Thread(new VillainScenario());
        villainScenario.start();
        Thread fightScenario = new Thread(new FightScenario());
        fightScenario.start();
    }
}
