package io.github.openminigameserver.worldedit.platform.config;

import com.sk89q.worldedit.LocalConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;

public class WorldEditConfiguration extends LocalConfiguration {
    @Override
    public void load() {
    }

    @Override
    public Path getWorkingDirectoryPath() {
        return Paths.get("server/extensions/WorldEdit");
    }

    /*@Unreported
    @Nullable
    private CommentedConfigurationNode node;
    @Unreported
    @NotNull
    private final ConfigurationLoader config;
    @Unreported
    @NotNull
    private final Logger logger;

    public WorldEditConfiguration(@NotNull ConfigurationLoader config, @NotNull Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    @Nullable
    protected final CommentedConfigurationNode getNode() {
        return this.node;
    }

    protected final void setNode(@Nullable CommentedConfigurationNode var1) {
        this.node = var1;
    }

    @NotNull
    public Path getWorkingDirectoryPath() {
        return MinestomWorldEdit.getInstance().getDataFolder().toPath();
    }

    public void load() {
        try {
            ConfigurationOptions options = ConfigurationOptions.defaults();
            options = options.shouldCopyDefaults(true);
            this.node = (CommentedConfigurationNode) this.config.load(options);
        } catch(IOException e) {
            this.logger.warn("Error loading WorldEdit configuration", e);
        }

        this.profile = (this.node.node("debug")).getBoolean(this.profile);
        this.traceUnflushedSessions = (this.node.node("debugging", "trace-unflushed-sessions")).getBoolean(this.traceUnflushedSessions);

        String wandItem = (this.node.node("wand-item")).getString(this.wandItem);
        if(wandItem == null) {
            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
        }

        this.wandItem = wandItem.toLowerCase();;
        try {
            this.wandItem = LegacyMapper.getInstance()
                    .getItemFromLegacy(Integer.parseInt(wandItem)).getId();
        } catch(Exception ignored) {
        }


        this.defaultChangeLimit = Math.max(-1, (this.node.node("limits", "max-blocks-changed", "default")).getInt(this.defaultChangeLimit));
        this.maxChangeLimit = Math.max(-1, (this.node.node("limits", "max-blocks-changed", "maximum")).getInt(this.maxChangeLimit));
        this.defaultVerticalHeight = Math.max(1, (this.node.node("limits", "vertical-height", "default")).getInt(this.defaultVerticalHeight));
        this.defaultMaxPolygonalPoints = Math.max(-1, (this.node.node("limits", "max-polygonal-points", "default")).getInt(this.defaultMaxPolygonalPoints));;
        this.maxPolygonalPoints = Math.max(-1, (this.node.node("limits", "max-polygonal-points", "maximum")).getInt(this.maxPolygonalPoints));
        this.maxRadius = Math.max(-1, (this.node.node("limits", "max-radius")).getInt(this.maxRadius));
        this.maxBrushRadius = (this.node.node("limits", "max-brush-radius")).getInt(this.maxBrushRadius);
        this.maxSuperPickaxeSize = Math.max(1, (this.node.node("limits", "max-super-pickaxe-size")).getInt(this.maxSuperPickaxeSize));
        this.butcherDefaultRadius = Math.max(-1, (this.node.node("limits", "butcher-radius", "default")).getInt(this.butcherDefaultRadius));
        this.butcherMaxRadius = Math.max(-1, (this.node.node("limits", "butcher-radius", "maximum")).getInt(this.butcherMaxRadius));

        try {
            List<String> disallowedBlocks = (this.node.node("limits", "disallowed-blocks")).getList(String.class);
            if(disallowedBlocks != null) {
                this.disallowedBlocks = new HashSet<>(disallowedBlocks);
            }
        } catch(SerializationException e) {
            this.logger.warn("Error loading WorldEdit configuration", e);
        }

        try {
            List<String> allowedDataCycleBlocks = (this.node.node("limits", "allowed-data-cycle-blocks")).getList(String.class);
            if(allowedDataCycleBlocks != null) {
                this.allowedDataCycleBlocks = new HashSet<>(allowedDataCycleBlocks);
            }
        } catch(SerializationException e) {
            this.logger.warn("Error loading WorldEdit configuration", e);
        }

        this.registerHelp = (this.node.node("register-help")).getBoolean(true);
        this.logCommands = (this.node.node("logging", "log-commands")).getBoolean(this.logCommands);
        this.logFile = (this.node.node("logging", "file")).getString(this.logFile);
        this.logFormat = (this.node.node("logging", "format")).getString(this.logFormat);
        this.superPickaxeDrop = (this.node.node("super-pickaxe", "drop-items")).getBoolean(this.superPickaxeDrop);
        this.superPickaxeManyDrop = (this.node.node("super-pickaxe", "many-drop-items")).getBoolean(this.superPickaxeManyDrop);
        this.useInventory = (this.node.node("use-inventory", "enable")).getBoolean(this.useInventory);
        this.useInventoryOverride = (this.node.node("use-inventory", "allow-override")).getBoolean(this.useInventoryOverride);
        this.useInventoryCreativeOverride = (this.node.node("use-inventory", "creative-mode-overrides")).getBoolean(this.useInventoryCreativeOverride);

        wandItem = (this.node.node("navigation-wand", "item")).getString(this.navigationWand);
        if(wandItem == null) {
            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
        }

        this.navigationWand = wandItem.toLowerCase();
        try {
            this.navigationWand = LegacyMapper.getInstance().getItemFromLegacy(Integer.parseInt(wandItem)).getId();
        } catch(Throwable var18) {
        }

        this.navigationWandMaxDistance = (this.node.node("navigation-wand", "max-distance")).getInt(this.navigationWandMaxDistance);
        this.navigationUseGlass = (this.node.node("navigation", "use-glass")).getBoolean(this.navigationUseGlass);
        this.scriptTimeout = (this.node.node("scripting", "timeout")).getInt(this.scriptTimeout);
        this.scriptsDir = (this.node.node("scripting", "dir")).getString(this.scriptsDir);
        this.saveDir = (this.node.node("saving", "dir")).getString(this.saveDir);
        this.allowSymlinks = (this.node.node("files", "allow-symbolic-links")).getBoolean(false);

        LocalSession.MAX_HISTORY_SIZE = Math.max(0, (this.node.node("history", "size")).getInt(15));
        SessionManager.EXPIRATION_GRACE = (this.node.node("history", "expiration")).getInt(10) * 60 * 1000;

        this.showHelpInfo = (this.node.node("show-help-on-first-use")).getBoolean(true);

        this.serverSideCUI = (this.node.node("server-side-cui")).getBoolean(true);

        String snapshotsDir = (this.node.node("snapshots", "directory")).getString("");
        boolean experimentalSnapshots = (this.node.node("snapshots", "experimental")).getBoolean(false);

        this.initializeSnapshotConfiguration(snapshotsDir, experimentalSnapshots);

        this.shellSaveType = (this.node.node("shell-save-type")).getString("").trim();
        this.extendedYLimit = (this.node.node("compat", "extended-y-limit")).getBoolean(false);

        this.setDefaultLocaleName((this.node.node("default-locale")).getString(this.defaultLocaleName));

        try {
            this.config.save(this.node);
        } catch(IOException e) {
            this.logger.warn("Error loading WorldEdit configuration", e);
        }
    }

    @NotNull
    protected final ConfigurationLoader getConfig() {
        return this.config;
    }

    @NotNull
    protected final Logger getLogger() {
        return this.logger;
    }*/

}
