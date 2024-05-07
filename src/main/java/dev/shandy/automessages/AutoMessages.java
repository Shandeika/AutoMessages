package dev.shandy.automessages;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


record Message(String style, List<String> lines, String color, String link, String command) {

}

public final class AutoMessages extends JavaPlugin {
    private File configFile;
    private FileConfiguration config;
    private static final String SPIGOT_RESOURCE_ID = "116446";
    private ArrayList<BukkitTask> message_tasks;

    @Override
    public void onEnable() {
        // Подключение проверки обновлений
        new UpdateChecker(this, UpdateCheckSource.SPIGOT, SPIGOT_RESOURCE_ID)
                .checkEveryXHours(24)
                .setDonationLink("https://shandy.dev/donate")
                .setDownloadLink(Integer.parseInt(SPIGOT_RESOURCE_ID))
                .onFail((commandSenders, exception) -> {
                    getLogger().warning("Failed to check for updates: " + exception.getMessage());
                })
                .setNotifyOpsOnJoin(true)
                .checkNow();

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

        // Команда "am"
        Objects.requireNonNull(this.getCommand("am")).setExecutor(new AutoMessagesCommand());

        var text = String.format("[%s] Plugin enabled", getName());
        getServer().getConsoleSender().sendMessage(Component.text(text).color(NamedTextColor.GREEN));
    }

    @Override
    public void onDisable() {
        var text = String.format("[%s] Plugin disabled", getName());
        getServer().getConsoleSender().sendMessage(Component.text(text).color(NamedTextColor.RED));
    }

    public class AutoMessagesCommand implements CommandExecutor {

        public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
            if (command.getName().equalsIgnoreCase("am")) {
                if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    reloadConfig();
                    config = getConfig();
                    startMessageTask();
                    var text = String.format("[%s] Configuration reloaded", getName());
                    sender.sendMessage(Component.text(text));
                    return true;
                }
            }
            return false;
        }
    }

    private void startMessageTask() {
        var text = String.format("[%s] Start message task", getName());
        getServer().getConsoleSender().sendMessage(Component.text(text));
        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            if (message_tasks != null) {
                message_tasks.forEach(BukkitTask::cancel);
            }
            message_tasks = new ArrayList<>();
            for (String key : messagesSection.getKeys(false)) {
                String prefix = messagesSection.getString(key + ".prefix", "");
                String prefixColor = messagesSection.getString(key + ".prefix-color", "white");
                String prefixStyle = messagesSection.getString(key + ".prefix-style", "");
                String prefixLink = messagesSection.getString(key + ".prefix-link", "");
                String prefixCommand = messagesSection.getString(key + ".prefix-command", "");
                final Component prefixComponent = stringToComponent(prefix, prefixColor, prefixStyle, prefixLink, prefixCommand);

                List<Map<?, ?>> messageLinesMaps = messagesSection.getMapList(key + ".message-lines");
                List<Message> messages = getMessages(messageLinesMaps);

                int interval = messagesSection.getInt(key + ".interval");

                var task = Bukkit.getScheduler().runTaskTimer(this, () -> {
                    for (Message message : messages) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            Component messageComponent = stringToComponent(
                                    String.join("\n", message.lines()),
                                    message.color(),
                                    message.style(),
                                    message.link(),
                                    message.command()
                            );
                            player.sendMessage(Component.text().append(prefixComponent).append(messageComponent).build());
                        }
                    }
                }, 0L, 20L * interval);
                message_tasks.add(task);
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
                String command = "";
                if (line.containsKey("command")) {
                    command = (String) line.get("command");
                }
                messages.add(new Message(style, Collections.singletonList(text), color, link, command));
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

    public Component stringToComponent(@NotNull String text, @NotNull String color, String style, String link, String command) {
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
        if (command != null && command.startsWith("/")) component = component.clickEvent(ClickEvent.suggestCommand(command));
        return component;
    }
}
