package fr.kanpvp.copsplugin.cops;


import fr.kanpvp.copsplugin.CopsPlugin;
import fr.kanpvp.copsplugin.PlayerStar;
import fr.kanpvp.copsplugin.utlis.randomdraw.EventLoot;
import fr.kanpvp.copsplugin.utlis.randomdraw.ManagerDraw;
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
import java.util.stream.Collectors;

public class Cops {
    private static final Map<Integer, List<List<CopsRole>>> copsGroups = Cops.createCopsGroup();
    private static final HashMap<Integer, String> weaponsData = Cops.createWeaponsData();

    public static HashMap<UUID, Cops> copsList = new HashMap<>();

    public ArrayList<Cops> listCops = new ArrayList<>();
    public UUID idSection;
    public String name;
    public EntityType entityType;
    public CopsRole copsRole;
    public HashMap<String, ItemStack> equipement;
    public String weaponTitle;
    public Creature entityCop;
    public Player target;
    public long timePassive;

    public Cops(CopsRole copRole, Player target, UUID idSection, Location loc){
        this.idSection = idSection;
        this.copsRole = copRole;
        this.name = copRole.nameRole;
        this.entityType = copRole.creatureType;
        this.equipement = copRole.equipement;
        this.weaponTitle = copRole.getWeaponTitle();
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
        //creature.setCustomNameVisible(true);;

        EntityEquipment equip = creature.getEquipment();

        assert equip != null;
        equip.clear();

        creature.setMetadata("cops", new FixedMetadataValue(CopsPlugin.getInstance(), "cops"));

        creature.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300*20, 2, false, false));

        creature.setCanPickupItems(false);
        creature.setRemoveWhenFarAway(false);

        if(creature.getVehicle() != null){
            creature.getVehicle().remove();
        }

        if(creature instanceof Zombie){
            Zombie zombie = (Zombie) creature;
            zombie.setAdult();
            return (Creature) zombie;
        }

        return (Creature) creature;
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

    public static ArrayList<Cops> cobsSeekPlayerReel(Player player){
        ArrayList<Cops> copsSeek = new ArrayList<>();

        PlayerStar playerStar = PlayerStar.playerDataFromPlayer(player);
        assert playerStar != null;
        double star = playerStar.getStar();

        int distance;
        if (star <= 2.6) {
            distance = 20;
        } else if (star <= 4.6) {
            distance = 50;
        } else {
            distance = 100;
        }

        List<Cops> allPlayersInRange = player.getNearbyEntities(distance, distance, distance)
                .stream()
                .filter(entity -> copsList.containsKey(entity.getUniqueId()))
                .map(entity -> copsList.get(entity.getUniqueId()))
                .distinct()
                .collect(Collectors.toList());

        for (Cops cop : allPlayersInRange) {
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

                for (HashMap.Entry<UUID, Cops> entry : copsList.entrySet()) { //ForEach value in HashMap / EACH cops
                    UUID id = entry.getKey();
                    Cops cop = entry.getValue();

                    Creature entityCop = cop.entityCop;

                    if(cop.target != null && cop.target.isOnline()){
                        boolean playerInRange = false;
                        Player player = cop.target;
                        double distance = player.getLocation().distance(cop.entityCop.getLocation());

                        if(distance < 20){
                            shootIfPossible(entityCop, cop.weaponTitle, player);
                            playerInRange = true;

                        } else if(distance > 20 && distance < 50 && PlayerStar.playerDataFromPlayer(player).getStar() > 2.6) {
                                shootIfPossible(entityCop, cop.weaponTitle, player);
                                playerInRange = true;

                        } else if(distance < 100 && PlayerStar.playerDataFromPlayer(player).getStar() > 4.6 ) {

                                playerInRange = true;

                        }

                        if(!playerInRange){ //If player is out set null
                            if(entityCop.getTarget() != null){
                                entityCop.setTarget(null);
                                cop.setTimePassive(System.currentTimeMillis());
                            }
                        } else {
                            entityCop.setTarget((LivingEntity) cop.target);
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

    public static void shootIfPossible(LivingEntity shooter, String weaponTitle, Player target) {

        if(weaponTitle != null && CopsPlugin.getVectorCal().entityCanSee(shooter, target)){ //If player is in front of the cop
            WeaponMechanicsAPI.shoot(shooter, weaponTitle, shooter.getLocation().getDirection().normalize() );
        }

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

        SWATT("Swatt", EntityType.ZOMBIE, equipementCops("SWATT"), ManagerDraw.type1Loot),
        GENDARME("Gendarme", EntityType.ZOMBIE, equipementCops("GENDARME"), ManagerDraw.type1Loot),
        BRIGADIER("Swatt", EntityType.ZOMBIE, equipementCops("BRIGADIER"), ManagerDraw.type1Loot),
        CAPORAL("Swatt", EntityType.ZOMBIE, equipementCops("CAPORAL"), ManagerDraw.type1Loot),
        SOLDAT("Swatt", EntityType.ZOMBIE, equipementCops("SOLDAT"), ManagerDraw.type1Loot);

        private final String nameRole;
        private final EntityType creatureType;
        private final HashMap<String, ItemStack> equipement;
        public final EventLoot[] loots;

        private String weaponTile;

        CopsRole(String displayName, EntityType type, HashMap<String, ItemStack> equipement, EventLoot[] eventsA){
            this.nameRole = displayName;
            this.creatureType = type;
            this.equipement = equipement;
            this.loots = eventsA;
        }

        private static HashMap<String, ItemStack> equipementCops(String name){

            HashMap<String, ItemStack> equipement = new HashMap<>();

            if(name.equalsIgnoreCase("SWATT")){
                equipement.put("helmet", itemCreate(Material.IRON_HELMET, 0));
                equipement.put("item", itemCreate(Material.LEATHER_HORSE_ARMOR, 40016)); //Ak47

            } else if(name.equalsIgnoreCase("GENDARME")){

               /* ItemStack items = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) items.getItemMeta();
                leatherArmorMeta.setColor(Color.fromBGR(10,50,100));*/

                equipement.put("helmet", itemCreate(Material.IRON_HELMET, 0));
                equipement.put("chestplate", itemCreate(Material.CHAINMAIL_CHESTPLATE, 0));
                equipement.put("item", itemCreate(Material.LEATHER_HORSE_ARMOR, 40009)); //50_GS

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

        private String getWeaponTitle(){
            ItemStack weapon = this.equipement.getOrDefault("item",null);
            if(weapon != null){
                Integer nbtWeapon = weapon.getItemMeta().getCustomModelData();
                return weaponsData.get(nbtWeapon);
            }
            return null;
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

    public static Map<Integer, List<List<CopsRole>>> createCopsGroup(){

        Map<Integer, List<List<CopsRole>>> copsGroups = new HashMap<>();

        List<List<CopsRole>> copsGroups0 = new ArrayList<>();
        copsGroups0.add(Arrays.asList(CopsRole.SWATT, CopsRole.SWATT));
        copsGroups0.add(Arrays.asList(CopsRole.SWATT, CopsRole.SWATT, CopsRole.GENDARME));
        copsGroups.put(0, copsGroups0);

        List<List<CopsRole>> copsGroups1 = new ArrayList<>();
        copsGroups1.add(Arrays.asList(CopsRole.SWATT, CopsRole.SWATT, CopsRole.SWATT, CopsRole.GENDARME));
        copsGroups1.add(Arrays.asList(CopsRole.SWATT, CopsRole.SWATT, CopsRole.SWATT));
        copsGroups.put(1, copsGroups1);

        List<List<CopsRole>> copsGroups2 = new ArrayList<>();
        copsGroups2.add(Arrays.asList(CopsRole.SWATT, CopsRole.SWATT, CopsRole.GENDARME, CopsRole.GENDARME));
        copsGroups2.add(Arrays.asList(CopsRole.SWATT, CopsRole.SWATT, CopsRole.GENDARME, CopsRole.GENDARME));
        copsGroups.put(2, copsGroups2);

        return copsGroups;
    }

    public static HashMap<Integer, String> createWeaponsData(){
        HashMap<Integer, String> weapons = new HashMap<Integer, String>();

        weapons.put(40016, "military_rifle");
        weapons.put(40009, "pistol_mk_2");

        return weapons;
    }



    public static List<CopsRole> selectCopsGroup(int stars) {
        List<List<CopsRole>> groups = copsGroups.getOrDefault(stars, copsGroups.get(0));
        int randomIndex = new Random().nextInt(groups.size());
        return groups.get(randomIndex);
    }





}


