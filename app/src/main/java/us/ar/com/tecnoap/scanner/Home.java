package us.ar.com.tecnoap.scanner;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    public static int PICKFILE_REQUEST_CODE=1000;
    DBHelper dbHelper;
    EditText input_price;
    LinearLayout updatepricelay;
    public static int MY_PERMISSIONS_REQUEST_CAMERA=1000;
    public static int MY_PERMISSIONS_REQUEST_STORAGE=2000;
    String code;
    ProgressDialog pd;
    private CompoundBarcodeView barcodeView;


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
        dbHelper=new DBHelper(Home.this);
        checkpremission("storage");

    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.setStatusText(result.getText());
                Log.i("SCAN 0", result.getText());
                if(dbHelper.getCode(Home.this,"7790036020211")){
                    dbHelper.getpnameprice(txtpname,txtprice,pricetext,"7790036020211");
                    code="7790036020211";
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
                    //Log.i("readline",check[0]+"---"+check[1]+"---"+check[2]+"---"+check[3]+"---");
                   // text.append('\n');
                }
                br.close();
                Log.i("data check",linelist.size()+"");
                dbHelper.add_data(linelist);
                //dbHelper.addData(check[0],check[1],check[2],check[3]);
                //dbHelper.getData();
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
