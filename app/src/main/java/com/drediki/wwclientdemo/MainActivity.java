package com.drediki.wwclientdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.drediki.wwclient.Client;
import com.drediki.wwclient.Position;
import com.drediki.wwclient.User;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Client.FeedbackListener,Client.ReceiveListener{

    private TextView mTextMessage;
    private EditText editText;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private EditText editText5;
    private Client client = new Client();
    private Handler handler;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText("Message Cleared");
                    return true;
                case R.id.navigation_dashboard:
                    client.disconnect();
                    mTextMessage.setText(mTextMessage.getText()+"\nTrying to disconnect");
                    return true;
                case R.id.navigation_notifications:
                    client.connect();
                    mTextMessage.setText(mTextMessage.getText()+"\nTrying to connect");
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        client.setFeedbackListener(this);
        client.setReceiveListener(this);
        mTextMessage = (TextView) findViewById(R.id.message);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        editText5 = (EditText) findViewById(R.id.editText5);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.button1:
//                User user = new User(editText3.getText().toString());
                User user = new User(editText3.getText().toString());
                user.setPassword(editText4.getText().toString());
                user.setGuest(true);
                user.setCurrentPosition(new Position(70,120));
                client.login(user);
                mTextMessage.setText(mTextMessage.getText()+"\nTrying to login");
                break;
            case R.id.button2:
                Position position = new Position(Double.parseDouble(editText.getText().toString()),Double.parseDouble(editText2.getText().toString()));
                client.update(position);
                mTextMessage.setText(mTextMessage.getText()+"\nTrying to update position");
                break;
            case R.id.button3:
                mTextMessage.setText(mTextMessage.getText()+"\nTrying to send :"+editText5.getText());
                client.broadcastToNearBy(editText5.getText().toString());
                break;
            case R.id.button4:
                client.logout();
                break;
            case R.id.button5:
                mTextMessage.setText(mTextMessage.getText()+"\nTrying to send :"+editText5.getText());
                client.broadcastToAll(editText5.getText().toString());
                break;
            case R.id.button6:
                mTextMessage.setText(mTextMessage.getText()+"\nTrying to get nearby");
                client.getNearbyUsers();
                break;
            case R.id.button7:
                mTextMessage.setText(mTextMessage.getText()+"\nTrying to get nearby");
                client.getAllUsers();
                break;
        }
    }


    @Override
    public void onSuccess(final Client.ACTION action,final int resultCode) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mTextMessage.setText(mTextMessage.getText()+"\n"+action + "Success,code:" + resultCode);
            }
        });
    }

    @Override
    public void onFailed(final Client.ACTION action,final int resultCode) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mTextMessage.setText(mTextMessage.getText()+"\n"+action + "Failed,code:" + resultCode);
            }
        });
    }

    @Override
    public void broadcast(final User user, final String s,final String s1) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mTextMessage.setText(mTextMessage.getText()+"\n"+user.getName() + " Send message in"+s+": " + s1);
            }
        });
    }

    @Override
    public void chat(final User user, final String s) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mTextMessage.setText(mTextMessage.getText()+"\n"+user.getName() + " Send message privately:" + s);
            }
        });
    }

    @Override
    public void nearby(final User user,final int i) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mTextMessage.setText(mTextMessage.getText()+"\n"+user.getName() + " actioned:" + i);
            }
        });
    }

    @Override
    public void users(ArrayList<User> arrayList, String s) {
        if(arrayList==null||arrayList.isEmpty())return;
        for(User user:arrayList){
            if(user==null)continue;
            System.out.println("Get Information Of:" + user.getName());
        }
    }
}
