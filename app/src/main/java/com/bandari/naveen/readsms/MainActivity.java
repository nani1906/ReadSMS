package com.bandari.naveen.readsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bandari.naveen.readsms.listeners.SmsListener;
import com.bandari.naveen.readsms.network.ConnectivityReceiver;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText otpLabel;
    SmsVerifyCatcher smsVerifyCatcher;
    private BroadcastReceiver mReceiver;
    private static SmsListener mListener;
    Boolean b;
    String abcd,xyz;
    String otp_generated,contactNo,id1;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bandari.naveen.readsms.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        otpLabel = findViewById(R.id.otpLabel);
        tv=(TextView) findViewById(R.id.verify_otp);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // TO complete auto-detect SMS received to mobile. using third party library

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                Log.e("On SMS Catch","============================");
                String code = parseCode(message);//Parse verification code
                otpLabel.setText(code);//set code in edit text
                Log.e("CODEEEEEE","============================"+code);
                Toast.makeText(getApplicationContext(), "Message code:"+code, Toast.LENGTH_LONG);
                //then you can send verification code to server
            }
        });




        // TO complete auto-detect SMS received to mobile. without using library....
        // Uncomment following block for to avoid third party library....

/*        ConnectivityReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                otpLabel.setText(messageText);
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    InputMethodManager imm=
                            (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                }
                catch(Exception e)
                {}
                if (otpLabel.getText().toString().equals(otp_generated))
                {
                    Toast.makeText(MainActivity.this, "OTP Verified Successfully !", Toast.LENGTH_SHORT).show();
                }
            }
        });

*/

    }







    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle data  = intent.getExtras();
                Object[] pdus = (Object[]) data.get("pdus");
                for(int i=0;i<pdus.length;i++){
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    String sender = smsMessage.getDisplayOriginatingAddress();
                    // b=sender.endsWith("WNRCRP");  //Just to fetch otp sent from WNRCRP
                    String messageBody = smsMessage.getMessageBody();
                    abcd=messageBody.replaceAll("[^0-9]","");   // here abcd contains otp which is in number format
                    //Pass on the text to our listener.
                    if(b==true) {
                        mListener.messageReceived(abcd);  // attach value to interface object
                    }
                    else
                    {
                    }
                }
            }
            public void bindListener(SmsListener listener) {
                mListener = listener;
            }
        };
    }

        @Override
    protected void onStart() {
        super.onStart();
        Log.e("ON START","============================");
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("ON STOP","============================");
//        smsVerifyCatcher.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("ON STOP","============================");
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
