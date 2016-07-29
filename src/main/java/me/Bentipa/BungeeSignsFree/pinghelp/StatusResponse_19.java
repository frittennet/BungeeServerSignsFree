/*
 * stealth-coders (c) 2016  All rights reserved.
 * Copyright by stealth-coders:
 * You are NOT allowed to share, upload or decompile this plugin at any time.
 * You are NOT allowed to share, upload or use code parts/snippets of this plugin without our consent.
 * You are allowed to use this software only for yourself and/or your server/servers.
 * The respective Owner of this Software is stealth-coders.
 */
package me.Bentipa.BungeeSignsFree.pinghelp;

import java.util.List;

/**
 *
 * @author Benjamin
 */
public class StatusResponse_19{

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
    
    public String getDescription() {
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

    public class Description {

        private String text;

        public String getText() {
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
