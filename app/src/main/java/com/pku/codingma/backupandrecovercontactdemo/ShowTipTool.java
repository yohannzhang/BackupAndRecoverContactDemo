package com.pku.codingma.backupandrecovercontactdemo;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ma on 2016/3/19.
 */
public class ShowTipTool {
    public static void showTip(Context context,String content){
        Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
    }
}
