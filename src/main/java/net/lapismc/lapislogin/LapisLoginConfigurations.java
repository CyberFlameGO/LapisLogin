/*
 * Copyright 2017 Benjamin Martin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.lapismc.lapislogin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LapisLoginConfigurations {

    public String primaryColor = ChatColor.AQUA.toString();
    public String secondaryColor = ChatColor.DARK_AQUA.toString();
    LapisLogin plugin;
    YamlConfiguration messages;
    File messagesFile;

    public LapisLoginConfigurations(LapisLogin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        configVersion();
        new File(plugin.getDataFolder(), "PlayerData").mkdirs();
        primaryColor = ChatColor.translateAlternateColorCodes('&', getMessages().getString("PrimaryColor"));
        secondaryColor = ChatColor.translateAlternateColorCodes('&', getMessages().getString("SecondaryColor"));
    }

    private void configVersion() {
        if (plugin.getConfig().getInt("ConfigVersion") != 2) {
            File oldConfig = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config_old.yml");
            File newConfig = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml");
            if (!newConfig.renameTo(oldConfig)) {
                plugin.logger.info(plugin.getName() + " failed to update the config.yml");
            }
            plugin.saveDefaultConfig();

            getMessages();
            File oldMessages = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "Messages_old.yml");
            if (!messagesFile.renameTo(oldMessages)) {
                plugin.logger.info(plugin.getName() + " failed to update the Messages.yml");
            }
            messages = null;
            messagesFile = null;
            getMessages();
            plugin.logger.info("New Configuration Generated for " + plugin.getName() + "," +
                    " Please Transfer Values From config_old.yml & Messages_old.yml");
        }
    }

    private YamlConfiguration getMessages() {
        if (messages == null) {
            if (messagesFile == null) {
                messagesFile = new File(plugin.getDataFolder() + File.separator + "Messages.yml");
                if (!messagesFile.exists()) {
                    plugin.saveResource("Messages.yml", false);
                }
            }
            messages = YamlConfiguration.loadConfiguration(messagesFile);
        }
        return messages;
    }

    public String getColoredMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', getMessages().getString(path).replace("&p", primaryColor).replace("&s", secondaryColor));
    }

    public String getMessage(String path) {
        return ChatColor.stripColor(getColoredMessage(path));
    }

}
