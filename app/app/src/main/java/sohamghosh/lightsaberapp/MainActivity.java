package sohamghosh.lightsaberapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button btn;
    private ImageView lightsaberBlade;

    private String channelId = "iOpC09Pf7e0Z";
    private String baseUrl = "http://192.168.0.146:5000/";

    private Socket socket;
    private JSONObject data;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    final static int QRCODE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) this.findViewById(R.id.scanCodeButton);
        lightsaberBlade = (ImageView) this.findViewById(R.id.lightsaberBlade);
        lightsaberBlade.setVisibility(View.GONE);
        this.setupSensor();
        this.startLightSaber();
    }

    private void startLightSaber() {
        this.btn.setText("START");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do QR code scanning here
                Intent qrIntent = new Intent(MainActivity.this, QRCodeActivity.class);
                startActivityForResult(qrIntent, QRCODE_REQUEST);
            }
        });
    }

    private void stopLightSaber() {
        this.btn.setText("STOP");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do QR code scanning here
                MainActivity.this.stopDoingSensorStuff();
                MainActivity.this.socket.disconnect();
                MainActivity.this.animateLightSaber(false);
                MainActivity.this.startLightSaber(); // set button to start mode
            }
        });
    }

    private void animateLightSaber(boolean in){
        Animation a;
        if (in){
            a = new AlphaAnimation(0.00f, 1.00f);
            a.setDuration(1000);
            a.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationStart(Animation animation) {
                    MainActivity.this.lightsaberBlade.setVisibility(View.VISIBLE);

                }

                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }

                public void onAnimationEnd(Animation animation) {
                }
            });
        } else {
            a = new AlphaAnimation(1.00f, 0.00f);
            a.setDuration(1000);
            a.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    MainActivity.this.lightsaberBlade.setVisibility(View.GONE);
                }
            });
        }
        this.lightsaberBlade.startAnimation(a);
    }

    private void setupSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null){
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        } else {
            Log.e("SENSOR", "No sensor available, go suck thumb");
        }
    }

    private void setupWebsocket() {
        try {
            String url = baseUrl + channelId;
            Log.d("SOCKET", "connecting to " + url);
            socket = IO.socket(url);
            socket.connect();
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    socket.emit("foo", "hi");
                    Log.d("SOCKET", "connected");
                    MainActivity.this.startDoingSensorStuff();
                }

            }).on("event", new Emitter.Listener() {

                @Override
                public void call(Object... args) {}

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {}

            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void startDoingSensorStuff(){
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void stopDoingSensorStuff(){
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d("SENSOR", "sensor called");
        float[] values = sensorEvent.values;
        data = new JSONObject();
        try {
            data.put("x", values[0]);
            data.put("y", values[1]);
            data.put("z", values[2]);
            socket.emit("rot-data", data);
        } catch (JSONException e) {
            Log.e("SENSOR", "Problem serialising JSON");
        }
//
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("QRCODE main", data.getStringExtra("result"));
        if (requestCode == QRCODE_REQUEST && resultCode == RESULT_OK){
            MainActivity.this.channelId = data.getStringExtra("result");
            Log.d("QRCODE main", "Got result: " + this.channelId);
            MainActivity.this.setupWebsocket();
            MainActivity.this.animateLightSaber(true);
            MainActivity.this.stopLightSaber(); // set button to stop mode
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
