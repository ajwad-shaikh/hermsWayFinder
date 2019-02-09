package ml.ajwad.hermswayfinder;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MonitorActivity extends AppCompatActivity {

    ArrayList<String> listItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ProgressDialog pd;
    String txtJSON;
    ArrayList<String> routeSteps = new ArrayList<>();

    String newQuery;
    String querySender;
    String sourceFinal;
    String destFinal;
    String seekURI;

    String saldo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(lookForQuery, new IntentFilter("Inbox"));
        adapter = new ArrayAdapter<>(this,
                R.layout.list_row,
                listItems);
        ListView mListView = findViewById(R.id.listMonitor);
        mListView.setAdapter(adapter);
    }

    private BroadcastReceiver lookForQuery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("Inbox")) {
                newQuery = intent.getStringExtra("message");
                querySender = intent.getStringExtra("sender");
                String sourcePlace = "Source : ";
                String destPlace = "Destination : ";
                String endTag = "</hermsWay>";
                String source = newQuery.substring
                        (newQuery.indexOf(sourcePlace)+sourcePlace.length(),
                                newQuery.indexOf(destPlace)-1);
                String dest = newQuery.substring
                        (newQuery.indexOf(destPlace)+destPlace.length(),
                                newQuery.indexOf(endTag)-1);
                routeSteps.clear();
                sourceFinal = destFinal = seekURI = "";
                seekDirectionsResponse(source, dest);
                addToList(source, dest);
                sendData();
            }
        }
    };

    private void addToList(String source, String dest) {
        String timeStamp = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        String listText = "Timestamp : " + timeStamp + "\nInput Source : "
                + source + "\nInput Destination : " + dest + "\nIdentified Source : "
                + sourceFinal + "\nIdentified Destination : " + destFinal ;
        listItems.add(listText);
        adapter.notifyDataSetChanged();
    }

    private void seekDirectionsResponse(String source, String dest) {
        Resources resources = getResources();
        seekURI = resources.getString(R.string.seek_url_origin) + source +
                resources.getString(R.string.seek_url_destination) + dest +
                resources.getString(R.string.seek_url_key )+
                resources.getString(R.string.directions_api_key);
        seekURI = seekURI.replace(" ","+");
        Log.d("seekURI",seekURI);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            JSONObject response = new JSONObject(IOUtils.toString(new URL(seekURI), Charset.forName("UTF-8")));
            Log.d("response", response.toString());
            JSONArray stepsArray = response.getJSONArray("routes").getJSONObject(0)
                    .getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            sourceFinal = response.getJSONArray("routes").getJSONObject(0).
                    getJSONArray("legs").getJSONObject(0).getString("start_address");
            destFinal = response.getJSONArray("routes").getJSONObject(0).
                    getJSONArray("legs").getJSONObject(0).getString("end_address");
            for(int b = 0; b < stepsArray.length(); b++){
                JSONObject instObject = stepsArray.getJSONObject(b);
                routeSteps.add(instObject.getString("html_instructions"));
                Log.d("routes",instObject.getString("html_instructions"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendData(){
        StringBuilder smsMessage = new StringBuilder();
        for(int a = 0; a < routeSteps.size(); a++){
            Document doc = Jsoup.parse(routeSteps.get(a));
            smsMessage.append(doc.body().text()).append("\n");
        }
        String message = smsMessage.toString();
        Log.d("message", message);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage
                (querySender, null, message,
                        null, null);

    }

}
