package com.example.help2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE};
    int[] res = new int[]{R.drawable.icon_address, R.drawable.icon_shoucang, R.drawable.icon_weishoucang};

    /**
     * 主要方法：创建一个bitmap放于画布之上进行绘制
     */
    private static Bitmap convertViewToBitmap(View tempView) {
        Bitmap bitmap = Bitmap.createBitmap(tempView.getWidth(),
                tempView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tempView.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MyImageview imageview = findViewById(R.id.image);
        imageview.setImageResource(R.drawable.icon_address);
        final ImageView imageView2 = findViewById(R.id.image2);
        checkPermission();
        findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = convertViewToBitmap(imageview);
                imageView2.setImageBitmap(bitmap);
                saveImageToGallery(bitmap);
                imageview.clear();
                imageview.setImageResource(res[new Random().nextInt(2)]);
            }
        });
    }

    /**
     * 保存图片到本地
     *
     * @param bmp
     * @return
     */
    public int saveImageToGallery(Bitmap bmp) {
        //生成路径
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dirName = "dou";
        File appDir = new File(root, dirName);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        //文件名为时间
        long timeStamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(timeStamp));
        String fileName = sd + ".jpg";

        //获取文件
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            //通知系统相册刷新
            MainActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(new File(file.getPath()))));
            return 2;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 检查权限
     */
    public void checkPermission() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 100);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                Log.d("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }
}
