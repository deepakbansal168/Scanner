package us.ar.com.tecnoap.scanner;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.MalformedURLException;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class Connect extends AppCompatActivity implements Runnable {


    EditText ip,username,password;
    Button btnconnect;
    String filename;

    void init(){
        ip=(EditText)findViewById(R.id.ip);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        btnconnect=(Button)findViewById(R.id.connect);
        btnconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread().run();
            }
        });


    }

    @Override
    public void run() {
       // String ip = "112.196.25.12";
        // String user = SyncStateContract.Constants.username + ":" + SyncStateContract.Constants.password;
        String url = "smb://" + ip.getText().toString().trim();

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",
                username.getText().toString().trim(), password.getText().toString().trim());
        SmbFile root= null;
        try {
            root = new SmbFile(url, auth);
            String[] files = root.list();
            for (String fileName : files) {
                Log.d("GREC", "File: " + fileName);
                filename=fileName;
                handler.sendEmptyMessage(100);
            }
        } catch (MalformedURLException e) {
            Toast.makeText(Connect.this,"error 1",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (SmbException e) {
            Toast.makeText(Connect.this,"error 2",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==100){
                Toast.makeText(Connect.this,filename,Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        init();
    }



}
