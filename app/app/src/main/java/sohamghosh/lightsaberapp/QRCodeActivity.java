package sohamghosh.lightsaberapp;



import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QRCodeActivity extends AppCompatActivity {

    private BarcodeDetector detector;
    private CameraSource mCameraSource;
    private SurfaceView mSurfaceView;
//    private CameraPreview mPreview;
    private Camera camera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        this.detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        this.mSurfaceView = new SurfaceView(getApplicationContext());

        if(!detector.isOperational()) {
            Log.e("QRCODE", "Could not start barcode detector");
        }

        this.mCameraSource = new CameraSource.Builder(getApplicationContext(), detector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(15.0f)
                .build();

        try {
            this.startCamera();
        } catch (IOException e) {
            Log.e("QRCODE", "Could not start camera");
        }
    }

    private void startCamera() throws IOException {
//        mPreview = new CameraPreview(this, this.camera);
        this.mCameraSource.start(mSurfaceView.getHolder());
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
//        preview.addView(mPreview);
    }

}
