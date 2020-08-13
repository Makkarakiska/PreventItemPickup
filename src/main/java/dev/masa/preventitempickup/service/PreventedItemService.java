package dev.masa.preventitempickup.service;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import dev.masa.preventitempickup.PreventItemPickup;
import dev.masa.preventitempickup.model.PreventedItem;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Material;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

public class PreventedItemService {

    private PreventItemPickup plugin;

    private Dao<PreventedItem, Integer> itemsDao;

    @Getter
    private HashMap<UUID, List<Material>> players = new HashMap<>();

    @SneakyThrows
    public PreventedItemService(PreventItemPickup plugin) {
        this.plugin = plugin;
        this.itemsDao = DaoManager.createDao(plugin.getDatabaseService().getConnection(), PreventedItem.class);
        TableUtils.createTableIfNotExists(plugin.getDatabaseService().getConnection(), PreventedItem.class);
    }

    public List<Material> getPreventedItems(UUID owner) {
        return this.getPlayers().get(owner);
    }

    public void loadPreventedItems(UUID owner) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                List<PreventedItem> items = this.itemsDao.queryForEq("owner", owner);
                this.mapMaterials(items, materials -> {
                    this.addToCache(owner, materials);
                });
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void addPreventedItem(PreventedItem item) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                this.itemsDao.create(item);
                this.updateToCache(item.getOwner(), Material.matchMaterial(item.getMaterial()));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Removes an item from database
     *
     * @param owner    preventer of the item
     * @param material material to remove
     */
    public void removePreventedItem(UUID owner, Material material) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreventedItem item = this.itemsDao.queryBuilder().where().in("owner", owner).and().in("material", material.name()).queryForFirst();
                if (item != null) {
                    this.itemsDao.delete(item);
                }
                this.removeFromCache(owner, material);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Map {@link PreventedItem#getMaterial()} to to {@link Material}
     *
     * @param items    items to map
     * @param callback callback to use after mapping
     */
    private void mapMaterials(List<PreventedItem> items, Consumer<List<Material>> callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            List<Material> materials = new ArrayList<>();
            for (PreventedItem item : items) {
                materials.add(Material.matchMaterial(item.getMaterial()));
            }
            callback.accept(materials);
        });
    }

    /**
     * Init cache for preventer
     *
     * @param owner owner
     */
    public void initCache(UUID owner) {
        this.players.put(owner, new ArrayList<>());
    }

    /**
     * Add a list of prevented materials to cache
     *
     * @param owner     preventer of the items
     * @param materials a list of materials to prevent
     */
    private void addToCache(UUID owner, List<Material> materials) {
        this.getPlayers().put(owner, materials);
    }

    /**
     * Get an optional list of prevented materials
     *
     * @param owner preventer of the items
     * @return returns a list of materials or empty
     */
    private Optional<List<Material>> getFromCache(UUID owner) {
        if (this.getPlayers().containsKey(owner)) {
            return Optional.of(this.getPlayers().get(owner));
        }

        return Optional.empty();
    }

    /**
     * Updates an item to the cache
     *
     * @param owner    preventer of the items
     * @param material material to add
     */
    private void updateToCache(UUID owner, Material material) {
        this.getPlayers().get(owner).add(material);
    }

    /**
     * Remove the prevented item from cache
     *
     * @param owner    preventer of items
     * @param material Material
     */
    private void removeFromCache(UUID owner, Material material) {
        this.getPlayers().get(owner).remove(material);
    }

}
