package dev.shandy.automessages;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Objects;

public class Localization {
    private final FileConfiguration local_yml;
    private final FileConfiguration default_translate;

    public Localization(JavaPlugin plugin, String locale_code) {
        default_translate = YamlConfiguration.loadConfiguration(
                new BufferedReader(
                        new InputStreamReader(
                                Objects.requireNonNull(plugin.getResource("localizations/en.yml"))
                        )
                )
        );
        var locale_file = new File(plugin.getDataFolder(), "localizations/"+locale_code + ".yml");
        if (!locale_file.exists()) {
            if (plugin.getResource("localizations/" + locale_code + ".yml") != null) {
                plugin.saveResource("localizations/" + locale_code + ".yml", false);

            } else {
                plugin.getLogger().warning(String.format("Locale file %s.yml not found, using default translation", locale_code));
                if (!new File(plugin.getDataFolder(), "localizations/en.yml").exists()) {
                    plugin.saveResource("localizations/en.yml", false);
                }
                locale_file = new File(plugin.getDataFolder(), "localizations/en.yml");
            }
        }

        local_yml = YamlConfiguration.loadConfiguration(locale_file);
    }

    public String localize(String key, Object... args) {
        if (!local_yml.contains(key)) {
            return String.format(default_translate.getString(key, key), args);
        }
        return String.format(Objects.requireNonNull(local_yml.get(key)).toString(), args);
    }
}
