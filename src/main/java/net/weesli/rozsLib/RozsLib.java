package net.weesli.rozsLib;

import net.weesli.rozsLib.bossbar.BossBarManager;
import net.weesli.rozsLib.events.LibListener;
import net.weesli.rozsLib.example.RLibCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class RozsLib extends JavaPlugin {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/Weesli/RLib/releases";


    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("[RLib] Starting plugin...");

        // check lib version
        if (!checkVersion()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[RLib] A new version of RLib is available. Please update!");
        }else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[RLib] You are using the latest version of RLib.");
        }

        // register lib events

        this.getServer().getPluginManager().registerEvents(new LibListener(), this);

        /**
         * Command register like this
         */
        new RLibCommand(this).setCommand("RLib").build();

    }


    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("Stopping plugin...");
        BossBarManager.getRegisteredBars().values().forEach(bossbar-> bossbar.removeAll());
    }


    private boolean checkVersion() {
        try {
            URL url = new URL(GITHUB_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONParser parser = new JSONParser();
                JSONArray releases = (JSONArray) parser.parse(response.toString());

                if (!releases.isEmpty()) {
                    JSONObject latestRelease = (JSONObject) releases.get(0);
                    String tagName = latestRelease.get("tag_name").toString();
                    if (tagName.equals(this.getDescription().getVersion())){
                        return true;
                    }else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error parsing");
        }catch (ParseException e) {
            System.out.println("Error parsing");
        }
        return false;
    }

}
