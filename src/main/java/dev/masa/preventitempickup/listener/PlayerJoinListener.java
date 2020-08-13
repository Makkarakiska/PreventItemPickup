package dev.masa.preventitempickup.listener;

import dev.masa.preventitempickup.PreventItemPickup;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public class PlayerJoinListener implements Listener {

    private PreventItemPickup plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.plugin.getPreventedItemService().initCache(event.getPlayer().getUniqueId());
        this.plugin.getPreventedItemService().loadPreventedItems(event.getPlayer().getUniqueId());
    }
}
