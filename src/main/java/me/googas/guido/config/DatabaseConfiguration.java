package me.googas.guido.config;

import lombok.Getter;
import lombok.NonNull;
import me.googas.guido.GuidoFiles;
import me.googas.io.StarboxFile;

public class DatabaseConfiguration {

    @NonNull @Getter
    private final DatabaseType type;
    @NonNull @Getter
    private final String url;

    public DatabaseConfiguration(@NonNull DatabaseType type, @NonNull String url) {
        this.type = type;
        this.url = url;
    }

    public DatabaseConfiguration() {
        this(DatabaseType.H2, "jdbc:" + new StarboxFile(GuidoFiles.DIR, "guido.db").getAbsoluteFile().toURI().toString().replaceFirst("^file", "h2"));
    }

    public enum DatabaseType {
        H2
    }
}
