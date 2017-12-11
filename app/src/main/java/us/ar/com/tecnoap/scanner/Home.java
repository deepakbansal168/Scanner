package us.ar.com.tecnoap.scanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.devspark.appmsg.AppMsg;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.manateeworks.BarcodeScanner;
import com.manateeworks.CameraManager;
import com.manateeworks.MWOverlay;
import com.manateeworks.MWParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class Home extends AppCompatActivity implements SurfaceHolder.Callback, ActivityCompat.OnRequestPermissionsResultCallback{


    CardView scanproduct;
    Button btnprice;
    TextView txtpname,txtprice,pricetext;
    RelativeLayout save,cancel,sync;
    DBHelper dbHelper;
    EditText input_price;
    LinearLayout updatepricelay;
    public static int MY_PERMISSIONS_REQUEST_CAMERA=1000;
    public static int MY_PERMISSIONS_REQUEST_STORAGE=2000;
    String code;
    ProgressDialog pd;
//    private CompoundBarcodeView barcodeView;
    public final static int Codezeo   = 50; // Keyboard.KEYCODE_DELETE
    public final static int Codeone   = 51; // Keyboard.KEYCODE_DELETE
    public final static int Codetwo   = 52; // Keyboard.KEYCODE_CANCEL
    public final static int Codethree     = 53;
    public final static int Codefour  = 54;
    public final static int Codefive     = 55;
    public final static int Codesix    = 56;
    public final static int Codeseven = 57;
    public final static int Codeeight     = 58;
    public final static int Codenine     = 59;
    public final static int Codedotzero     = 500;
    public final static int CodeClear    = -55;
    public final static int Codeleft     = 55002;
    public final static int Coderight     = 55001;
    public final static int Codedot    = 55000;
    public final static int Codeent    = 55003;
    KeyboardView mKeyboardView;
    ScrollView scrollView1;

    public static final boolean USE_MWANALYTICS = false;
    public static final boolean PDF_OPTIMIZED = false;

    /* Parser */
    /*
     * MWPARSER_MASK - Set the desired parser type Available options:
	 * MWParser.MWP_PARSER_MASK_ISBT MWParser.MWP_PARSER_MASK_AAMVA
	 * MWParser.MWP_PARSER_MASK_IUID MWParser.MWP_PARSER_MASK_HIBC
	 * MWParser.MWP_PARSER_MASK_SCM MWParser.MWP_PARSER_MASK_NONE
	 */
    public static final int MWPARSER_MASK = MWParser.MWP_PARSER_MASK_NONE;

    public static final int USE_RESULT_TYPE = BarcodeScanner.MWB_RESULT_TYPE_MW;

    public static final OverlayMode OVERLAY_MODE = OverlayMode.OM_MWOVERLAY;

    // !!! Rects are in format: x, y, width, height !!!
    public static final Rect RECT_LANDSCAPE_1D = new Rect(3, 20, 94, 60);
    public static final Rect RECT_LANDSCAPE_2D = new Rect(20, 5, 60, 90);
    public static final Rect RECT_PORTRAIT_1D = new Rect(20, 3, 60, 94);
    public static final Rect RECT_PORTRAIT_2D = new Rect(20, 5, 60, 90);
    public static final Rect RECT_FULL_1D = new Rect(3, 3, 94, 94);
    public static final Rect RECT_FULL_2D = new Rect(20, 5, 60, 90);
    public static final Rect RECT_DOTCODE = new Rect(30, 20, 40, 60);

    private static final String MSG_CAMERA_FRAMEWORK_BUG = "Sorry, the Android camera encountered a problem: ";

    public static final int ID_AUTO_FOCUS = 0x01;
    public static final int ID_DECODE = 0x02;
    public static final int ID_RESTART_PREVIEW = 0x04;
    public static final int ID_DECODE_SUCCEED = 0x08;
    public static final int ID_DECODE_FAILED = 0x10;

    private Handler decodeHandler;
    private boolean hasSurface;
    private String package_name;

    private int activeThreads = 0;
    public static int MAX_THREADS = Runtime.getRuntime().availableProcessors();

    private ImageButton zoomButton;
    private ImageButton buttonFlash;
    private ImageView imageOverlay;

    boolean flashOn = false;

    private int zoomLevel = 0;
    private int firstZoom = 150;
    private int secondZoom = 300;


    private SurfaceHolder surfaceHolder;
    private boolean surfaceChanged = false;

	/* Analytics */
    /*
     * private String encResult; private String tName; private String
	 * analyticsTag = "TestTag";
	 */

    private enum State {
        STOPPED, PREVIEW, DECODING
    }

    private enum OverlayMode {
        OM_IMAGE, OM_MWOVERLAY, OM_NONE
    }

    State state = State.STOPPED;

    public Handler getHandler() {
        return decodeHandler;
    }



    void init(){
        save=(RelativeLayout)findViewById(R.id.save);
        cancel=(RelativeLayout)findViewById(R.id.cancel);
        btnprice=(Button)findViewById(R.id.btnprice);
        scanproduct=(CardView)findViewById(R.id.cardview);
        sync=(RelativeLayout)findViewById(R.id.sync);
        input_price=(EditText)findViewById(R.id.input_price);
        pricetext=(TextView)findViewById(R.id.txtprice);
        scrollView1=(ScrollView)findViewById(R.id.ScrollView01);
//        barcodeView = (CompoundBarcodeView)findViewById(R.id.barcode_scanner);
//        barcodeView.decodeContinuous(callback);
//        barcodeView.setTorchListener(new DecoratedBarcodeView.TorchListener() {
//            @Override
//            public void onTorchOn() {
//
//            }
//
//            @Override
//            public void onTorchOff() {
//
//            }
//        });



        txtpname=(TextView)findViewById(R.id.pname);
        txtprice=(TextView)findViewById(R.id.price);
        updatepricelay=(LinearLayout)findViewById(R.id.savelay);
        scanproduct.setOnClickListener(clickListener);
        btnprice.setOnClickListener(clickListener);
        save.setOnClickListener(clickListener);
        cancel.setOnClickListener(clickListener);
        sync.setOnClickListener(clickListener);
        input_price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if( hasFocus ) showCustomKeyboard(v); else hideCustomKeyboard();
            }
        });
        input_price.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });
        input_price.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                edittext.setSelection(edittext.getText().length());
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
        input_price.setInputType( input_price.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS );

        dbHelper=new DBHelper(Home.this);
        checkpremission("storage");

        // Create the Keyboard
        Keyboard mKeyboard= new Keyboard(Home.this,R.xml.hexkbd);

        // Lookup the KeyboardView
         mKeyboardView= (KeyboardView)findViewById(R.id.keyboardview);
        // Attach the keyboard to the view
        mKeyboardView.setKeyboard( mKeyboard );
        // Do not show the preview balloons
        mKeyboardView.setPreviewEnabled(false);
        // Install the key handler
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        // Hide the standard keyboard initially
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);




        surfaceChanged = false;
        package_name = getPackageName();

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        imageOverlay = (ImageView) findViewById(R.id.imageOverlay);
        // register your copy of library with given key
        int registerResult = BarcodeScanner.MWBregisterSDK("SDK_Key", this);

        switch (registerResult) {
            case BarcodeScanner.MWB_RTREG_OK:
                Log.i("MWBregisterSDK", "Registration OK");
                break;
            case BarcodeScanner.MWB_RTREG_INVALID_KEY:
                Log.e("MWBregisterSDK", "Registration Invalid Key");
                break;
            case BarcodeScanner.MWB_RTREG_INVALID_CHECKSUM:
                Log.e("MWBregisterSDK", "Registration Invalid Checksum");
                break;
            case BarcodeScanner.MWB_RTREG_INVALID_APPLICATION:
                Log.e("MWBregisterSDK", "Registration Invalid Application");
                break;
            case BarcodeScanner.MWB_RTREG_INVALID_SDK_VERSION:
                Log.e("MWBregisterSDK", "Registration Invalid SDK Version");
                break;
            case BarcodeScanner.MWB_RTREG_INVALID_KEY_VERSION:
                Log.e("MWBregisterSDK", "Registration Invalid Key Version");
                break;
            case BarcodeScanner.MWB_RTREG_INVALID_PLATFORM:
                Log.e("MWBregisterSDK", "Registration Invalid Platform");
                break;
            case BarcodeScanner.MWB_RTREG_KEY_EXPIRED:
                Log.e("MWBregisterSDK", "Registration Key Expired");
                break;

            default:
                Log.e("MWBregisterSDK", "Registration Unknown Error");
                break;
        }

        // choose code type or types you want to search for
        if (PDF_OPTIMIZED) {
            BarcodeScanner.MWBsetDirection(BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL);
            BarcodeScanner.MWBsetActiveCodes(BarcodeScanner.MWB_CODE_MASK_PDF);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF, RECT_LANDSCAPE_1D);
        } else {
            // Our sample app is configured by default to search both directions...
            BarcodeScanner.MWBsetDirection(
                    BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL | BarcodeScanner.MWB_SCANDIRECTION_VERTICAL);

            // Our sample app is configured by default to search all supported barcodes...
            // But for better performance, only activate the symbologies your application requires...
            BarcodeScanner.MWBsetActiveCodes(
                    BarcodeScanner.MWB_CODE_MASK_25 | BarcodeScanner.MWB_CODE_MASK_39 | BarcodeScanner.MWB_CODE_MASK_93
                            | BarcodeScanner.MWB_CODE_MASK_128 | BarcodeScanner.MWB_CODE_MASK_AZTEC
                            | BarcodeScanner.MWB_CODE_MASK_DM | BarcodeScanner.MWB_CODE_MASK_EANUPC
                            | BarcodeScanner.MWB_CODE_MASK_PDF | BarcodeScanner.MWB_CODE_MASK_QR
                            | BarcodeScanner.MWB_CODE_MASK_CODABAR | BarcodeScanner.MWB_CODE_MASK_11
                            | BarcodeScanner.MWB_CODE_MASK_MAXICODE | BarcodeScanner.MWB_CODE_MASK_POSTAL
                            | BarcodeScanner.MWB_CODE_MASK_MSI | BarcodeScanner.MWB_CODE_MASK_RSS);

            // set the scanning rectangle based on scan direction(format in pct:
            // x, y, width, height)
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_25, RECT_FULL_1D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_39, RECT_FULL_1D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_93, RECT_FULL_1D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_128, RECT_FULL_1D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_AZTEC, RECT_FULL_2D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DM, RECT_FULL_2D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_EANUPC, RECT_FULL_1D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF, RECT_FULL_1D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_QR, RECT_FULL_2D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_RSS, RECT_FULL_1D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_CODABAR, RECT_FULL_1D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DOTCODE, RECT_DOTCODE);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_11, RECT_FULL_1D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_MSI, RECT_FULL_1D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_MAXICODE, RECT_FULL_1D);
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_POSTAL, RECT_FULL_1D);

        }

        if (OVERLAY_MODE == OverlayMode.OM_IMAGE) {
            imageOverlay.setVisibility(View.VISIBLE);
        }
        BarcodeScanner.MWBsetLevel(2);
        BarcodeScanner.MWBsetResultType(USE_RESULT_TYPE);

        // Set minimum result length for low-protected barcode types
        BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_25, 5);
        BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_MSI, 5);
        BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_39, 5);
        BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_CODABAR, 5);
        BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_11, 5);

        CameraManager.init(getApplication());

        hasSurface = false;
        state = State.STOPPED;
        decodeHandler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case ID_DECODE:
                        decode((byte[]) msg.obj, msg.arg1, msg.arg2);
                        break;

                    case ID_AUTO_FOCUS:
                        if (state == State.PREVIEW || state == State.DECODING) {
                            CameraManager.get().requestAutoFocus(decodeHandler, ID_AUTO_FOCUS);
                        }
                        break;
                    case ID_RESTART_PREVIEW:
                        restartPreviewAndDecode();
                        break;
                    case ID_DECODE_SUCCEED:
                        state = State.STOPPED;
                        handleDecode((BarcodeScanner.MWResult) msg.obj);
                        break;
                    case ID_DECODE_FAILED:
                        break;
                }
                return false;
            }
        });

        zoomButton = (ImageButton) findViewById(R.id.zoomButton);
        zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                zoomLevel++;
                if (zoomLevel > 2) {
                    zoomLevel = 0;
                }

                switch (zoomLevel) {
                    case 0:
                        CameraManager.get().setZoom(100);
                        break;
                    case 1:
                        CameraManager.get().setZoom(firstZoom);
                        break;
                    case 2:
                        CameraManager.get().setZoom(secondZoom);
                        break;

                    default:
                        break;
                }

            }
        });
        buttonFlash = (ImageButton) findViewById(R.id.flashButton);
        buttonFlash.setOnClickListener(new View.OnClickListener() {
            // @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void onClick(View v) {
                toggleFlash();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        final SurfaceView surfaceView = (SurfaceView) findViewById(
                getResources().getIdentifier("preview_view", "id", package_name));
        surfaceHolder = surfaceView.getHolder();


        recycleOverlayImage();
        if (OVERLAY_MODE == OverlayMode.OM_MWOVERLAY) {
            MWOverlay.removeOverlay();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MWOverlay.addOverlay(Home.this, surfaceView);
                }
            }, 1);
        }

        if (hasSurface) {
            Log.i("Init Camera", "On resume");
            initCamera();
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScaner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScaner();
    }

    private void stopScaner() {
        /* Stops the scanner when the activity goes in background */
        if (OVERLAY_MODE == OverlayMode.OM_MWOVERLAY) {
            MWOverlay.removeOverlay();
        }

        imageOverlay.setImageDrawable(null);

        CameraManager.get().stopPreview();
        CameraManager.get().closeDriver();
        state = State.STOPPED;
        flashOn = false;
        updateFlash();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();

        CameraManager.get().updateCameraOrientation(rotation);

        super.onConfigurationChanged(config);
    }

    private void toggleFlash() {
        flashOn = !flashOn;
        updateFlash();
    }

    private void updateFlash() {

        if (!CameraManager.get().isTorchAvailable()) {
            buttonFlash.setVisibility(View.GONE);
            return;

        } else {
            buttonFlash.setVisibility(View.VISIBLE);
        }

        if (flashOn) {
            buttonFlash.setImageResource(R.drawable.flashbuttonon);
        } else {
            buttonFlash.setImageResource(R.drawable.flashbuttonoff);
        }

        CameraManager.get().setTorch(flashOn);

        buttonFlash.postInvalidate();

    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("Init Camera", "On Surface changed");
        initCamera();
        surfaceChanged = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
            // Handle these events so they don't launch the Camera app
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void decode(final byte[] data, final int width, final int height) {

        if (activeThreads >= MAX_THREADS || state == State.STOPPED) {
            return;
        }

        new Thread(new Runnable() {
            public void run() {
                activeThreads++;

                byte[] rawResult = BarcodeScanner.MWBscanGrayscaleImage(data, width, height);

                if (state == State.STOPPED) {
                    activeThreads--;
                    return;
                }

                BarcodeScanner.MWResult mwResult = null;

                if (rawResult != null && BarcodeScanner.MWBgetResultType() == BarcodeScanner.MWB_RESULT_TYPE_MW) {

                    BarcodeScanner.MWResults results = new BarcodeScanner.MWResults(rawResult);

                    if (results.count > 0) {
                        mwResult = results.getResult(0);
                        rawResult = mwResult.bytes;
                    }

                } else if (rawResult != null
                        && BarcodeScanner.MWBgetResultType() == BarcodeScanner.MWB_RESULT_TYPE_RAW) {
                    mwResult = new BarcodeScanner.MWResult();
                    mwResult.bytes = rawResult;
                    mwResult.text = rawResult.toString();
                    mwResult.type = BarcodeScanner.MWBgetLastType();
                    mwResult.bytesLength = rawResult.length;
                }

                if (mwResult != null) {
                    state = State.STOPPED;
                    Message message = Message.obtain(Home.this.getHandler(), ID_DECODE_SUCCEED, mwResult);
                    message.arg1 = mwResult.type;
                    message.sendToTarget();
                } else {
                    Message message = Message.obtain(Home.this.getHandler(), ID_DECODE_FAILED);
                    message.sendToTarget();
                }

                activeThreads--;
            }
        }).start();
    }

    private void restartPreviewAndDecode() {
        if (state == State.STOPPED) {
            state = State.PREVIEW;
            Log.i("preview", "requestPreviewFrame.");
            CameraManager.get().requestPreviewFrame(getHandler(), ID_DECODE);
            CameraManager.get().requestAutoFocus(getHandler(), ID_AUTO_FOCUS);
        }
    }

    public void handleDecode(BarcodeScanner.MWResult result) {

        String typeName = result.typeName;
        String barcode = result.text;




        if (result.locationPoints != null && CameraManager.get().

                getCurrentResolution()

                != null
                && OVERLAY_MODE == OverlayMode.OM_MWOVERLAY)

        {
            MWOverlay.showLocation(result.locationPoints.points, result.imageWidth, result.imageHeight);
        }

        if (result.isGS1)

        {
            typeName += " (GS1)";
        }

        Log.i("SCAN 0", barcode);
        if(dbHelper.getCode(Home.this,barcode)){
            dbHelper.getpnameprice(txtpname,txtprice,pricetext,barcode);
            code=barcode;
            Log.i("SCAN 1", "yes");
        }else{
            AppMsg.makeText(Home.this,"Product Not Found",AppMsg.STYLE_ALERT).show();
        }

//        new AlertDialog.Builder(this)
//                .setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        if (decodeHandler != null) {
//                            decodeHandler.sendEmptyMessage(ID_RESTART_PREVIEW);
//                        }
//                    }
//                })
//                .setTitle(typeName)
//                .setMessage(barcode)
//                .setNegativeButton("Close", null)
//                .show();


    }

    private void initCamera() {

        if (ContextCompat.checkSelfPermission(Home.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            /* WHEN TARGETING ANDROID 6 OR ABOVE, PERMISSION IS NEEDED */
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this,
                    Manifest.permission.CAMERA)) {

                new AlertDialog.Builder(Home.this).setMessage("You need to allow access to the Camera")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(Home.this,
                                        new String[]{Manifest.permission.CAMERA}, 12322);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                }).create().show();
            } else {
                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.CAMERA},
                        12322);
            }
        } else {
            try {
                // Select desired camera resoloution. Not all devices
                // supports all
                // resolutions, closest available will be chosen
                // If not selected, closest match to screen resolution will
                // be
                // chosen
                // High resolutions will slow down scanning proccess on
                // slower
                // devices

                if (MAX_THREADS > 2 || PDF_OPTIMIZED) {
                    CameraManager.setDesiredPreviewSize(1280, 720);
                } else {
                    CameraManager.setDesiredPreviewSize(800, 480);
                }

                CameraManager.get().openDriver(surfaceHolder,
                        (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT));

                int maxZoom = CameraManager.get().getMaxZoom();
                if (maxZoom < 100) {
                    zoomButton.setVisibility(View.GONE);
                } else {
                    zoomButton.setVisibility(View.VISIBLE);
                    if (maxZoom < 300) {
                        secondZoom = maxZoom;
                        firstZoom = (maxZoom - 100) / 2 + 100;

                    }

                }
            } catch (IOException ioe) {
                displayFrameworkBugMessageAndExit(ioe.getMessage());
                return;
            } catch (RuntimeException e) {
                // Barcode Scanner has seen crashes in the wild of this
                // variety:
                // java.?lang.?RuntimeException: Fail to connect to camera
                // service
                displayFrameworkBugMessageAndExit(e.getMessage());
                return;
            }

            Log.i("preview", "start preview.");

            flashOn = false;

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    switch (zoomLevel) {
                        case 0:
                            CameraManager.get().setZoom(100);
                            break;
                        case 1:
                            CameraManager.get().setZoom(firstZoom);
                            break;
                        case 2:
                            CameraManager.get().setZoom(secondZoom);
                            break;

                        default:
                            break;
                    }

                }
            }, 300);
            CameraManager.get().startPreview();
            restartPreviewAndDecode();
            updateFlash();
        }
    }

    private void displayFrameworkBugMessageAndExit(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getIdentifier("app_name", "string", package_name));
        builder.setMessage(MSG_CAMERA_FRAMEWORK_BUG + message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
    }
    protected void recycleOverlayImage() {

        if (OVERLAY_MODE == OverlayMode.OM_IMAGE) {
            ((ImageView)imageOverlay.findViewById(R.id.imageOverlay)).setImageDrawable(getResources().getDrawable(R.drawable.overlay));
        }else{

            Drawable imageDrawable = imageOverlay.getDrawable();
            imageOverlay.setImageDrawable(null);

            if (imageDrawable!=null && imageDrawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageDrawable);

                if (!bitmapDrawable.getBitmap().isRecycled()) {
                    bitmapDrawable.getBitmap().recycle();
                }
            }
        }
    }

    @Override public void onBackPressed() {
        if( isCustomKeyboardVisible() ) hideCustomKeyboard(); else this.finish();
    }

    private KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override public void onKey(int primaryCode, int[] keyCodes) {
        }

        @Override public void onPress(int arg0) {
        }

        @Override public void onRelease(int primaryCode) {
            //Toast.makeText(Home.this,"release"+primaryCode,Toast.LENGTH_SHORT).show();
            EditText edittext=input_price;
            Editable editable=input_price.getText();
            int start = input_price.getSelectionStart();
            if( primaryCode==Codeone ) {
                edittext.setText(edittext.getText().append("1"));
                edittext.setSelection(start + 1);
            } else if( primaryCode==Codetwo ) {
                edittext.setText(edittext.getText().append("2"));
                edittext.setSelection(start + 1);
            } else if( primaryCode==Codethree ) {
                edittext.setText(edittext.getText().append("3"));
                edittext.setSelection(start + 1);
            } else if( primaryCode==Codefour ) {
                edittext.setText(edittext.getText().append("4"));
                edittext.setSelection(start + 1);
            } else if( primaryCode==Codefive ) {
                edittext.setText(edittext.getText().append("5"));
                edittext.setSelection(start + 1);
            } else if( primaryCode==Codesix ) {
                edittext.setText(edittext.getText().append("6"));
                edittext.setSelection(start + 1);
            } else if( primaryCode==Codeseven ) {
                edittext.setText(edittext.getText().append("7"));
                edittext.setSelection(start + 1);
            } else if( primaryCode==Codeeight ) {
                edittext.setText(edittext.getText().append("8"));
                edittext.setSelection(start + 1);
            } else if(primaryCode==Codenine){// Insert character
                edittext.setText(edittext.getText().append("9"));
                edittext.setSelection(start + 1);
            }else if( primaryCode==Codezeo ) {
                edittext.setText(edittext.getText().append("0"));
                edittext.setSelection(start + 1);
            }else if(primaryCode==Codedotzero){
                edittext.setText(edittext.getText().append(".00"));
                edittext.setSelection(start + 3);
            }else if(primaryCode==CodeClear){

                int curPostion = edittext.getSelectionEnd();
                if(curPostion!=0){
                    SpannableStringBuilder selectedStr = new
                            SpannableStringBuilder(edittext.getText());
                    selectedStr.replace(curPostion - 1, curPostion, "");
                    edittext.setText(selectedStr);
                    //this is to set the cursor position by -1 after deleting char/text
                    edittext.setSelection(curPostion - 1);
                }
            }else if(primaryCode==Codedot){
                edittext.setText(edittext.getText().append("."));
                edittext.setSelection(start + 1);
            }else if(primaryCode==Codeleft){
                if(start>0){
                    edittext.setSelection(start - 1);
                }
            }else if(primaryCode==Coderight){
                if(start<edittext.getText().length()){
                    edittext.setSelection(start + 1);
                }

            }else if(primaryCode==Codeent){
                hideCustomKeyboard();
            }
        }

        @Override public void onText(CharSequence text) {
        }

        @Override public void swipeDown() {
        }

        @Override public void swipeLeft() {
        }

        @Override public void swipeRight() {
        }

        @Override public void swipeUp() {
        }
    };

    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    public void showCustomKeyboard( View v ) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if( v!=null ) ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
               // barcodeView.setStatusText(result.getText());

                Log.i("SCAN 0", result.getText());
                if(dbHelper.getCode(Home.this,result.getText())){
                    dbHelper.getpnameprice(txtpname,txtprice,pricetext,result.getText());
                    code=result.getText();
                    Log.i("SCAN 1", "yes");
                }else{
                    AppMsg.makeText(Home.this,"Product Not Found",AppMsg.STYLE_ALERT).show();
                }
            }else{
                AppMsg.makeText(Home.this,"You cancelled the scanning",AppMsg.STYLE_ALERT).show();
            }

            //Do something with code result
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };



    public class reading extends AsyncTask<String ,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(Home.this);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            readfile();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
        }
    }

    void readfile(){

            File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/chkprice.txt");
            //Read text from file
            StringBuilder text = new StringBuilder();
            ArrayList<Model> linelist=new ArrayList<>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    String[] check=line.split(";");
                    linelist.add(new Model(check[0],check[1],check[2],check[3]));
                }
                br.close();
                Log.i("data check",linelist.size()+"");
                dbHelper.add_data(linelist);
            }
            catch (IOException e) {
                Log.i("error","error");
                //You'll need to add proper error handling here
            }

    }



    void checkpremission(String which){
        // Here, thisActivity is the current activity
        if(which.equals("storage")){
            if (ContextCompat.checkSelfPermission(Home.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_STORAGE);
                }
            }
        }else if(which.equals("camera")){
            if (ContextCompat.checkSelfPermission(Home.this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this,
                        Manifest.permission.CAMERA)) {

                } else {
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

       if(requestCode==MY_PERMISSIONS_REQUEST_STORAGE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                File myDirectory = new File(Environment.getExternalStorageDirectory(), "Scanner");

                if(!myDirectory.exists()) {
                    myDirectory.mkdirs();
                }
                checkpremission("camera");
            } else {
                checkpremission("camera");
            }
            return;
        }else if(requestCode==MY_PERMISSIONS_REQUEST_CAMERA){
           // If request is cancelled, the result arrays are empty.
           if (grantResults.length > 0
                   && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           } else {
               checkpremission("storage");
           }
           return;
       }
    }


    View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id=view.getId();
            switch (id){
                case R.id.save:
                    if(dbHelper.getCode(Home.this,code)){
                        dbHelper.updateStatus(input_price.getText().toString().trim(),code);
                        updatepricelay.setVisibility(View.GONE);
                        btnprice.setVisibility(View.VISIBLE);
                        sync.setVisibility(View.VISIBLE);
                        input_price.setVisibility(View.GONE);
                        txtpname.setVisibility(View.VISIBLE);
                        txtprice.setVisibility(View.VISIBLE);
                        txtprice.setText(input_price.getText().toString().trim());
                        pricetext.setVisibility(View.VISIBLE);
                        hideCustomKeyboard();
                        AppMsg.makeText(Home.this,"Product Updated",AppMsg.STYLE_CONFIRM).show();

                    }else{
                        AppMsg.makeText(Home.this,"Product Not Found",AppMsg.STYLE_ALERT).show();
                    }
                 //   barcodeView.resume();
                    break;
                case R.id.cancel:
                    updatepricelay.setVisibility(View.GONE);
                    btnprice.setVisibility(View.VISIBLE);
                    input_price.setVisibility(View.GONE);
                    txtpname.setVisibility(View.VISIBLE);
                    txtprice.setVisibility(View.VISIBLE);
                    pricetext.setVisibility(View.VISIBLE);
                    hideCustomKeyboard();
              //      barcodeView.resume();
                    break;
                case R.id.btnprice:
                    if(txtpname.getText().toString().trim().equals("")){
                    }else{
                        updatepricelay.setVisibility(View.VISIBLE);
                        btnprice.setVisibility(View.GONE);
                        input_price.setVisibility(View.VISIBLE);
                        sync.setVisibility(View.GONE);
                        txtpname.setVisibility(View.GONE);
                        txtprice.setVisibility(View.GONE);
                        pricetext.setVisibility(View.VISIBLE);
                        input_price.setText(txtprice.getText().toString().trim());
                   //     barcodeView.pause();
                    }

                    break;
                case R.id.cardview:
                   // scan();
                    break;
                case R.id.sync:
                    dbHelper.syncdatabase(Home.this);
                    sync.setVisibility(View.GONE);
                 //   barcodeView.resume();
                    break;
            }
        }
    };


