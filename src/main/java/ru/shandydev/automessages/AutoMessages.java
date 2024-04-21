package ru.shandydev.automessages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


record Message(String style, List<String> lines, String color, String link) {

}

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

        getServer().getConsoleSender().sendMessage(Component.text("[Auto Messages] Plugin enabled").color(NamedTextColor.GREEN));
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(Component.text("[Auto Messages] Plugin disabled").color(NamedTextColor.RED));
    }

    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("am")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                config = getConfig();
                startMessageTask();
                sender.sendMessage("[Auto Messages] Конфигурация перезагружена.");
                return true;
            }
        }
        return false;
    }

    private void startMessageTask() {
        getServer().getConsoleSender().sendMessage("[Auto Messages] Запуск таймера для отправки сообщений");
        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (String key : messagesSection.getKeys(false)) {
                String prefix = messagesSection.getString(key + ".prefix", "");
                String prefixColor = messagesSection.getString(key + ".prefix-color", "white");
                String prefixStyle = "";
                String prefixLink = "";
                final Component prefixComponent = stringToComponent(prefix, prefixColor, prefixStyle, prefixLink);

                List<Map<?, ?>> messageLinesMaps = messagesSection.getMapList(key + ".message-lines");
                List<Message> messages = getMessages(messageLinesMaps);

                int interval = messagesSection.getInt(key + ".interval");

                Bukkit.getScheduler().runTaskTimer(this, () -> {
                    for (Message message : messages) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            Component messageComponent = stringToComponent(
                                    String.join("\n", message.lines()),
                                    message.color(),
                                    message.style(),
                                    message.link()
                            );
                            player.sendMessage(Component.text().append(prefixComponent).append(messageComponent).build());
                        }
                    }
                }, 0L, 20L * interval);
            }
        }
    }

    @NotNull
    private static List<Message> getMessages(List<Map<?, ?>> messageLinesMaps) {
        List<Message> messages = new ArrayList<>();
        for (Map<?, ?> lineMap : messageLinesMaps) {
            for (Map.Entry<?, ?> entry : lineMap.entrySet()) {
                Map<?, ?> line = (Map<?, ?>) entry.getValue();
                String text = (String) line.get("text");
                String color = (String) line.get("color");
                String style = (String) line.get("style");
                String link = "";
                if (line.containsKey("link")) {
                    link = (String) line.get("link");
                }
                messages.add(new Message(style, Collections.singletonList(text), color, link));
            }
        }
        return messages;
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            getServer().getConsoleSender().sendMessage(Component.text("[Auto Messages] Не удалось сохранить конфиг").color(NamedTextColor.RED));
        }
    }

    @Override
    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            try (InputStream is = getResource("config.yml")) {
                Files.copy(is, Paths.get(configFile.toURI()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Component stringToComponent(@NotNull String text, @NotNull String color, String style, String link) {
        final Map<String, NamedTextColor> colorMap = Map.ofEntries(
                Map.entry("aqua", NamedTextColor.AQUA),
                Map.entry("black", NamedTextColor.BLACK),
                Map.entry("blue", NamedTextColor.BLUE),
                Map.entry("dark_aqua", NamedTextColor.DARK_AQUA),
                Map.entry("dark_blue", NamedTextColor.DARK_BLUE),
                Map.entry("dark_gray", NamedTextColor.DARK_GRAY),
                Map.entry("dark_green", NamedTextColor.DARK_GREEN),
                Map.entry("dark_purple", NamedTextColor.DARK_PURPLE),
                Map.entry("dark_red", NamedTextColor.DARK_RED),
                Map.entry("gold", NamedTextColor.GOLD),
                Map.entry("gray", NamedTextColor.GRAY),
                Map.entry("green", NamedTextColor.GREEN),
                Map.entry("light_purple", NamedTextColor.LIGHT_PURPLE),
                Map.entry("red", NamedTextColor.RED),
                Map.entry("white", NamedTextColor.WHITE),
                Map.entry("yellow", NamedTextColor.YELLOW)
        );
        final Map<String, TextDecoration> styleMap = Map.ofEntries(
                Map.entry("bold", TextDecoration.BOLD),
                Map.entry("italic", TextDecoration.ITALIC),
                Map.entry("strikethrough", TextDecoration.STRIKETHROUGH),
                Map.entry("underlined", TextDecoration.UNDERLINED),
                Map.entry("obfuscated", TextDecoration.OBFUSCATED)
        );
        var component = Component.text(text).color(colorMap.getOrDefault(color, NamedTextColor.WHITE));
        if (style != null && !style.isEmpty() && styleMap.containsKey(style)) component = component.decorate(styleMap.get(style));
        if (link != null && !link.isEmpty()) component = component.clickEvent(ClickEvent.openUrl(link));
        return component;
    }
}
