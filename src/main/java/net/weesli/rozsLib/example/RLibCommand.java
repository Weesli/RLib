package net.weesli.rozsLib.example;

import net.weesli.rozsLib.CommandBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class RLibCommand extends CommandBuilder {


    public RLibCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected boolean Command(CommandSender sender, Command command, String s, String[] strings) {
        // Your command logic here
        sender.sendMessage("Running RLib "+getPlugin().getDescription().getVersion());
        return false;
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
        return List.of();
    }
}
