package fr.kanpvp.copsplugin.cops;


import fr.kanpvp.copsplugin.CopsPlugin;
import me.deecaad.weaponmechanics.WeaponMechanicsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Cops {
    private static final Map<Integer, List<List<CopsRole>>> copsGroups = Cops.createCopsGroup();

    public static Map<Integer, List<List<CopsRole>>> createCopsGroup(){

        Map<Integer, List<List<CopsRole>>> copsGroups = new HashMap<>();

        List<List<CopsRole>> copsGroups0 = new ArrayList<>();
        copsGroups0.add(Arrays.asList(CopsRole.SWATT, CopsRole.SWATT));
        copsGroups0.add(Arrays.asList(CopsRole.SWATT, CopsRole.SWATT, CopsRole.GENDARME));
        copsGroups.put(0, copsGroups0);

        List<List<CopsRole>> copsGroups1 = new ArrayList<>();
        copsGroups1.add(Arrays.asList(CopsRole.SWATT, CopsRole.SWATT, CopsRole.SWATT, CopsRole.SWATT));
        copsGroups1.add(Arrays.asList(CopsRole.SWATT, CopsRole.SWATT, CopsRole.GENDARME));
        copsGroups.put(1, copsGroups1);

        List<List<CopsRole>> copsGroups2 = new ArrayList<>();
        copsGroups2.add(Arrays.asList(CopsRole.SWATT, CopsRole.SWATT, CopsRole.GENDARME, CopsRole.GENDARME));
        copsGroups2.add(Arrays.asList(CopsRole.SWATT, CopsRole.SWATT, CopsRole.GENDARME, CopsRole.GENDARME));
        copsGroups.put(2, copsGroups2);

        return copsGroups;
    }

    public static List<CopsRole> selectCopsGroup(int stars) {
        List<List<CopsRole>> groups = copsGroups.getOrDefault(stars, copsGroups.get(0));
        int randomIndex = new Random().nextInt(groups.size());
        return groups.get(randomIndex);
    }


    public static HashMap<UUID, Cops> copsList = new HashMap<>();

    public ArrayList<Cops> listCops = new ArrayList<>();
    public UUID idSection;
    public String name;
    public EntityType entityType;
    public HashMap<String, ItemStack> equipement;
    public Creature entityCop;
    public Player target;
    public long timePassive;

    public Cops(CopsRole copRole, Player target, UUID idSection, Location loc){
        this.idSection = idSection;
        this.name = copRole.nameRole;
        this.entityType = copRole.creatureType;
        this.equipement = copRole.equipement;
        this.entityCop = copsSpawn(target, loc);
        this.target = target;

        this.timePassive = System.currentTimeMillis();


        Cops.copsList.put(entityCop.getUniqueId(), this);
    }

    public void setTarget(Player newTarget){
        this.target = newTarget;

        Cops.copsList.put(this.entityCop.getUniqueId(), this);
    }

    public void setTimePassive(long time){
        this.timePassive = time;

        Cops.copsList.put(this.entityCop.getUniqueId(), this);
    }


    public Creature copsSpawn(Player target, Location location) {
        EntityType entityType = this.entityType;

        Creature creature = (Creature) Bukkit.getWorld("world").spawnEntity(location, entityType);

        //creature.setCustomName(this.name);
        //creature.setCustomNameVisible(true);

        EntityEquipment equip = creature.getEquipment();

        assert equip != null;
        equip.clear();


        creature.setMetadata("cops", new FixedMetadataValue(CopsPlugin.getInstance(), "cops"));

        creature.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300*20, 2, false, false));

        creature.setCanPickupItems(false);

        creature.setTarget(target);

        Zombie zombie = (Zombie) creature;

        zombie.setAdult();

        if(zombie.getVehicle() != null){
            zombie.getVehicle().remove();
        }

        return (Creature) zombie;
    }

    public static ArrayList<Cops> cobsSeekPlayer(Player player){
        ArrayList<Cops> copsSeek = new ArrayList<>();

        for (HashMap.Entry<UUID, Cops> entry : copsList.entrySet()) {
            UUID id = entry.getKey();
            Cops cop = entry.getValue();

            if(cop.target == player){
                copsSeek.add(cop);
            }
        }
        return copsSeek;
    }

    public static ArrayList<Cops> getCopsSection(UUID section){
        ArrayList<Cops> copsSection = new ArrayList<>();

        for (HashMap.Entry<UUID, Cops> entry : copsList.entrySet()) {
            Cops cop = entry.getValue();

            if(cop.idSection == section) {
                copsSection.add(cop);
            }
        }
        return copsSection;
    }

    public static void copsIA(){
        new BukkitRunnable(){

            @Override
            public void run() {

                ArrayList<UUID> copsOff = new ArrayList<>();

                for (HashMap.Entry<UUID, Cops> entry : copsList.entrySet()) { //ForEach value in HashMap
                    UUID id = entry.getKey();
                    Cops cop = entry.getValue();

                    Creature entityCop = cop.entityCop;


                    boolean playerInRange = false;

                    for(Entity entity : entityCop.getNearbyEntities(20,20,20)){
                        if(entity instanceof Player){
                            if(cop.target != null){
                                if(entity.getUniqueId().equals(cop.target.getUniqueId())){
                                    entityCop.setTarget((LivingEntity) entity);

                                    //String weaponTile = WeaponMechanicsAPI.getWeaponTitle(cop.equipement.get("item"));
                                    //System.out.println(weaponTile);
                                    //if(weaponTile != null){
                                        //Entity 1 = entityCop, Entity 2 = entity (Player)
                                        if(entityCanSee(entityCop, entity)){
                                            System.out.println("can see");
                                            WeaponMechanicsAPI.shoot(entityCop, "ak47", getLookDirection(entityCop.getLocation().getYaw(), entity.getLocation().getPitch()));
                                        } else {
                                            System.out.println("can't see");
                                        }
                                    //}

                                    cop.setTimePassive(System.currentTimeMillis());
                                    playerInRange = true;
                                }
                            }
                        }
                    }

                    if(playerInRange == false){
                        if(entityCop.getTarget() != null){
                            System.out.println("Stop Target");
                            entityCop.setTarget(null);
                            cop.setTimePassive(System.currentTimeMillis());
                        }
                    }

                    if(System.currentTimeMillis() - cop.timePassive > 30*1000){
                        copsOff.add(id);
                    }
                }

                for(UUID id : copsOff){
                    copsList.get(id).entityCop.remove();
                    copsList.remove(id);
                }

            }
        }.runTaskTimer(CopsPlugin.getInstance(), 40, 10);
    }

    public static org.bukkit.util.Vector getLookDirection(LivingEntity entity) {
        double yaw = Math.toRadians(entity.getLocation().getYaw() + (new Random().nextDouble(5-(-5)) + (-5)) );

        double x = -Math.sin(yaw);
        double z = Math.cos(yaw);

        return new org.bukkit.util.Vector(x, 0,z).normalize();
    }
    public static org.bukkit.util.Vector getLookDirection(float yaw, float pitch) {
        // Convert the Yaw and Pitch from degrees to radians
        double yawRadians = Math.toRadians(yaw);
        double pitchRadians = Math.toRadians(pitch);

        // Calculate the X, Y and Z components of the direction vector using trigonometric functions
        double x = -Math.sin(yawRadians) * Math.cos(pitchRadians);
        double y = Math.sin(pitchRadians);
        double z = Math.cos(yawRadians) * Math.cos(pitchRadians);

        return new org.bukkit.util.Vector(x,y,z);
    }

    public static org.bukkit.util.Vector getVectorBetweenEntities(Entity entity1, Entity entity2) {
        org.bukkit.util.Vector entity1Location = entity1.getLocation().toVector();
        org.bukkit.util.Vector entity2Location = entity2.getLocation().toVector();
        return entity2Location.subtract(entity1Location);
    }

    public static double angleBetweenVectors(org.bukkit.util.Vector v1, org.bukkit.util.Vector v2) {
        double dotProduct = v1.dot(v2);
        double magnitudesProduct = v1.length() * v2.length();
        double angleRadians = Math.acos(dotProduct / magnitudesProduct);
        return Math.toDegrees(angleRadians);
    }

    private static boolean entityCanSee(Entity entity1, Entity entity2) {
        org.bukkit.util.Vector vector1 = getVectorBetweenEntities(entity1, entity2);
        org.bukkit.util.Vector vector2 = getLookDirection(entity1.getLocation().getYaw(), entity2.getLocation().getPitch());
        double angle = angleBetweenVectors(vector1, vector2);
        return angle < 120/2;
    }


    public static Creature entityEquipement(Creature creature, HashMap<String, ItemStack> equipement){

        EntityEquipment equip = creature.getEquipment();

        assert equip != null;
        equip.clear();

        /*String weaponTitle = "Artsd"

        ItemStack weaponStack = WeaponMechanics.getConfigurations().getObject(weaponTitle + ".Info.Weapon_Item", ItemStack.class);
        weaponStack = weaponStack.clone();
        weaponStack.setAmount(1);

        Bukkit.getPluginManager().callEvent(new WeaponGenerateEvent(weaponTitle, weaponStack, entity, data));*/

        if(equipement.containsKey("helmet"))
            equip.setHelmet(equipement.get("helmet"));
        if(equipement.containsKey("chestplate"))
            equip.setChestplate(equipement.get("chestplate"));
        if(equipement.containsKey("leggings"))
            equip.setLeggings(equipement.get("leggings"));
        if(equipement.containsKey("boots"))
            equip.setBoots(equipement.get("boots"));
        if(equipement.containsKey("item"))
            equip.setItemInMainHand(equipement.get("item"));
        return creature;
    }




    public enum CopsRole {
        //Display Name, EntityType, Equipement, Rangs, SpawnDistance

        SWATT("Swatt", EntityType.ZOMBIE, equipementCops("SWATT")),
        GENDARME("Gendarme", EntityType.SKELETON, equipementCops("GENDARME")),
        BRIGADIER("Swatt", EntityType.ZOMBIE, equipementCops("BRIGADIER")),
        CAPORAL("Swatt", EntityType.ZOMBIE, equipementCops("CAPORAL")),
        SOLDAT("Swatt", EntityType.ZOMBIE, equipementCops("SOLDAT"));

        private final String nameRole;
        private final EntityType creatureType;
        private final HashMap<String, ItemStack> equipement;

        CopsRole(String displayName, EntityType type, HashMap<String, ItemStack> equipement){
            this.nameRole = displayName;
            this.creatureType = type;
            this.equipement = equipement;
        }

        private static HashMap<String, ItemStack> equipementCops(String name){

            HashMap<String, ItemStack> equipement = new HashMap<>();

            if(name.equalsIgnoreCase("SWATT")){
                equipement.put("helmet", itemCreate(Material.IRON_HELMET, 0));
                equipement.put("item", itemCreate(Material.FEATHER, 121)); //Ak47

            } else if(name.equalsIgnoreCase("GENDARME")){
                equipement.put("helmet", itemCreate(Material.IRON_HELMET, 0));
                equipement.put("chestplate", itemCreate(Material.CHAINMAIL_CHESTPLATE, 0));

            } else if(name.equalsIgnoreCase("BRIGADIER")){
                equipement.put("helmet", itemCreate(Material.IRON_HELMET, 0));
                equipement.put("chestplate", itemCreate(Material.GOLDEN_CHESTPLATE, 1));
                equipement.put("leggings", itemCreate(Material.LEATHER_LEGGINGS, 0));

            } else if(name.equalsIgnoreCase("CAPORAL")){
                equipement.put("helmet", itemCreate(Material.IRON_HELMET, 0));

            } else if(name.equalsIgnoreCase("SOLDAT")){
                equipement.put("helmet", itemCreate(Material.DIAMOND_HELMET, 0));
            }

            return equipement;
        }

        private static ItemStack itemCreate(Material material, int customModelData) {
            final ItemStack item = new ItemStack(material, 1);
            final ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if(customModelData != 0){
                    meta.setCustomModelData(customModelData);
                    item.setItemMeta(meta);
                }
            }
            return item;
        }

    }
}


