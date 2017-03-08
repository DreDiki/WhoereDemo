package com.drediki.wwclientdemo;

import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private EditText editText;
    private EditText editText2;
    private Client client = new Client();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    client.disconnect();
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    client.connect();
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.button1:
                User user = new User();
                user.setName("DreDiki"+Math.random());
                user.setPassword("2333");
                user.setGuest(true);
                user.setCurrentPosition(new Position(70,120));
                client.login(user);
                break;
            case R.id.button2:
                Position position = new Position(Double.parseDouble(editText.getText().toString()),Double.parseDouble(editText2.getText().toString()));
                client.update(position);
                break;
        }
    }
}
