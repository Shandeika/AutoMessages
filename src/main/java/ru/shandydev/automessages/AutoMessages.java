package ru.shandydev.automessages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class AutoMessages extends JavaPlugin {
    private File configFile;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        // Создание конфигурационного файла
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        // Загрузка конфигурации
        config = YamlConfiguration.loadConfiguration(configFile);

        // Запуск таймера для отправки сообщений
        startMessageTask();
    }

    @Override
    public void onDisable() {
        // При выключении плагина сохраняем конфигурацию
        saveConfig();
    }

    private void startMessageTask() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            // Отправка каждого сообщения из конфигурации
            for (String key : config.getKeys(false)) {
                String prefix = config.getString(key + ".prefix", "");
                String[] messageLines = config.getStringList(key + ".message").toArray(new String[0]);
                int interval = config.getInt(key + ".interval");

                // Отправка каждой строки сообщения
                for (String line : messageLines) {
                    String formattedMessage = ChatColor.translateAlternateColorCodes('&', prefix + line);
                    Bukkit.broadcastMessage(formattedMessage);
                }
            }
        }, 0L, 20L); // Запускаем каждую секунду (20 тиков)
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            getLogger().warning("Не удалось сохранить конфигурацию!");
        }
    }
}
