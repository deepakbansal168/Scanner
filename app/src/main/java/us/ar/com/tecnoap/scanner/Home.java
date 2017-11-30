package us.ar.com.tecnoap.scanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.devspark.appmsg.AppMsg;
import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

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

public class Home extends AppCompatActivity {


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
    private CompoundBarcodeView barcodeView;
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
    KeyboardView mKeyboardView;


    void init(){
        save=(RelativeLayout)findViewById(R.id.save);
        cancel=(RelativeLayout)findViewById(R.id.cancel);
        btnprice=(Button)findViewById(R.id.btnprice);
        scanproduct=(CardView)findViewById(R.id.cardview);
        sync=(RelativeLayout)findViewById(R.id.sync);
        input_price=(EditText)findViewById(R.id.input_price);
        pricetext=(TextView)findViewById(R.id.txtprice);
        barcodeView = (CompoundBarcodeView)findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);



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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
                barcodeView.setStatusText(result.getText());
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
                            MY_PERMISSIONS_REQUEST_STORAGE);
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
                    barcodeView.resume();
                    break;
                case R.id.cancel:
                    updatepricelay.setVisibility(View.GONE);
                    btnprice.setVisibility(View.VISIBLE);
                    input_price.setVisibility(View.GONE);
                    txtpname.setVisibility(View.VISIBLE);
                    txtprice.setVisibility(View.VISIBLE);
                    pricetext.setVisibility(View.VISIBLE);
                    hideCustomKeyboard();
                    barcodeView.resume();
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
                        barcodeView.pause();
                    }

                    break;
                case R.id.cardview:
                   // scan();
                    break;
                case R.id.sync:
                    dbHelper.syncdatabase(Home.this);
                    sync.setVisibility(View.GONE);
                    barcodeView.resume();
                    break;
            }
        }
    };
    @Override
    public void onResume() {
        barcodeView.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        barcodeView.pause();
        super.onPause();
    }

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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }


}
