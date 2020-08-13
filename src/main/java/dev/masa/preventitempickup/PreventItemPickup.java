package dev.masa.preventitempickup;

import co.aikar.commands.PaperCommandManager;
import dev.masa.preventitempickup.command.PreventItemPickupCommand;
import dev.masa.preventitempickup.listener.PlayerItemPickupListener;
import dev.masa.preventitempickup.listener.PlayerJoinListener;
import dev.masa.preventitempickup.service.DatabaseService;
import dev.masa.preventitempickup.service.PreventedItemService;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public final class PreventItemPickup extends JavaPlugin {

    @Getter
    private DatabaseService databaseService;
    @Getter
    private PreventedItemService preventedItemService;

    @Override
    public void onEnable() {
        // Setup metrics
        Metrics metrics = new Metrics(this, 8517);
        this.saveDefaultConfig();

        Configuration config = this.getConfig();
        this.databaseService = new DatabaseService(config.getString("database.address"), config.getInt("database.port"), config.getString("database.name"), config.getString("database.username"), config.getString("database.password"));
        this.preventedItemService = new PreventedItemService(this);

        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerItemPickupListener(this), this);

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new PreventItemPickupCommand(this));
    }
}
