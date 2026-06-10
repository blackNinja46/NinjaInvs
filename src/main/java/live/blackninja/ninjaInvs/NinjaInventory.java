package live.blackninja.ninjaInvs;

import live.blackninja.ninjaInvs.content.InventoryContents;
import live.blackninja.ninjaInvs.content.InventoryProvider;
import live.blackninja.ninjaInvs.opener.InventoryOpener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class NinjaInventory {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private String id;
    private Component title;
    private InventoryType type;
    private int rows, columns;
    private boolean closeable;

    private InventoryProvider provider;
    private NinjaInventory parent;

    private List<InventoryListener<? extends Event>> listeners;
    private InventoryManager manager;

    private NinjaInventory(InventoryManager manager) {
        this.manager = manager;
    }

    public Inventory open(Player player) { return open(player, 0); }
    public Inventory open(Player player, int page) {
        Optional<NinjaInventory> oldInv = this.manager.getInventory(player);

        oldInv.ifPresent(inv -> {
            inv.getListeners().stream()
                    .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                            .accept(new InventoryCloseEvent(player.getOpenInventory())));

            this.manager.setInventory(player, null);
        });

        InventoryContents contents = new InventoryContents.Impl(this, player.getUniqueId());
        contents.pagination().page(page);

        this.manager.setContents(player, contents);

        try {
            this.provider.init(player, contents);

            // If the current inventory has been closed or replaced within the init method, returns
            if (!this.manager.getContents(player).equals(Optional.of(contents))) {
                return null;
            }

            InventoryOpener opener = this.manager.findOpener(type)
                    .orElseThrow(() -> new IllegalStateException("No opener found for the inventory type " + type.name()));
            Inventory handle = opener.open(this, player);

            this.manager.setInventory(player, this);

            return handle;
        } catch (Exception e) {
            this.manager.handleInventoryOpenError(this, player, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public void close(Player player) {
        listeners.stream()
                .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                        .accept(new InventoryCloseEvent(player.getOpenInventory())));

        this.manager.setInventory(player, null);
        player.closeInventory();

        this.manager.setContents(player, null);
    }

    public String getId() { return id; }
    public Component getTitle() { return title; }
    public InventoryType getType() { return type; }
    public int getRows() { return rows; }
    public int getColumns() { return columns; }

    public boolean isCloseable() { return closeable; }
    public void setCloseable(boolean closeable) { this.closeable = closeable; }

    public InventoryProvider getProvider() { return provider; }
    public Optional<NinjaInventory> getParent() { return Optional.ofNullable(parent); }

    public InventoryManager getManager() { return manager; }

    List<InventoryListener<? extends Event>> getListeners() { return listeners; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {

        private String id = "unknown";
        private Component title = Component.empty();
        private InventoryType type = InventoryType.CHEST;
        private int rows = 6, columns = 9;
        private boolean closeable = true;

        private InventoryManager manager;
        private JavaPlugin plugin;
        private InventoryProvider provider;
        private NinjaInventory parent;

        private List<InventoryListener<? extends Event>> listeners = new ArrayList<>();

        private Builder() {}

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = Component.text(title);
            return this;
        }

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder miniMessageTitle(String title) {
            this.title = MINI_MESSAGE.deserialize(title);
            return this;
        }

        public Builder type(InventoryType type) {
            this.type = type;
            return this;
        }

        public Builder size(int rows, int columns) {
            this.rows = rows;
            this.columns = columns;
            return this;
        }

        public Builder closeable(boolean closeable) {
            this.closeable = closeable;
            return this;
        }

        public Builder provider(InventoryProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder parent(NinjaInventory parent) {
            this.parent = parent;
            return this;
        }

        public Builder listener(InventoryListener<? extends Event> listener) {
            this.listeners.add(listener);
            return this;
        }

        public Builder manager(InventoryManager manager) {
            this.manager = manager;
            return this;
        }

        public Builder plugin(JavaPlugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public NinjaInventory build() {
            if(this.provider == null)
                throw new IllegalStateException("The provider of the NinjaInventory.Builder must be set.");

            InventoryManager resolvedManager = this.manager;

            if (resolvedManager == null && this.plugin != null) {
                resolvedManager = NinjaInvs.init(this.plugin);
            }

            if (resolvedManager == null) {
                throw new IllegalStateException("The manager of the NinjaInventory.Builder must be set, "
                        + "or a host plugin must be provided via plugin(...).");
            }

            NinjaInventory inv = new NinjaInventory(resolvedManager);
            inv.id = this.id;
            inv.title = this.title;
            inv.type = this.type;
            inv.rows = this.rows;
            inv.columns = this.columns;
            inv.closeable = this.closeable;
            inv.provider = Objects.requireNonNull(this.provider, "provider");
            inv.parent = this.parent;
            inv.listeners = this.listeners;

            return inv;
        }
    }

}
