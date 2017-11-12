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

package net.lapismc.lapislogin.util;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;

public class MySQLDatabaseTool {

    String url = "jdbc:mysql://%URL%/%DBName%";
    String username;
    String password;
    String DBName;
    FileConfiguration config;
    Connection conn;

    public MySQLDatabaseTool(FileConfiguration config) {
        this.config = config;
        this.username = config.getString("Database.username");
        this.password = config.getString("Database.password");
        this.DBName = config.getString("Database.dbName");
        url = url.replace("%URL%", config.getString("Database.location")).replace("%DBName%", DBName);
        setupDatabase(username, password);
    }

    public void addData(String ID, String password, Long login, Long logout, String IP) {
        try {
            conn = getConnection();
            String sql = "INSERT INTO loginPlayers(UUID,Password,Login,Logout,IPAddress) VALUES(?,?,?,?,?)";
            PreparedStatement pareStatement = conn.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            pareStatement.setString(1, ID);
            pareStatement.setString(2, password);
            pareStatement.setLong(3, login);
            pareStatement.setLong(4, logout);
            pareStatement.setString(5, IP);
            pareStatement.execute();
            pareStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setData(String ID, String item, Object data) {
        try {
            conn = getConnection();
            String sqlUpdate = "UPDATE loginPlayers SET ? = ? WHERE id = ?";
            PreparedStatement pareStatement = conn.prepareStatement(sqlUpdate);
            pareStatement.setString(1, item);
            pareStatement.setObject(2, data);
            pareStatement.setString(3, ID);
            pareStatement.execute();
            pareStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object getData(String ID, String item) {
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT " + item + " WHERE UUID = " + ID + " FROM loginPlayers";
            ResultSet rs = stmt.executeQuery(sql);
            if (!rs.isBeforeFirst()) {
                rs.close();
                return null;
            }
            rs.next();
            Object data = rs.getObject(item);
            rs.close();
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getRows() {
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM loginPlayers";
            ResultSet rs = stmt.executeQuery(sql);
            rs.last();
            return rs.getRow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void setupDatabase(String username, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "CREATE DATABASE IF NOT EXISTS " + DBName;
            stmt.execute(sql);
            stmt = conn.createStatement();
            sql = "CREATE TABLE IF NOT EXISTS loginPlayers (" +
                    "UUID BLOB NOT NULL," +
                    "Password BLOB," +
                    "Login BIGINT," +
                    "Logout BIGINT," +
                    "IPAddress BLOB)";
            stmt.execute(sql);
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() {
        try {
            if (conn == null) {
                return DriverManager.getConnection(url, username, password);
            } else {
                return conn;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
