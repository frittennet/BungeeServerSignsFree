/*
 * stealth-coders (c) 2016  All rights reserved.
 * Copyright by stealth-coders:
 * You are NOT allowed to share, upload or decompile this plugin at any time.
 * You are NOT allowed to share, upload or use code parts/snippets of this plugin without our consent.
 * You are allowed to use this software only for yourself and/or your server/servers.
 * The respective Owner of this Software is stealth-coders.
 */
package me.Bentipa.BungeeSignsFree;

import com.google.gson.Gson;
import java.util.List;

/**
 *
 * @author Benjamin
 */
public class Test {

    private static Gson gson = new Gson();

    public static void main(String[] args) {
        String json = "{\"description\":{\"text\":\"Waiting\"},\"players\":{\"max\":8,\"online\":0},\"version\":{\"name\":\"Spigot 1.9\",\"protocol\":107}}";
        StatusResponse2 response = null;
        try{
         response = gson.fromJson(json, StatusResponse2.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println(response);
        System.out.println(response.getDescription());
        System.out.println(response.getFavicon());
        System.out.println(response.getPlayers().getMax());
        System.out.println(response.getPlayers().getOnline());
    }

    public class StatusResponse2 {

//        private String description;
        private Players players;
        private Version version;
        private String favicon;
        private Description description;
        private int time;

//        public String getDescription() {
//            return description;
//        }

        public Players getPlayers() {
            return players;
        }
        
        public String getDescription(){
            return description.getText();
        }

        public Version getVersion() {
            return version;
        }

        public String getFavicon() {
            return favicon;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

    }
    
    public class Description{
        private String text;
        
        public String getText(){
            return this.text;
        }
    }

    public class Players {

        private int max;
        private int online;
        private List<Player> sample;

        public int getMax() {
            return max;
        }

        public int getOnline() {
            return online;
        }

        public List<Player> getSample() {
            return sample;
        }
    }

    public class Player {

        private String name;
        private String id;

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

    }

    public class Version {

        private String name;
        private String protocol;

        public String getName() {
            return name;
        }

        public String getProtocol() {
            return protocol;
        }
    }
}
