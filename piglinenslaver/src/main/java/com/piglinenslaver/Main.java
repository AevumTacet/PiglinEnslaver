package com.piglinenslaver;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

   public static Main plugin;
   public static FileConfiguration config;
   public static ThrallManager manager;

   @Override
   public void onEnable() {
      plugin = this;
      saveDefaultConfig();
      config = getConfig();

      manager = new ThrallManager();

      this.getServer().getPluginManager().registerEvents(manager, this);
      getLogger().info("Thrall Master plugin enabled.");
   }

   public static void reload() {
      plugin.reloadConfig();
      config = plugin.getConfig();
   }
}