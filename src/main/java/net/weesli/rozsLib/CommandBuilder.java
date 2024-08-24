package net.weesli.rozsLib;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import java.util.List;

/**
 *  The extends for this class is used
 *  @Author Weesli
 *
 */

public abstract class CommandBuilder implements CommandExecutor, TabCompleter {

    // creating variables
    private Plugin plugin;
    private String command;

    // constructor to set plugin in the builder class.
    public CommandBuilder(Plugin plugin){
        this.plugin = plugin;
    }

    // constructor to set command in the builder class
    public CommandBuilder setCommand(String command){
        this.command = command;
        return this;
    }

    // method to build the command, sets the executor and tabCompleter.
    public void build() {
        plugin.getServer().getPluginCommand(command).setExecutor(this);
        plugin.getServer().getPluginCommand(command).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return Command(sender, command, label, args);
    }

    // Implementing the TabCompleter interface
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return TabComplete(sender, command, alias, args);
    }

    protected abstract boolean Command(CommandSender sender, Command command, String s, String[] args);

    protected abstract List<String> TabComplete(CommandSender sender, Command command, String s, String[] args);

    public String getCommand() {
        return command;
    }

    public Plugin getPlugin() {
        return plugin;
    }

}
