package me.malaguti.tChat;

import me.malaguti.tChat.commands.GlobalChannelCommandExecutor;
import me.malaguti.tChat.commands.PrivateChannelCommandExecutor;
import me.malaguti.tChat.commands.TChatCommandExecutor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public final class TChat extends JavaPlugin {

    private LuckPerms luckPerms;
    private FileConfiguration config;
    private final HashMap<Player, String> playerChatMode = new HashMap<>(); // Armazena o chat padrão do jogador

    @Override
    public void onEnable() {
        getLogger().info("Tchat enabled!");

        createMainConfig();
        loadMessagesConfig();

        // Registrando o luckperms
        this.luckPerms = getServer().getServicesManager().load(LuckPerms.class);

        // Registrando os comandos
        registerCommands();

        // Registrando o listener de chat local
        getServer().getPluginManager().registerEvents(new LocalChatListener(this), this);

    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("tchat")).setExecutor(new TChatCommandExecutor(this));
        Objects.requireNonNull(getCommand("g")).setExecutor(new GlobalChannelCommandExecutor(this));
        Objects.requireNonNull(getCommand("tell")).setExecutor(new PrivateChannelCommandExecutor(this));
        Objects.requireNonNull(getCommand("chat")).setExecutor(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("TChat disabled!");
    }

    public void createMainConfig() {
        // Salva o config.yml padrão se ele não existir
        File configFile = new File(getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            saveDefaultConfig();
        }
    }

    public void loadMessagesConfig() {
        // Lista de arquivos de mensagens suportados
        String[] supportedLanguages = {"en", "pt-BR"};

        // Cria todos os arquivos de mensagens se não existirem
        for(String language : supportedLanguages) {
            File languageFile = new File(getDataFolder(), "messages/" + language + ".yml");
            if(!languageFile.exists()) {
                saveResource("messages/" + language + ".yml", false);
            }
        }

        // Obtém o nome do arquivo de mensagens especificado no config.yml
        String messageFileName = getConfig().getString("language-file", "en");
        File configFile = new File(getDataFolder(), "messages/" + messageFileName + ".yml");

        // Carrega o arquivo de configuração de mensagens
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfigMessages() {
        return config;
    }

    public int getChatRange() {
        return getConfig().getInt("chat-range", 100); // 100 é o valor padrão se não estiver definido
    }

    public String getPlayerChatMode(Player player) {
        return playerChatMode.getOrDefault(player, "local"); // Retorna 'local' como padrão
    }

    public void setPlayerChatMode(Player player, String mode) {
        playerChatMode.put(player, mode);
    }

    public String getPrefix(Player player) {
        if(this.luckPerms != null) {
            CachedMetaData metaData = this.luckPerms.getPlayerAdapter(Player.class).getMetaData(player);
            String prefix = metaData.getPrefix();
            return prefix != null ? prefix : "";
        } else {
            getLogger().warning("§cLuckPerms is not initialized correctly.");
            return "";
        }
    }

    public void sendGlobalMessage(Player sender, String message) {
        if (sender.hasPermission("tchat.colors")) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        String prefix = getPrefix(sender);
        String globalMessage = Objects.requireNonNull(getConfigMessages().getString("global_chat"))
                .replace("%player%", sender.getName())
                .replace("%prefix%", prefix);

        globalMessage = ChatColor.translateAlternateColorCodes('&', globalMessage);
        Bukkit.broadcastMessage(globalMessage + message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("chat")) {
            if(sender instanceof Player player) {
                if(args.length > 0) {
                    String mode = args[0].toLowerCase();
                    if(mode.equals("global") || mode.equals("local")) {
                        setPlayerChatMode(player, mode);
                        String message = Objects.requireNonNull(getConfigMessages().getString("chat_mode_set"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message) + mode);
                    } else {
                        String message = Objects.requireNonNull(getConfigMessages().getString("warn_mode_set"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                } else {
                    String message = Objects.requireNonNull(getConfigMessages().getString("warn_mode_set"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            } else {
                sender.sendMessage("§cOnly players can use this command.");
            }
            return true;
        }
        return false;
    }
}
