package com.agent.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;

import android.view.View;
import android.widget.*;

import com.agent.model.MIBtree;
import com.agent.service.AgentService;
import com.util.TimeandJWD;
import com.won.usb_ch340.R;

import java.util.ArrayList;

public class AgentActivity extends Activity implements View.OnClickListener {
    /** Messenger for communicating with service. */
    Messenger mService = null;
    /** Flag indicating whether we have called bind on the service. */
    boolean mIsBound;


    private LinearLayout messagesReceivedScrollView;
    private ListView registeredManagersList;
    private ArrayAdapter<String> messagesReceivedAdapter;
    private Button dangerButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        messagesReceivedScrollView = (LinearLayout) findViewById(R.id.snmp_messages_history);

        messagesReceivedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        registeredManagersList = (ListView) findViewById(R.id.list_of_registered_managers);
        registeredManagersList.setAdapter(messagesReceivedAdapter);
        dangerButton = (Button) findViewById(R.id.danger_alert_button);
        dangerButton.setOnClickListener(this);


        Intent intent = new Intent(this, AgentService.class);
        startService(intent);
        doBindAgentService();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.danger_alert_button:
                handleSendDangerAlert();
                break;
        }
    }
    //// TODO: 16/8/12  这个代码是发送给服务器  自己创建了OID 来实现的。
    private void handleSendDangerAlert() {
        Message msg = Message.obtain(null,
                AgentService.MSN_SEND_DANGER_TRAP);
        msg.replyTo = mMessenger;
        sendMessageToAgentService(msg);
    }

    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AgentService.MSG_SET_VALUE:
                    break;

                case AgentService.MSG_SNMP_REQUEST_RECEIVED:
                    TextView aux = new TextView(AgentActivity.this);
                    aux.setText(AgentService.lastRequestReceived);
                    messagesReceivedScrollView.addView(aux);

                    break;

                case AgentService.MSG_MANAGER_MESSAGE_RECEIVED:
                    MIBtree miBtree = MIBtree.getInstance();
                    String message = miBtree.getNext(MIBtree.MNG_MANAGER_MESSAGE_OID).getVariable().toString();
                    messagesReceivedAdapter.add(message);
                    messagesReceivedAdapter.notifyDataSetChanged();
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void sendMessageToAgentService(Message msg){
        try {
            TimeandJWD tj=new TimeandJWD(AgentActivity.this);
            msg.obj=tj;
            mService.send(msg);
        } catch (RemoteException e) {

        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            mService = new Messenger(service);

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null,
                        AgentService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                // Give it some value as an example.
                msg = Message.obtain(null,
                        AgentService.MSG_SET_VALUE, this.hashCode(), 0);
                mService.send(msg);
            } catch (RemoteException e) {

            }

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
        }
    };

    void doBindAgentService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(new Intent(this, AgentService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindAgentService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            AgentService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {

                }
            }

            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        doUnbindAgentService();
        super.onDestroy();
    }
}
