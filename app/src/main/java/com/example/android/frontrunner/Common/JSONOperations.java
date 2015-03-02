package com.example.android.frontrunner.Common;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.android.frontrunner.MainActivity;
import com.example.android.frontrunner.entities.Game;
import com.example.android.frontrunner.entities.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melos on 9/27/2014.
 */
public class JSONOperations {
    //private Context appContext;

    public JSONOperations(Context context){
    }

    public static List<Game> convertJSONtoGames(String jsonInput) {
        List<Game> gamesList = new ArrayList<Game>() {
        };
        try {
            JSONArray gamesJSON = new JSONArray(jsonInput);

            for (int i = 0; i < gamesJSON.length(); i++) {
                Game game = new Game();
                JSONObject gameJSON = gamesJSON.getJSONObject(i);
                game.setId(gameJSON.getInt("_id"));
                game.setGame_name(gameJSON.getString("gameName"));
                game.setParticipant(gameJSON.getInt("participant"));
                game.setParticipant_status(gameJSON.getInt("participant_status"));
                game.setGameCreator(gameJSON.getInt("gameCreator"));
                game.setLatitude(gameJSON.getDouble("latitude"));
                game.setLongitude(gameJSON.getDouble("longitude"));
                gamesList.add(game);
            }

        } catch (JSONException jsonExc) {
            jsonExc.printStackTrace();
        }

        return gamesList;
    }

    public static synchronized String fetchJSONFromString(String queryUri)
    {
        StringBuffer stringBuffer = new StringBuffer();
         HttpURLConnection myConnection = null;
         BufferedReader reader = null;

        try {

            URL url = new URL(queryUri);
            myConnection = (HttpURLConnection) url.openConnection();
            myConnection.setRequestMethod("GET");
            myConnection.connect();

            InputStream inputStream = myConnection.getInputStream();
            if (inputStream == null) {
                // Nothing to do.
                Toast.makeText(MainActivity.mainActivity, "No data", Toast.LENGTH_LONG).show();
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }

            if (stringBuffer == null)
                Toast.makeText(MainActivity.mainActivity, "No data", Toast.LENGTH_LONG).show();

        } catch (MalformedURLException urlerror) {
            urlerror.printStackTrace();
        } catch (IOException ioe) {
            // Toast.makeText(appContext, ioe.getMessage(), Toast.LENGTH_LONG).show();
            ioe.printStackTrace();
        }

        finally {
            if (myConnection != null) {
                myConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuffer.toString();

        }



    public static List<User> convertJSONToUsers(String jsonInput) {
        List<User> usersList = new ArrayList<User>() {
        };

        try {

            JSONArray usersJSONArray = new JSONArray(jsonInput);
            for (int i = 0; i < usersJSONArray.length(); i++) {
                User user = new User();
                JSONObject userJSON = usersJSONArray.getJSONObject(i);
                user.setId(userJSON.getInt("id"));
                user.setName(userJSON.getString("Name"));
                user.setNickname(userJSON.getString("nickname"));
                user.setPhoto(userJSON.getString("photo"));
                user.setUserName(userJSON.getString("username"));
                user.setPassword(userJSON.getString("password"));

                usersList.add(user);
            }
        } catch (JSONException jsonExc) {
            jsonExc.printStackTrace();
        }

        return usersList;
    }
}
