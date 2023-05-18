package fr.kanpvp.copsplugin.utlis.randomdraw;

import org.bukkit.inventory.ItemStack;

public class EventLoot {

    private ItemStack item;
    private double probability;

    public EventLoot(ItemStack item, double probability) {
        this.item = item;
        this.probability = probability;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getProbability() {
        return probability;
    }




}
