package net.weesli.rozsLib;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import java.util.List;

/**
 *  The extends for this class is used
 *  @Author Weesli
 *
 */

public abstract class CommandBuilder {

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
    public void build(){
        plugin.getServer().getPluginCommand(command).setExecutor(this::Command);
        plugin.getServer().getPluginCommand(command).setTabCompleter(this::TabComplete);
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
