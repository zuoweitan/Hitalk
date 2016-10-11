package com.vivifram.second.hitalk.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ReceiverCallNotAllowedException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zuowei on 16-8-24.
 */
public class ConnectivityNotifier extends BroadcastReceiver{

    private Set<ConnectivityListener> listeners = new HashSet<>();
    private boolean hasRegisteredReceiver = false;
    private final Object lock = new Object();

    public interface ConnectivityListener {
        void networkConnectivityStatusChanged(boolean isConnected);
    }

    private static final ConnectivityNotifier singleton = new ConnectivityNotifier();

    public static ConnectivityNotifier getNotifier(Context context) {
        boolean result = singleton.tryToRegisterForNetworkStatusNotifications(context);
        return singleton;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }

    private void tryNotifyAll(Context context,Intent intent) {
        List<ConnectivityListener> listenersCopy;
        synchronized (lock) {
            listenersCopy = new ArrayList<>(listeners);
        }
        boolean connectionLost = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        boolean isConnected = !connectionLost ? isConnected(context.getApplicationContext()) : false;
        for (ConnectivityListener delegate : listenersCopy) {
            delegate.networkConnectivityStatusChanged(isConnected);
        }
    }

    public void addListener(ConnectivityListener delegate) {
        synchronized (lock) {
            listeners.add(delegate);
        }
    }

    public void removeListener(ConnectivityListener delegate) {
        synchronized (lock) {
            listeners.remove(delegate);
        }
    }

    public void addAndRequestOneShot(Context context,ConnectivityListener delegate){
        addListener(delegate);
        delegate.networkConnectivityStatusChanged(isConnected(context.getApplicationContext()));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        tryNotifyAll(context,intent);
    }

    private boolean tryToRegisterForNetworkStatusNotifications(Context context) {
        synchronized (lock) {
            if (hasRegisteredReceiver) {
                return true;
            }

            try {
                if (context == null) {
                    return false;
                }
                context = context.getApplicationContext();
                context.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                hasRegisteredReceiver = true;
                return true;
            } catch (ReceiverCallNotAllowedException e) {
                return false;
            }
        }
    }
}
