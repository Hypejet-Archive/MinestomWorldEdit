package io.github.openminigameserver.worldedit.platform.config;

import com.sk89q.worldedit.LocalConfiguration;
import io.github.openminigameserver.worldedit.MinestomWorldEdit;

import java.nio.file.Path;

public class WorldEditConfiguration extends LocalConfiguration {
    @Override
    public void load() {}

    @Override
    public Path getWorkingDirectoryPath() {
        return MinestomWorldEdit.getInstance().getDataDirectory();
    }
}
