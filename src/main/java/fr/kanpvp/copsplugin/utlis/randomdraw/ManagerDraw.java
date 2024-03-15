package fr.kanpvp.copsplugin.utlis.randomdraw;

import me.deecaad.weaponmechanics.WeaponMechanicsAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ManagerDraw {
    public static EventLoot[] type1Loot = {
            new EventLoot(itemGui(Material.REDSTONE, "SANG", null), 5),
            new EventLoot(itemGui(Material.LEATHER_HORSE_ARMOR, "ap_pistol_36", null), 75),
            new EventLoot(null, 20)
    };
    public static EventLoot[] type2Loot = {
            new EventLoot(null, 5),
            new EventLoot(null, 75),
            new EventLoot(null, 20)
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

    public List<ItemStack> getRandomItems(EventLoot[] events) {
        List<ItemStack> listItems = new ArrayList<ItemStack>();
        for (int i = 0; i < events.length; i++) {
            double probability = events[i].getProbability();
            if(this.getRandomBoolean((int) probability)){
                if(events[i].getItem() != null){
                    if(events[i].getItem().getType().equals(Material.LEATHER_HORSE_ARMOR)){  // Convert My Leather_Horse_Armor in Weapon
                        String weaponName = events[i].getItem().getItemMeta().getDisplayName();
                        listItems.add(WeaponMechanicsAPI.generateWeapon(weaponName));
                    } else {
                        listItems.add(events[i].getItem());

                    }
                }
            }
        }
        return listItems;
    }

    public static ItemStack itemGui(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack itemGui(Material material, String name, List<String> lore, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }



}
