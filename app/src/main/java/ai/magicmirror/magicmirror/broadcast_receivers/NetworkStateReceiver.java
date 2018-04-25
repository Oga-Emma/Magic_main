package ai.magicmirror.magicmirror.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ai.magicmirror.magicmirror.utils.NetworkUtils;

public class NetworkStateReceiver extends BroadcastReceiver {

    private NetworkStateReceiverListener listener;

    public NetworkStateReceiver(NetworkStateReceiverListener listener) {
        this.listener = listener;
    }

    public void onReceive(Context context, Intent intent) {
        if(NetworkUtils.isNetworkAvailable(context))
            listener.networkAvailable();
        else
            listener.networkUnavailable();

    }

    public interface NetworkStateReceiverListener {
        public void networkAvailable();
        public void networkUnavailable();
    }
}