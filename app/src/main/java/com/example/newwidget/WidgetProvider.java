package com.example.newwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class WidgetProvider extends android.appwidget.AppWidgetProvider {

    private static final String CLICK_ACTION = "CLICKED";
    String NameString = "";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        ComponentName componentName = new ComponentName(context, WidgetProvider.class);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setOnClickPendingIntent(R.id.widgetBtn, getPendingSelfIntent(context, CLICK_ACTION));

        appWidgetManager.updateAppWidget(componentName, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(CLICK_ACTION)) {
            new JokeLoader().execute(context);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(CLICK_ACTION);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private class JokeLoader extends AsyncTask<Context, Void, Void> {
        private Context context;
        @Override
        protected Void doInBackground(Context... contexts) {
            context = contexts[0];
            String jsonString = getJson("https://api.chucknorris.io/jokes/random");
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                NameString = jsonObject.getString("value");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            NameString="";
        }

        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, WidgetProvider.class);
            if(!NameString.equals("")){
                views.setTextViewText(R.id.textView, NameString);
            }
            appWidgetManager.updateAppWidget(componentName, views);
        }
    }

    private String getJson(String link) {
        String data = "";
        try {
            URL url = new URL(link);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),
                        "utf-8"));
                data = r.readLine();
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
