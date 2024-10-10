package com.thrallmaster;

import java.util.Comparator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class ThrallUtils {
    private static ThrallManager manager = Main.manager;
    public static double searchRadius = 10.0;

    public static <T extends LivingEntity> LivingEntity findNearestEntity(Entity from) 
    {
        return findNearestEntity(from, Enemy.class);
    }

    public static <T extends LivingEntity> LivingEntity findNearestEntity(Entity from, Class<T> filterClass) 
    {
        if (from == null)
        {
            return null;
        }
        
        ThrallState state = manager.getThrall(from.getUniqueId());
        Player owner = state.getOwner();
        Location location = from.getLocation();
        double multiplier = MaterialUtils.isRanged(((Skeleton) from).getEquipment().getItemInMainHand().getType()) ? 1.5 : 1.0;

        return from.getWorld().getNearbyEntities(location, searchRadius * multiplier, searchRadius * multiplier, searchRadius * multiplier).stream()
            .filter(x -> x instanceof LivingEntity && !x.equals(owner))
            .filter(x -> !(manager.isThrall(x) && manager.haveSameOwner(state, x)))
            .filter(x -> filterClass.isAssignableFrom(x.getClass()) || x instanceof Player)
            .min(Comparator.comparingDouble(x -> x.getLocation().distance(location)))
            .map(x -> (LivingEntity) x)
            .orElse(null);
    } 


    public static void equipThrall(LivingEntity entity, ItemStack item)
    {
        World world = entity.getWorld();
        Material material = item.getType();
        EntityEquipment equipment = entity.getEquipment();

        if (MaterialUtils.isWeapon(material))
        {
            world.dropItemNaturally(entity.getLocation(), equipment.getItemInMainHand());
            equipment.setItemInMainHand(item);
        }
        else if (MaterialUtils.isArmor(material))
        {
            switch (MaterialUtils.getArmorType(material))
            {
                case HELMET:
                world.dropItemNaturally(entity.getLocation(), equipment.getHelmet());
                equipment.setHelmet(item);
                break;

                case CHESTPLATE:
                world.dropItemNaturally(entity.getLocation(), equipment.getChestplate());
                equipment.setChestplate(item);
                break;

                case LEGGINGS:
                world.dropItemNaturally(entity.getLocation(), equipment.getLeggings());
                equipment.setLeggings(item);
                break;

                case BOOTS:
                world.dropItemNaturally(entity.getLocation(), equipment.getBoots());
                equipment.setBoots(item);
                break;

                default:
                    break;
                
            }
        }

    }
}
