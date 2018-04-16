package fyp.najeeb.contentawaresystem;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TCPClient mTcpClient = null;
    public static TextView notis = null;
    public static TextView serverName = null;
    public static TextView savedNotis = null;
    Button getSavedNotis = null;
    Button clearCache = null;
    Button clear = null;
    Intent service = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        clearCache = (Button) findViewById(R.id.btnClearCache);
        service = new Intent(getBaseContext(), tcpService.class);
        startService(service);
        serverName = findViewById(R.id.serverName);
        notis = (TextView) findViewById(R.id.tvNoti);
        clear = (Button) findViewById(R.id.btnClear);
        savedNotis = (TextView) findViewById(R.id.savedNotis);
        getSavedNotis = (Button) findViewById(R.id.btnSaved);
                getSavedNotis.setOnClickListener(this);
                clearCache.setOnClickListener(this);
                clear.setOnClickListener(this);



    }

    public String getMacId() {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getBSSID();
    }




    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnClear){
            notis.setText("");
            savedNotis.setText("");

        }

        if(view.getId() == R.id.btnSaved)
        {
            showSavedNotifications();
        }

        if(view.getId() == R.id.btnClearCache)
        {
            tcpService.savedNotifications.edit().clear().apply();
        }
    }

    private void showSavedNotifications() {
        int counter = tcpService.savedNotifications.getInt("counter", 0);

        while(counter > 0)
        {
            MainActivity.savedNotis.setText(MainActivity.savedNotis.getText() + tcpService.savedNotifications.getString(counter + "", "") + " \n\n");
            counter--;
        }
    }



}


