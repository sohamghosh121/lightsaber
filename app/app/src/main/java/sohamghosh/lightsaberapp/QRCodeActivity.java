package sohamghosh.lightsaberapp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QRCodeActivity extends AppCompatActivity {

    private BarcodeDetector mDetector;
    private CameraSource mCameraSource;

    private Intent intent;

    private CameraManager mCameraManager;
    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        intent = this.getIntent();

        mSurfaceView = (SurfaceView) this.findViewById(R.id.cameraPreview);

        mDetector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        if (mDetector.isOperational())
            Log.d("QRCODE", "Started detector");

        // permissions check
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.d("QRCODE", "Has camera permissions");
        } else {
            this.requestPermissions(new String[]{ Manifest.permission.CAMERA}, 0);
        }

        mCameraManager = (CameraManager) getSystemService(getApplicationContext().CAMERA_SERVICE);
        try {
            String cameraId = mCameraManager.getCameraIdList()[0];
            mCameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice cameraDevice) {
                    Log.d("QRCODE", "Camera opened");
                    List<Surface> surfaceList = Collections.singletonList(mSurfaceView.getHolder().getSurface());
                    try {
                        cameraDevice.createCaptureSession(surfaceList, new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                Log.i("QRCODE", "capture session configured: " + session);
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) {
                                Log.e("QRCODE", "capture session configure failed: " + session);
                            }
                        }, new Handler());
                    } catch (CameraAccessException e) {
                        Log.e("QRCODE", "Could not access camera");
                    }
//                    cameraDevice.createCaptureSession(new ArrayList<Surface>(mSurface));
                }

                @Override
                public void onDisconnected(CameraDevice cameraDevice) {
                    Log.d("QRCODE", "Camera closed");
                }

                @Override
                public void onError(CameraDevice cameraDevice, int i) {
                    Log.e("QRCODE", "Camera error");
                }
            }, new Handler());
        } catch (SecurityException ex) {
            Log.e("QRCODE", "Security exception");
        } catch (CameraAccessException e) {
            Log.e("QRCODE", "Could not access camera");
        }

    }

    @Override
    public void onRequestPermissionsResult(int reqCode, String[] permissions, int[] grantResults) {
        if (reqCode == 0){
            Log.d("QRCODE", "Permissions");
        }
    }

    private void detect(){
        Bitmap bmp = Bitmap.createBitmap(null);
        Frame frame = new Frame.Builder().setBitmap(bmp).build();
        SparseArray<Barcode> barcodes = mDetector.detect(frame);

        if (barcodes.size() > 0) {
            Barcode thisCode = barcodes.valueAt(0);
            String result =  thisCode.rawValue;

            intent.putExtra("result", result);
            this.setResult(RESULT_OK, intent);
            finish();
        }
    }

}
