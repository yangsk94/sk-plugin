package com.sk.plugin.utils;

import com.sk.BuildParams;
import com.sk.GsonUtls;
import com.sk.PgyerInfoBean;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author ysk
 */
public class DingUtils {
    private static final String LOG_UPLOAD_TASK = "DingUtils + ";
    private static final String Webhook = "https://oapi.dingtalk.com/robot/send?access_token=";


    public static void pushMessageToDing(String apkPath, String fileName, String accessToken, PgyerInfoBean bean) {
        System.out.println(LOG_UPLOAD_TASK + "pushMessageToDing apkPath: " + apkPath + "; fileName: " + fileName);

        BuildParams.DingLinkReq req = BuildParams.DingLinkReq.Companion.getLink(bean.getData().getBuildCreated(),
                bean.getData().getBuildVersion(), bean.getData().getBuildQRCodeURL(), bean.getData().getBuildShortcutUrl());
        RequestBody fileBody = new FormBody.Builder()
                .add("msgtype", "lint")
                .add("link", GsonUtls.INSTANCE.getGson().toJson(req)).build();

        Request request = new Request.Builder()
                .header("content-type", "application/x-www-form-urlencoded")
                .url(Webhook + accessToken)
                .post(fileBody)
                .build();
        Response response;
        try {
            response = new OkHttpClient().newCall(request).execute();

            String responseMsg = response.body().string();

            System.out.println(LOG_UPLOAD_TASK + " pushMessageToDing upload success path: " + responseMsg + "; apkPath: " + apkPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
