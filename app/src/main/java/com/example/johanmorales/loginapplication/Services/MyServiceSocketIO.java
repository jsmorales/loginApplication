package com.example.johanmorales.loginapplication.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.johanmorales.loginapplication.Models.Resultado;
import com.example.johanmorales.loginapplication.R;
import com.example.johanmorales.loginapplication.ServicesActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MyServiceSocketIO extends Service {

    public static final String TAG = MyServiceSocketIO.class.getSimpleName();
    private static final int ID_NOT_MESSAGE = 234560;
    public Resultado resultado;
    private Socket socket;
    private final IBinder mBinder = new MyBinder();

    public MyServiceSocketIO() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "El servicio socket ha comenzado!!");

        String urlSocket = intent.getExtras().getString("url");

        resultado = (Resultado) intent.getExtras().get("resultado");

        Log.d(TAG, "El servicio socket pretende conectar a: "+urlSocket);

        getConnectionSocket(urlSocket);

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        Log.d(TAG, "El servicio ejecuta onBind!");

        //this.onStartCommand()

        return mBinder;
    }

    public void getConnectionSocket(final String url){

        /*validate socket connection*/
        checkSocketStatus();

        try {

            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.transports = new String[] {"websocket"};

            socket = IO.socket(url, opts);

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    Log.d(TAG, "Socket conectado a: "+url);

                    //logerText("Socket conectado a: "+url);


                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    Log.d(TAG, "Socket desconectado de: "+url);

                    //logerText("Socket desconectado a: "+url);

                }
            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    Log.d(TAG, "Error conectando a socket. ");

                    //logerText("Error conectando a socket. ");

                }
            }).on("service_created", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "Servicio creado.");

                    //logerText("[Servicio Creado]");

                    notifyMessage("Creado","[Servicio Creado]");

                    //refreshDataOnThread();

                }
            }).on("service_updated", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //Log.d(TAG, "Servicio actualizado.");

                    JSONObject obj = (JSONObject)args[0];

                    try {

                        Log.d(TAG, "[Servicio Actualizado] - "+obj.getString("paxName"));

                        //logerText("[Servicio Actualizado] - "+obj.getString("paxName"));

                        notifyMessage("Actualizado","[Servicio Actualizado] - "+obj.getString("paxName"));

                        //refreshDataOnThread();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).on("service_assignation_updated", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    //Log.d(TAG, "Servicio asignado.");

                    JSONObject obj = (JSONObject)args[0];

                    try {
                        //logerText("[Servicio Asignado] - "+obj.getString("serviceId"));

                        notifyMessage("Asignado","[Servicio Actualizado] - "+obj.getString("serviceId"));

                        Log.d(TAG, "[Servicio Asignado] - "+obj.getString("serviceId"));

                        //refreshDataOnThread();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).on("service_deleted", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    Log.d(TAG, "Servicio eliminado.");

                    JSONObject obj = (JSONObject)args[0];

                    try {

                        notifyMessage("Asignado","[Servicio Actualizado] - "+obj.getString("serviceId"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void checkSocketStatus(){

        if(socket == null){
            Log.d(TAG, "Socket no inicializado!");
        }else{
            Log.d(TAG, "Socket ya ha iniciado!");
            socket.disconnect();
        }
    }

    public void notifyMessage(String title, String message){

        Random rand = new Random();

        Intent notifyIntent = new Intent(this, ServicesActivity.class);
        // Set the Activity to start in a new, empty task
        //https://stackoverflow.com/questions/12408719/resume-activity-in-android
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //put the extras (android lg k10 2017)
        notifyIntent.putExtra("resultado", resultado);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //stackBuilder.addNextIntentWithParentStack(notifyIntent);

        // Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        //startActivityIfNeeded(notifyIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "logginAppChannel")
                .setSmallIcon(R.drawable.ic_menu_send)
                .setContentTitle("SAI-Monitor - " + title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(new long[]{NotificationCompat.DEFAULT_VIBRATE})
                .setShowWhen(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true);

        //
        mBuilder.setContentIntent(notifyPendingIntent);


        //for android 8.0 o higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "logginAppChannel";
            String description = "loginapp";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("logginAppChannel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManagerCompat.notify(rand.nextInt(ID_NOT_MESSAGE) + 1, mBuilder.build());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        socket.disconnect();
    }

    @Override
    public boolean stopService(Intent name) {

        Log.d(TAG, "parando el servicio sockect!");

        socket.disconnect();

        return super.stopService(name);
    }

    public class MyBinder extends Binder {

        MyServiceSocketIO getService() {
            return MyServiceSocketIO.this;
        }
    }
}
