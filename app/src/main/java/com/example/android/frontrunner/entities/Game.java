package com.example.android.frontrunner.entities;

import java.io.Serializable;

/**
 * Created by dev on 18/09/2014.
 */
public class Game implements Serializable{

    private int id;
    private int participant;
    private int participant_status;
    private double latitude;
    private double longitude;
    private int gameCreator;

    public int getGameCreator() {
        return gameCreator;
    }

    public void setGameCreator(int gameCreator) {
        this.gameCreator = gameCreator;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private String game_name;

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public int getParticipant() {
        return participant;
    }

    public void setParticipant(int participant) {
        this.participant = participant;
    }

    public int getParticipant_status() {
        return participant_status;
    }

    public void setParticipant_status(int participant_status) {
        this.participant_status = participant_status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
