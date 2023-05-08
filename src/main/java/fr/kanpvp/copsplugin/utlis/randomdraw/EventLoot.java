package fr.kanpvp.copsplugin.utlis.randomdraw;

import org.bukkit.inventory.ItemStack;

public class EventLoot {

    private String name;
    private ItemStack item;
    private double probability;

    public EventLoot(String name, ItemStack item, double probability) {
        this.name = name;
        this.item = item;
        this.probability = probability;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getProbability() {
        return probability;
    }


}
