package fr.kanpvp.copsplugin.cops;


import fr.kanpvp.copsplugin.CopsPlugin;
import fr.kanpvp.copsplugin.PlayerStar;
import fr.kanpvp.copsplugin.utlis.randomdraw.EventLoot;
import fr.kanpvp.copsplugin.utlis.randomdraw.ManagerDraw;
import me.deecaad.weaponmechanics.WeaponMechanicsAPI;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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

    public static void spawnCopsSection(Player target, Location loc){
        UUID idSection = UUID.randomUUID();

        PlayerStar playerStar = PlayerStar.playerDataFromPlayer(target);
        assert playerStar != null;
        int star =  Integer.parseInt(String.valueOf(String.valueOf(PlayerStar.playerDataFromPlayer(target).getStar()).charAt(0)));

        for(Cops.CopsRole role : Cops.selectCopsGroup(star) ){
            Cops cop = new Cops(role, target, idSection, loc); //Spawn Cops
            cop.entityCop.setInvisible(true);
            cop.entityCop.setGlowing(true);

            Vector ejectVec = getLookDirection(target);
            cop.entityCop.setVelocity(ejectVec.multiply(3 + new Random().nextInt(1))); //Ejection
        }

        Bukkit.getScheduler().runTaskLater(CopsPlugin.getInstance(), new Runnable(){
            final UUID section = idSection;
            @Override
            public void run() {
                Location loc = new Location(Bukkit.getWorld("world"), 0,0,0);
                for(Cops cop : Cops.getCopsSection(section)){
                    cop.entityCop.setInvisible(false);
                    cop.entityCop.setTarget(cop.target);

                    Cops.entityEquipement(cop.entityCop, cop.equipement);

                    loc = cop.entityCop.getLocation();
                }
                Bukkit.getWorld("world").playSound(loc, Sound.ITEM_TOTEM_USE, 1F, 1F);
            }
        }, 20);
    }

    public static void spawnCopsSection(Player target, Location loc, int type){
        UUID idSection = UUID.randomUUID();

        /*PlayerStar playerStar = PlayerStar.playerDataFromPlayer(target);
        assert playerStar != null;
        playerStar.addStar();*/

        for(Cops.CopsRole role : Cops.selectCopsGroup(type) ){
            Cops cop = new Cops(role, target, idSection, loc); //Spawn Cops
            cop.entityCop.setTarget(cop.target);
            Cops.entityEquipement(cop.entityCop, cop.equipement);
        }

    }

    public static org.bukkit.util.Vector getLookDirection(Player player) {
        double yaw = Math.toRadians(player.getLocation().getYaw() + (new Random().nextDouble() + (-5)) );

        double x = -Math.sin(yaw);
        double z = Math.cos(yaw);

        return new org.bukkit.util.Vector(x, 0,z).normalize();
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

                        PlayerStar playerStar = PlayerStar.playerDataFromPlayer(player);

                        if(playerStar != null && playerStar.getStar() == 0){
                            playerStar.addStar();
                        }

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
                            entityCop.setTarget((Player) cop.target);
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

        CopsRole(String displayName, EntityType type, HashMap<String, ItemStack> equipement, EventLoot[] eventsA){
            this.nameRole = displayName;
            this.creatureType = type;
            this.equipement = equipement;
            this.loots = eventsA;
        }

        private static HashMap<String, ItemStack> equipementCops(String name){

            HashMap<String, ItemStack> equipement = new HashMap<>();

            if(name.equalsIgnoreCase("SWATT")){ //Cop Niveau 4
                equipement.put("helmet", itemCreate(Material.IRON_HELMET, 0));
                equipement.put("item", itemCreate(Material.LEATHER_HORSE_ARMOR, 40016)); //Ak47

            } else if(name.equalsIgnoreCase("GENDARME")){  //Cop Niveau 3

                ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) chestplate.getItemMeta();
                assert leatherArmorMeta != null;
                leatherArmorMeta.setColor(Color.fromRGB(3,252,136));
                chestplate.setItemMeta(leatherArmorMeta);

                equipement.put("helmet", itemCreate(Material.IRON_HELMET, 0));
                equipement.put("chestplate", chestplate);
                equipement.put("item", itemCreate(Material.LEATHER_HORSE_ARMOR, 40009)); //50_GS

            } else if(name.equalsIgnoreCase("BRIGADIER")){ //Cop Niveau 2
                equipement.put("helmet", itemCreate(Material.IRON_HELMET, 0));
                equipement.put("chestplate", itemCreate(Material.GOLDEN_CHESTPLATE, 1));
                equipement.put("leggings", itemCreate(Material.LEATHER_LEGGINGS, 0));

            } else if(name.equalsIgnoreCase("CAPORAL")){ //Cop Niveau 1
                equipement.put("helmet", itemCreate(Material.IRON_HELMET, 0));

            } else if(name.equalsIgnoreCase("SOLDAT")){
                equipement.put("helmet", itemCreate(Material.DIAMOND_HELMET, 0));
            }

            return equipement;
        }

        private static Color hexStrColorToColor(String hexCode){

            int resultRed = Integer.valueOf(hexCode.substring(0, 2), 16);
            int resultGreen = Integer.valueOf(hexCode.substring(2, 4), 16);
            int resultBlue = Integer.valueOf(hexCode.substring(4, 6), 16);
            Color color = Color.fromRGB(resultRed, resultGreen, resultBlue);

            return color;
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


