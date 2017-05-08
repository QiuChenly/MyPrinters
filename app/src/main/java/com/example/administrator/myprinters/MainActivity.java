package com.example.administrator.myprinters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn=(Button)findViewById(R.id.mPrintButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    printDocx(Environment.getExternalStorageDirectory()+"/mAPPPrintTemps/Temps.docx");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 判断APP是否安装在手机内
     * @param context 直接传This即可
     * @param packagename 包名
     * @return TRUE=存在 FALSE=不存在
     */
    private boolean isAppInstalled(Context context, String packagename)
    {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        }catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        if(packageInfo ==null){
            return false;
        }else{
            return true;
        }
    }


    /**
     * 异步打印文档 安卓4.4+ targetSdkVersion 21 即可运行
     * @param URIs 本地文件地址
     * @throws IOException IO异常
     */
    public void printDocx(String URIs) throws IOException {
        if (isAppInstalled(getApplicationContext(), "com.dynamixsoftware.printershare.amazon")) {
            try {
                Uri data_uri = Uri.fromFile(new File(URIs));
                String data_type = "application/msword";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setPackage("com.dynamixsoftware.printershare.amazon");//未注册之前com.dynamixsoftware.printershare，注册后加上amazon
                i.setDataAndType(data_uri, data_type);
                startActivity(i);
                return ;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "没有找到printershare!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "请先安装PrintShare!", Toast.LENGTH_SHORT).show();
            FileUtils.getInstance(this.getApplicationContext()).copyAssetsToSD("PrinterShare.apk","Printer.apk").setFileOperateCallback(new FileUtils.FileOperateCallback() {
                @Override
                public void onSuccess() {
                    // TODO: 文件复制成功时，主线程回调
                    System.out.print("文件复制完成");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String url=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Printer.apk";
                    File file = new File(url);
                    intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
                    MainActivity.this.getApplicationContext().startActivity(intent);
                }

                @Override
                public void onFailed(String error) {
                    // TODO: 文件复制失败时，主线程回调
                    System.out.print("文件复制失败");
                }
            });
        }
    }
}
