package com.purduecs.kiwi.oneup.web;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;
import com.purduecs.kiwi.oneup.R;
import com.purduecs.kiwi.oneup.models.Challenge;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Adam on 2/25/16.
 */
public class ChallengesWebRequest implements OneUpWebRequest<JSONObject, ArrayList<Challenge>> {

    Request mRequest;

    public ChallengesWebRequest(String type, int start, int length, final RequestHandler<ArrayList<Challenge>> handler) {

        /*Request request = new JsonObjectRequest(Request.Method.GET, OneUpWebRequest.BASE_URL + "/challenges", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handler.onSuccess(parseResponse(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.onFailure();
            }
        });*/

        switch (type) {
            case "new":
            case "popular":
                type = "/challenges/local/" + type;
                break;
            case "bookmarks":
                type = "/bookmarks/";
                break;
            case "global":
            default:
                type = "/challenges/";
                break;
        }

        Map<String, String> headerArgs = new ArrayMap<String, String>();;
        headerArgs.put("offset", Integer.toString(start));
        headerArgs.put("limit", Integer.toString(length));

        mRequest = new JsonObjectEditHeaderRequest(Request.Method.GET, OneUpWebRequest.BASE_URL + type, headerArgs, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handler.onSuccess(parseResponse(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.onFailure();
            }
        });

        RequestQueueSingleton.getInstance(OneUpApplication.getAppContext()).addToRequestQueue(mRequest);
    }

    String[] winners = new String[] {"loeb", "mkausas", "dalal", "semenza", "christiansen", "craven"};
    java.util.Random r = new java.util.Random();

    @Override
    public ArrayList<Challenge> parseResponse(JSONObject response2) {

        JSONArray response = null;
        try {
            response = response2.getJSONArray("docs");
        } catch (Exception e) {
            ;
        }

        ArrayList<Challenge> c = new ArrayList<Challenge>();
        try {
            int ind = 0;
            JSONObject chall;
            Challenge challe;
            while (!response.isNull(ind)) {
                chall = response.getJSONObject(ind++);
                challe = new Challenge();
                challe.id = chall.getString("_id");
                challe.name = chall.getString("name");
                challe.image = chall.getJSONArray("attempts").getJSONObject(0).getString("gif_img");
                challe.categories = chall.getJSONArray("categories").toString()
                        .replace("\"", "").replace("[", "").replace("]", "").split(",");
                challe.owner = winners[r.nextInt(winners.length)];
                challe.score = r.nextInt(1000);
                challe.time = r.nextInt(10);
                challe.desc = "lots of placeholder text yo so this looks like a pretty high quality description";
                challe.previewImage = chall.getJSONArray("attempts").getJSONObject(0).getString("preview_img");
                c.add(challe);
            }
        } catch (Exception e) {

        }
        return c;
    }

    @Override
    public boolean cancelRequest() {
        if (mRequest.isCanceled()) return false;
        mRequest.cancel();
        return true;
    }

    private static Challenge[] toChallengesArray(ArrayList<Challenge> challenges)
    {
        Challenge[] ret = new Challenge[challenges.size()];
        Iterator<Challenge> iterator = challenges.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next();
        }
        return ret;
    }
}
