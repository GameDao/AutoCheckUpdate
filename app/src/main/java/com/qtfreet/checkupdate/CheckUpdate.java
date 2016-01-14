package com.qtfreet.checkupdate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;

import com.apkfuns.logutils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by qtfreet on 2016/1/5.
 */
public class CheckUpdate {

    //单例化检查更新类
    private CheckUpdate() {
    }
    private Context mcontext;
    private static CheckUpdate checkUpdate = null;

    public static CheckUpdate getInstance() {
        if (checkUpdate == null) {
            checkUpdate = new CheckUpdate();
        }
        return checkUpdate;
    }


    public void startCheck(Context context) {
        mcontext = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                InputStream is = null;
                HttpURLConnection conn = null;
                try {
                    //获取服务器端内容，检查是否存在更新，自己开发的话可以采用volley来检查更新
                    url = new URL(Constant.APK_URL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    LogUtils.e(sb);
                    br.close();
                    is.close();
                    Message message = new Message();
                    message.what = 0;
                    message.obj = sb.toString();
                    mhanler.sendMessage(message);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    private Handler mhanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    JSONObject js = null;
                    try {
                        js = new JSONObject(msg.obj.toString());
                        int version = js.getInt("version");
                        String intro = js.getString("introduction");
                        String apkurl = js.getString("url");
                        compareVersion(version, intro, apkurl); //与本地版本进行比较
                        LogUtils.e(apkurl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void compareVersion(int newVersion, String intro, final String url) {
        int versionCode = getVerCode(mcontext);
        LogUtils.e(versionCode);
        LogUtils.e(intro);
        LogUtils.e(url);
        if (newVersion > versionCode) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
            builder.setTitle("发现更新");
            builder.setMessage(intro);
            builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(mcontext, DownloadService.class);
                    intent.putExtra("url", url);
                    mcontext.startService(intent);
                }
            });
            builder.setNegativeButton("退出",null);
            builder.show();
        } else {
            return;
        }
    }


    private int getVerCode(Context ctx) {
        int currentVersionCode = 0;
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            currentVersionCode = info.versionCode; // 版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersionCode;
    }

}
