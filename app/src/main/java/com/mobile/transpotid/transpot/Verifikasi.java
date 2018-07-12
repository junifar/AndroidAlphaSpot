package com.mobile.transpotid.transpot;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.transpotid.transpot.Session.UserSessionManager;
import com.mobile.transpotid.transpot.ZXingIntegration.IntentIntegrator;
import com.mobile.transpotid.transpot.ZXingIntegration.IntentResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class Verifikasi extends AppCompatActivity implements View.OnClickListener {
    ImageView stiker, longStiker;
    private Toolbar toolbar;
    private TextView scnQRCode, hslQRCode;
    private Button btnSubmit;
    AlertDialog.Builder builder;

    private int gambardefault,gambarphoto;
    private String token;
    UserSessionManager sessionManager;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        stiker = (ImageView) findViewById(R.id.imgSticker);
        longStiker = (ImageView) findViewById(R.id.imgLongSticker);
        scnQRCode = (TextView) findViewById(R.id.scan_qr_code);
        hslQRCode = (TextView) findViewById(R.id.txt_Hasil_QR);
        btnSubmit = (Button) findViewById(R.id.btn_submitTakePhoto);

//        ################### GET TOKEN #####################
        sessionManager = new UserSessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        token = user.get(UserSessionManager.KEY_TOKEN);
//        ###################################################

        gambardefault = Verifikasi.this.getResources().getDrawable(R.mipmap.ico_camera).getMinimumHeight();

        scnQRCode.setOnClickListener(this);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stiker.getDrawable().getMinimumHeight() == gambardefault || longStiker.getDrawable().getMinimumHeight() == gambardefault || hslQRCode.getText().toString().equals("")) {
                    builder = new AlertDialog.Builder(Verifikasi.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Please fill all the fields..");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else if (!hslQRCode.getText().toString().equals(token)) {
                    builder = new AlertDialog.Builder(Verifikasi.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Your Barcode no Valid !!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            hslQRCode.setText("");
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    finish();
                }
            }
        });

        stiker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageSticker();
            }
        });

        longStiker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageLogSticker();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void selectImageSticker() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent, 1);
    }

    private void selectImageLogSticker() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1){
            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }
            try {
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 5;

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                        bitmapOptions);

                stiker.setImageBitmap(bitmap);

                String path = android.os.Environment
                        .getExternalStorageDirectory()
                        + File.separator
                        + "Phoenix" + File.separator + "default";
                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {
                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK && requestCode == 2){
            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }
            try {
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 5;

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                        bitmapOptions);

                longStiker.setImageBitmap(bitmap);

                String path = android.os.Environment
                        .getExternalStorageDirectory()
                        + File.separator
                        + "Phoenix" + File.separator + "default";
                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {
                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanningResult != null){
            String scanContent = scanningResult.getContents();
            hslQRCode.setText(scanContent);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.scan_qr_code){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }
}
