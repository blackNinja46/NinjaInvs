package live.blackninja.ninjaInvs;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class NinjaInvs {

    private static final Map<String, InventoryManager> MANAGERS = new ConcurrentHashMap<>();

    private NinjaInvs() {
    }

    public static InventoryManager init(JavaPlugin plugin) {
        return MANAGERS.computeIfAbsent(plugin.getName(), ignored -> {
            InventoryManager manager = new InventoryManager(plugin);
            manager.init();
            return manager;
        });
    }

    public static Optional<InventoryManager> get(JavaPlugin plugin) {
        return Optional.ofNullable(MANAGERS.get(plugin.getName()));
    }
}
