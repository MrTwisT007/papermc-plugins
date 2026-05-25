package me.bunnycat.warpsgui;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;


public class Utils {

    public static void showTitleWrapper(Player p, String title, String subtitle, int duration) {
        int fadein = 1, fadeout = 1;
        p.showTitle(Title.title(Component.text(title),
                Component.text(subtitle),
                Title.Times.times(Duration.ofSeconds(fadein),
                        Duration.ofSeconds(duration),
                        Duration.ofSeconds(fadeout)
                )
        ));
    }

    void loadImages() {
        try {
            copyResourceToBlueMapWebApp(Warps.blueMap.getWebApp().getWebRoot(), "/home.png", "homes");
            copyResourceToBlueMapWebApp(Warps.blueMap.getWebApp().getWebRoot(), "/warp.png", "warps");
            copyResourceToBlueMapWebApp(Warps.blueMap.getWebApp().getWebRoot(), "/spawn.png", "spawn");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void copyResourceToBlueMapWebApp(final Path webroot, final String fromResource, final String toAsset) throws IOException {
        final Path toPath = webroot.resolve("assets").resolve(toAsset);
        Files.createDirectories(toPath.getParent());
        try (
                final InputStream in = this.getClass().getResourceAsStream(fromResource);
                final OutputStream out = Files.newOutputStream(toPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        ){
            if (in == null) throw new IOException("Resource not found: " + fromResource);
            in.transferTo(out);
        }
    }

    static void addMarkers() {
        if (Warps.plugin.getConfig().getBoolean("BluemapIntegration.showwarps")) {
            addWarpMarkers();
        }
        if (Warps.plugin.getConfig().getBoolean("BluemapIntegration.showhomes")) {
            addHomeMarkers();
        }
        if (Warps.plugin.getConfig().getBoolean("BluemapIntegration.showspawn")) {
            addSpawnMarker();
        }
    }

    private static String getMarkerURL(final BlueMapMap map, final String id) {
            return "assets/" + id;
    }

    public static void addMarker(String markerWorld, String markerID, String markerName, Vector3d markerCoords, String markerSetKey){
        Warps.blueMap.getWorld(markerWorld).ifPresent(blueWorld -> blueWorld.getMaps().forEach(map -> {
            final MarkerSet markerSet = map.getMarkerSets().getOrDefault(markerSetKey, MarkerSet.builder().label(markerSetKey).build());
                POIMarker marker = POIMarker.builder()
                        .label(markerName)
                        .icon(getMarkerURL(map, markerSetKey), Vector2i.from(16, 16))
                        .position(markerCoords)
                        .build();
                markerSet.getMarkers().put(markerID, marker);
                map.getMarkerSets().put(markerSetKey, markerSet);
        }));
    }

    public static void removeMarker(String markerWorld, String markerID, String markerSetKey) {
        Warps.blueMap.getWorld(markerWorld).ifPresent(blueWorld -> blueWorld.getMaps().forEach(map -> {
            final MarkerSet markerSet = map.getMarkerSets().getOrDefault(markerSetKey, MarkerSet.builder().label(markerSetKey).build());
            markerSet.remove(markerID);
        }));
    }

    private static void addWarpMarkers(){
        if (Warps.plugin.getConfig().getConfigurationSection("warps") != null) {
            for (String warpName : Warps.plugin.getConfig().getConfigurationSection("warps").getKeys(false)) {
                String warpID = warpName;
                String warpWorld = Warps.plugin.getConfig().getString("warps." + warpName + ".world");
                Double warpX = Warps.plugin.getConfig().getDouble("warps." + warpName + ".x");
                Double warpY = Warps.plugin.getConfig().getDouble("warps." + warpName + ".y");
                Double warpZ = Warps.plugin.getConfig().getDouble("warps." + warpName + ".z");
                Vector3d warpCoords = Vector3d.from(warpX, warpY, warpZ);
                addMarker(warpWorld, warpID, warpName, warpCoords, "warps");
            }
        }
    }

    private static void addHomeMarkers() {
        if (Warps.plugin.getConfig().getConfigurationSection("homes") != null) {
            for (String playerUUID : Warps.plugin.getConfig().getConfigurationSection("homes").getKeys(false)) {
                for (String homeName : Warps.plugin.getConfig().getConfigurationSection("homes." + playerUUID).getKeys(false)) {
                    String homeID = playerUUID + homeName;
                    String playerName = Warps.plugin.getConfig().getString("homes." + playerUUID + "." + homeName + ".playername");
                    String homeWorld = Warps.plugin.getConfig().getString("homes." + playerUUID + "." + homeName + ".world");
                    Double homeX = Warps.plugin.getConfig().getDouble("homes." + playerUUID + "." + homeName + ".x");
                    Double homeY = Warps.plugin.getConfig().getDouble("homes." + playerUUID + "." + homeName + ".y");
                    Double homeZ = Warps.plugin.getConfig().getDouble("homes." + playerUUID + "." + homeName + ".z");
                    Vector3d homeCoords = Vector3d.from(homeX, homeY, homeZ);
                    addMarker(homeWorld, homeID, playerName + "'s " + homeName, homeCoords, "homes");
                }
            }
        }
    }

    private static void addSpawnMarker() {
        if (Warps.plugin.getConfig().getConfigurationSection("spawn") != null) {
            String spawnWorld = Warps.plugin.getConfig().getString("spawn.world");
            Double spawnX = Warps.plugin.getConfig().getDouble("spawn.x");
            Double spawnY = Warps.plugin.getConfig().getDouble("spawn.y");
            Double spawnZ = Warps.plugin.getConfig().getDouble("spawn.z");
            Vector3d spawnCoords = Vector3d.from(spawnX, spawnY, spawnZ);
            addMarker(spawnWorld, "spawn", "Spawn", spawnCoords, "spawn");
        }
    }

    public static void tpToWarp(String warpName, Player p) {
        try {
            int x = Warps.plugin.getConfig().getInt("warps." + warpName + ".x");
            int y = Warps.plugin.getConfig().getInt("warps." + warpName + ".y");
            int z = Warps.plugin.getConfig().getInt("warps." + warpName + ".z");
            int yaw = Warps.plugin.getConfig().getInt("warps." + warpName + ".yaws");
            int pitch = Warps.plugin.getConfig().getInt("warps." + warpName + ".pitch");

            World world = Bukkit.getWorld(Warps.plugin.getConfig().getString("warps." + warpName + ".world"));

            Location location = new Location(world, x, y, z, yaw, pitch);
            p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.TeleportedToWarpSound")), 1.0F, 0.0F);
            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.TeleportedToWarp") + " " + warpName, "", 5);

            p.teleport(location);

            location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 10000);
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 40));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 40));
        } catch (Exception localException1) {
            localException1.printStackTrace();
        }
    }

    public static void tpToHome(String homeName, Player p) {
        try {
            int x = Warps.plugin.getConfig().getInt("homes." + p.getUniqueId() + "." + homeName + ".x");
            int y = Warps.plugin.getConfig().getInt("homes." + p.getUniqueId() + "." + homeName + ".y");
            int z = Warps.plugin.getConfig().getInt("homes." + p.getUniqueId() + "." + homeName + ".z");
            int yaw = Warps.plugin.getConfig().getInt("homes." + p.getUniqueId() + "." + homeName + ".w");
            int pitch = Warps.plugin.getConfig().getInt("homes." + p.getUniqueId() + "." + homeName + ".v");

            World world = Bukkit.getWorld(Warps.plugin.getConfig().getString("homes." + p.getUniqueId() + "." + homeName + ".world"));

            Location location = new Location(world, x, y, z, yaw, pitch);
            p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.TeleportedToHomeSound")), 1.0F, 0.0F);
            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.TeleportedToHome") + " " + homeName, "", 5);

            p.teleport(location);

            location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 10000);
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 40));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 40));
        } catch (Exception localException2) {
            localException2.printStackTrace();
        }
    }
}
