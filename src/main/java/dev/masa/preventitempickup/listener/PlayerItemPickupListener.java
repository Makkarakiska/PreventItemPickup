package dev.masa.preventitempickup.listener;

import dev.masa.preventitempickup.PreventItemPickup;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.List;
import java.util.logging.Level;

@AllArgsConstructor
public class PlayerItemPickupListener implements Listener {

    private PreventItemPickup plugin;

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        List<Material> materials = plugin.getPreventedItemService().getPreventedItems(event.getEntity().getUniqueId());
        if (materials.contains(event.getItem().getItemStack().getType())) {
            event.setCancelled(true);
        }
    }
}
