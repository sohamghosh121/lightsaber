package sohamghosh.lightsaberapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button btn;

    private String channelId = "iOpC09Pf7e0Z";
    private String baseUrl = "http://192.168.0.146:5000/";

    private Socket socket;
    private boolean readyToSend;
    private JSONObject data;

    private SensorManager mSensorManager;
    private Sensor mSensor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) this.findViewById(R.id.scanCodeButton);

        this.setupSensor();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do QR code scanning here
                MainActivity.this.setupWebsocket();
            }
        });


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


    private void sendWebsocketData(){

    }

    private void startDoingSensorStuff(){
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void stopDoingSensorStuff(){
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        JSONObject data = new JSONObject();
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
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
