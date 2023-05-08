package fr.kanpvp.copsplugin.utlis.randomdraw;

import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ManagerDraw {
    EventLoot[] eventsA = {
            new EventLoot("Event 1", null, 5),
            new EventLoot("Event 2", null, 75),
            new EventLoot("Event 3", null, 20)
    };

    private final Random random;

    public ManagerDraw() {
        random = new Random();
    }

    public boolean getRandomBoolean(int PROB_X) {
        double randomNumber = random.nextDouble() * 100;
        if (randomNumber < PROB_X) {
            return true;
        } else {
            return false;
        }
    }

    public ItemStack getRandomItem(EventLoot[] events) {
        double randomNumber = random.nextDouble() * 100;
        double cumulativeProbability = 0.0;
        for (int i = 0; i < events.length; i++) {
            cumulativeProbability += events[i].getProbability();
            if (randomNumber < cumulativeProbability) {
                return events[i].getItem();
            }
        }
        return null;
    }

}