/*

    void scan(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);

        integrator.setPrompt("Scan!!");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.action_location:
                Intent connect=new Intent(Home.this,Connect.class);
                startActivity(connect);
                break;

            case R.id.action_readfile:
                dbHelper.deleteAllRows();
                dbHelper.againCreate();
                new reading().execute();
                //readfile();
                break;

            case R.id.action_clear:
                dbHelper.deleteAllRows();
                break;

            case R.id.action_sendfile:
                File myDirectory = new File(Environment.getExternalStorageDirectory(), "Scanner");
                File newfile=new File(myDirectory.getAbsolutePath()+"/"+"chkprice.txt");
                if(newfile.exists()){
                    new MyCopy().execute();
                }else{
                    Toast.makeText(Home.this,"No file exists to send to server",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private class MyCopy extends AsyncTask<String, String, String> {

        String z = "";
        String username = "", password = "", servername = "", filestocopy = "";

        @Override
        protected void onPreExecute() {
            username = "";
            password = "";
            servername = "smb://";
            File myDirectory = new File(Environment.getExternalStorageDirectory(), "Scanner");
            File newfile=new File(myDirectory.getAbsolutePath()+"/"+"chkprice.txt");
            filestocopy = newfile.getAbsolutePath().toString();
        }



        @Override
        protected String doInBackground(String... params) {

            File A = new File(filestocopy);
            String filename = A.getName();

            NtlmPasswordAuthentication auth1 = new NtlmPasswordAuthentication(servername, null, null);

            try {

                SmbFile sfile = new SmbFile(servername + "/" + filename, auth1);
                // if (!sfile.exists())
                // sfile.createNewFile();
                sfile.connect();

                InputStream in = new FileInputStream(A);
                //OutputStream sfos = new FileOutputStream(sfile);
                SmbFileOutputStream sfos = new SmbFileOutputStream(sfile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    sfos.write(buf, 0, len);
                }
                in.close();
                sfos.close();

                z = "File copied successfully";
            } catch (Exception ex) {

                z = z + " " + ex.getMessage().toString();
            }

            return z;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }


}
