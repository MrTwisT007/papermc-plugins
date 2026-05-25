package me.bunnycat.warpsgui.Listeners;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinEvent implements Listener
{
    public JoinEvent(JavaPlugin plugin)
  {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
}