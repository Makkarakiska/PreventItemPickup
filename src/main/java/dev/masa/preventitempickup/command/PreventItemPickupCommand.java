package dev.masa.preventitempickup.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Single;
import dev.masa.preventitempickup.PreventItemPickup;
import dev.masa.preventitempickup.model.PreventedItem;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
@CommandAlias("preventitempickups|preventitempickup|pip|preventitem")
public class PreventItemPickupCommand extends BaseCommand {

    private PreventItemPickup plugin;

    @CommandAlias("add")
    @CommandPermission("preventitempickups.item.add")
    public void addItem(Player player, @Single String material) {
        if (material.equalsIgnoreCase("hand")) {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                sendMessage(player, plugin.getConfig().getString("messages.hand-is-empty"));
                return;
            }
            material = player.getInventory().getItemInMainHand().getType().name();
        }
        if (Material.matchMaterial(material) != null) {
            PreventedItem item = new PreventedItem(material, player.getUniqueId());
            plugin.getPreventedItemService().addPreventedItem(item);
            sendMessage(player, plugin.getConfig().getString("messages.item-added"));
            return;
        }
        sendMessage(player, plugin.getConfig().getString("messages.item-not-found"));
    }

    @CommandAlias("remove")
    @CommandPermission("preventitempickups.item.remove")
    public void removeItem(Player player, @Single String material) {
        if (Material.matchMaterial(material) != null) {
            plugin.getPreventedItemService().removePreventedItem(player.getUniqueId(), Material.matchMaterial(material));
            sendMessage(player, plugin.getConfig().getString("messages.item-removed"));
            return;
        }
        sendMessage(player, plugin.getConfig().getString("messages.item-not-found"));
    }


    @CommandAlias("list")
    @CommandPermission("preventitempickups.item.list")
    public void listItems(Player player) {
        List<Material> materials = plugin.getPreventedItemService().getPreventedItems(player.getUniqueId());

        String prefix =  colorize(plugin.getConfig().getString("messages.item-list-prefix"));
        String item = colorize(plugin.getConfig().getString("messages.item-list-item"));
        String separator = colorize(plugin.getConfig().getString("messages.item-list-item-separator"));

        BaseComponent component = new TextComponent(prefix);
        int i = 0;
        for (Material material : materials) {
            component.addExtra(item.replace("%item%", material.name()));
            if (i < materials.size() - 1) {
                component.addExtra(separator);
            }
            i++;
        }
        player.sendMessage(component.toLegacyText());
    }

    private String colorize(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
