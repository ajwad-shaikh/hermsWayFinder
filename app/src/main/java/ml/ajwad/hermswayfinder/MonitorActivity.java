package ml.ajwad.hermswayfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.Timestamp;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MonitorActivity extends AppCompatActivity {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private ListView mListView;

    String newQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(lookForQuery, new IntentFilter("Inbox"));
        adapter = new ArrayAdapter<String>(this,
                R.layout.list_row,
                listItems);
        mListView = (ListView) findViewById(R.id.listMonitor);
        mListView.setAdapter(adapter);
    }

    private BroadcastReceiver lookForQuery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("Inbox")) {
                newQuery = intent.getStringExtra("message");
                String sourcePlace = "Source : ";
                String destPlace = "Destination : ";
                String endTag = "</hermsWay>";
                String source = newQuery.substring
                        (newQuery.indexOf(sourcePlace)+sourcePlace.length(),
                                newQuery.indexOf(destPlace));
                String dest = newQuery.substring
                        (newQuery.indexOf(destPlace)+destPlace.length(),
                                newQuery.indexOf(endTag)-1);
                addToList(source, dest);

            }
        }
    };

    private void addToList(String source, String dest) {
        Timestamp tsTemp = new Timestamp((int) System.currentTimeMillis());
        String timeStamp = tsTemp.toString();
        String listText = "Timestamp : " + timeStamp + "\nSource : "
                + source + "\nDestination : " + dest;
        listItems.add(listText);
        adapter.notifyDataSetChanged();
    }

    private void seekDirectionsResponse(String source, String dest){

    }
}
