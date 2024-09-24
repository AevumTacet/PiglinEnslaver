package com.piglinenslaver;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

   public static Main plugin; // Mantener referencia estática al plugin
   public static FileConfiguration config; // Mantener referencia estática a la configuración
   public static PiglinManager manager; // Instancia de PiglinFollow

   @Override
   public void onEnable() {
       plugin = this;
       saveDefaultConfig(); // Cargar o crear el config.yml si no existe
       config = getConfig(); // Referencia estática al archivo de configuración

       // Instanciar PiglinFollow
       manager = new PiglinManager();

       // Registrar eventos, solo una vez
       this.getServer().getPluginManager().registerEvents(new Events(), this);
       this.getServer().getPluginManager().registerEvents(manager, this); // Registrar PiglinFollow también

       // Mensaje en consola cuando se habilita el plugin
       getLogger().info("Piglin Enslaver plugin enabled.");
   }

   @Override
  // Método llamado al deshabilitar el plugin
  public void onDisable() {
     // Imprime un mensaje en la consola indicando que el plugin se ha deshabilitado
     this.getLogger().info("Disabled " + this.getDescription().getName() + " " + this.getDescription().getVersion());
  }

  // Método estático para recargar la configuración del plugin
  public static void reload() {
     plugin.reloadConfig();
     config = plugin.getConfig();
  }
}