package fyp.najeeb.contentawaresystem;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class tcpService extends Service {

    static TCPClient con = null;
    static Context c = null;
    String info;
    public static SharedPreferences savedNotifications;
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private static void sendMessageToActivity(String msg, String target, String type) {
        if(target.equals("main")) {
            Intent intent = new Intent("main");
            intent.putExtra(type, msg);
            LocalBroadcastManager.getInstance(c).sendBroadcast(intent);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Bundle extras = intent.getExtras();
            //Toast.makeText(this, "after extras " + extras.getString("data"), Toast.LENGTH_SHORT).show();

            if (extras != null) {
                info = extras.getString("data");
                con.sendMessage(extras.getString("data"));
                //Toast.makeText(this, "got it", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){}

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        savedNotifications = getApplicationContext().getSharedPreferences("notifications", 0);

        c = this;
        new ConnectTask().execute("");
        new Thread()
        {
            public void run()
            {
                while(true)
                {
                    try {
                        con.sendMessage("req,"+getMacId()+",");
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }.start();
        //Toast.makeText(c, "Hello", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

    public String getMacId() {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getBSSID();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public class ConnectTask extends AsyncTask<String, String, TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object
            con = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });

            con.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //Toast.makeText(c, values[0], Toast.LENGTH_SHORT).show();

            try {
                String[] info = values[0].split(",");
                int i = 0;
                if(info[0].equals("noti"))
                {

                    MainActivity.serverName.setText(info[1]);
                    i++;

                    for (String s: info) {
                        if(i>=3) {
                            String notification = s;
                            MainActivity.notis.setText(MainActivity.notis.getText() + "\n\n" + notification);
                            if(!isSaved(s))
                            saveNotification(notification);
                        }
                        i++;
                    }
                }

            }
            catch(Exception x)
            {

            }
        }

        private boolean isSaved(String n) {

            Map<String, ?> allNotis = savedNotifications.getAll();

            for (Map.Entry<String, ?> entry : allNotis.entrySet()) {
                if(entry.getValue().equals(n))
                    return true;
            }
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        private void saveNotification(String notification) {
            SharedPreferences.Editor editor = savedNotifications.edit();
            editor.putInt("counter", savedNotifications.getInt("counter", 0) + 1);

            editor.putString(savedNotifications.getInt("counter", 0) + "", notification);

            editor.apply();
            showNo();

        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void showNo()
        {
            final NotificationManager mgr=
                    (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification note=new Notification(R.drawable.ic_launcher_background,
                    "New notification arrived",
                    System.currentTimeMillis());
            mgr.notify(11, note);


        }


    }





}