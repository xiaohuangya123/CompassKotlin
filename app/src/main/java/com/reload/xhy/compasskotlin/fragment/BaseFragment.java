package com.reload.xhy.compasskotlin.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reload.xhy.compasskotlin.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends Fragment {

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA
    };

    private static final int PERMISSON_REQUESTCODE = 010;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23
                && getActivity().getApplicationInfo().targetSdkVersion >= 23) {
            if (isNeedCheck) {
                checkRuntimePermissions(needPermissions);
            }
        }
    }

    private void checkRuntimePermissions(String[] permissions) {
        //获取没有被授权的权限
        String[] deniedPermission = findDeniedPermission(permissions);
        if(deniedPermission != null && deniedPermission.length>0){
            ActivityCompat.requestPermissions(requireActivity(), deniedPermission,PERMISSON_REQUESTCODE);
        }
    }

    //获取没有被授权的权限
    protected String[] findDeniedPermission(String[] permissions){
        List<String> deniedPermissionList = new ArrayList<>();
        for (int i=0;i<permissions.length;i++){
            if(ContextCompat.checkSelfPermission(requireContext(), permissions[i])
                    != PackageManager.PERMISSION_GRANTED){
                deniedPermissionList.add(permissions[i]);
            }
        }
        return  deniedPermissionList.toArray(new String[deniedPermissionList.size()]);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("TTT", "11111111111111111");
        if(PERMISSON_REQUESTCODE == requestCode){
            Log.d("TTT", "222222222222222222");
            if(!verifyPermissions(grantResults)){
                Log.d("TTT", "333333333333333");
                showMissingPermissionDialog();
                isNeedCheck = false;
            }else {
                Log.d("TTT", "4444444444444444444");

            }
        }
    }

    protected void showMissingPermissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage(R.string.notifyMsg);

        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });

        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    //启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        startActivity(intent);
    }

    //检测是否所有的权限都已经授权
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }




}
