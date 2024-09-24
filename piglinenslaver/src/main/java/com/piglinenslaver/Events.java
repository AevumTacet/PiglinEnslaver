package com.piglinenslaver;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Piglin; // Cambiado de PigZombie a Piglin
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffectType;

public class Events implements Listener {
    // Mapa que asocia los UUID de los Piglins a sus respectivos jugadores
    private HashMap<UUID, Integer> cureMap = new HashMap<>(); // Mapa de curaciones por PigZombie
    private HashMap<UUID, UUID> piglinOwners = new HashMap<>(); // Mapa de dueños de Piglins

    // Constructor
    public Events() {
        // No se necesita la tarea de seguimiento si se elimina el seguimiento del Piglin
    }

    // Método para establecer el dueño de un Piglin
    public void setOwner(UUID piglinUUID, UUID ownerUUID) {
        piglinOwners.put(piglinUUID, ownerUUID);
    }

    // Método para obtener el UUID del dueño de un Piglin
    public UUID getOwnerUUID(UUID piglinUUID) {
        return piglinOwners.get(piglinUUID);
    }

    // Evento que se activa al lanzar una poción a un PigZombie
    @EventHandler
    public void onPotionSplash(PotionSplashEvent e) {
        // Filtra para pociones de curación
        if (e.getPotion().getEffects().stream().anyMatch(effect -> effect.getType().equals(PotionEffectType.REGENERATION))) {
            for (LivingEntity entity : e.getAffectedEntities()) {
                if (entity instanceof PigZombie) { // Verifica si es un PigZombie
                    PigZombie pigZombie = (PigZombie) entity;
                    Player thrower = (Player) e.getPotion().getShooter();
                    UUID pigZombieUUID = pigZombie.getUniqueId();
                    
                    // Actualiza el número de curaciones
                    int currentCures = cureMap.getOrDefault(pigZombieUUID, 0) + 1;
                    cureMap.put(pigZombieUUID, currentCures);
                    
                    // Verifica si se alcanzó el número necesario de curaciones
                    if (currentCures >= Main.config.getInt("minCures") && currentCures <= Main.config.getInt("maxCures")) {
                        // Transformar al PigZombie en Piglin adulto y domesticado
                        pigZombie.remove(); // Remueve el PigZombie original
                        Piglin tamedPiglin = pigZombie.getWorld().spawn(pigZombie.getLocation(), Piglin.class); // Spawnea a un Piglin curado
                        tamedPiglin.setAdult(); // Asegura que sea un Piglin adulto
                        tamedPiglin.setImmuneToZombification(true); // Hace al Piglin inmune a la zombificación

                        // Desactiva comportamientos predeterminados que puedan interferir
                        tamedPiglin.setAI(true); // Asegura que tenga AI activada

                        // Añade efectos visuales de partículas SOUL
                        tamedPiglin.getWorld().spawnParticle(Particle.SOUL, tamedPiglin.getLocation().add(0.0D, 1.0D, 0.0D), 20, 0.5D, 1.0D, 0.5D);

                        // Asigna el Piglin al jugador que lanzó las pociones
                        setOwner(tamedPiglin.getUniqueId(), thrower.getUniqueId()); // Asignar el dueño
                        String piglinPath = "piglins." + tamedPiglin.getUniqueId().toString();
                        Main.config.set(piglinPath + ".owner", thrower.getUniqueId().toString());
                        Main.plugin.saveConfig(); // Guarda en la configuración

                        // Registrar al piglin como domesticado
                        Main.manager.setOwner(tamedPiglin, thrower);
                        thrower.sendMessage("Your Piglin has been tamed!");
                        
                        // Remueve al PigZombie del mapa de curaciones
                        cureMap.remove(pigZombieUUID);
                    }
                }
            }
        }
    }

    // Evento que se activa cuando un Piglin muere
    @EventHandler
    public void onPiglinDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Piglin) { // Verifica si es un Piglin
            UUID eu = e.getEntity().getUniqueId();
            String pstr = "piglins." + eu.toString();
            if (Main.config.contains(pstr)) {
                Player owner = Bukkit.getPlayer(UUID.fromString(Main.config.getString(pstr + ".owner")));
                if (owner != null) {
                    owner.sendMessage(e.getEntity().getName() + " has died");
                }
                Main.config.set(pstr, null);
                Main.plugin.saveConfig(); // Guarda la configuración actualizada
            }
        }
    }
}