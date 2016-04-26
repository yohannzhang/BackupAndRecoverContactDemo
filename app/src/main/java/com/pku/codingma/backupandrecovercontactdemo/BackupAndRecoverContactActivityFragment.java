package com.pku.codingma.backupandrecovercontactdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dd.CircularProgressButton;

import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class BackupAndRecoverContactActivityFragment extends Fragment implements View.OnClickListener{
    CircularProgressButton mBackupContactButton;
    CircularProgressButton mRecoverContactButton;

    //标记消息的来源
    public final int BACKUP_WHAT = 0;
    public final int RECOVER_WHAT = 1;

    //标记成功还是失败
    public final int SUCCESS_FLAG = 1;
    public final int FAIL_FLAG = 0;

    //用于进行备份和还原操作的ContactHandler内部类
    ContactInfo.ContactHandler handler = ContactInfo.ContactHandler.getInstance();

    public BackupAndRecoverContactActivityFragment() {

    }

    Handler mBackupAndRecoverProcessHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BACKUP_WHAT){
                //add your action
                if (msg.arg1 == SUCCESS_FLAG){
                    mBackupContactButton.setProgress(100);
                }else {
                    mBackupContactButton.setProgress(-1);
                }
            }else if (msg.what == RECOVER_WHAT){
                //add your action
                if (msg.arg1 == SUCCESS_FLAG){
                    mRecoverContactButton.setProgress(100);
                }else {
                    mRecoverContactButton.setProgress(-1);
                }
            }
            ShowTipTool.showTip(getActivity(), msg.obj.toString());
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_backup_and_recover_contact, container, false);
        mBackupContactButton = (CircularProgressButton) view.findViewById(R.id.backup_contact_button);
        mRecoverContactButton = (CircularProgressButton) view.findViewById(R.id.recover_contact_button);
        initEvent();
        return view;
    }

    private void initEvent() {
        mRecoverContactButton.setOnClickListener(this);
        mBackupContactButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backup_contact_button:
                backup_contact();
                break;
            case R.id.recover_contact_button:
                recover_contact();
                break;
            default:
                break;
        }
    }

    public void backup_contact(){
        //让按钮进入工作状态
        mBackupContactButton.setIndeterminateProgressMode(true);
        mBackupContactButton.setProgress(50);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //新建一条Handler处理的消息
                Message message = new Message();
                try{
                    // 进行备份联系人信息动作
                    handler.backupContacts(getActivity(), handler.getContactInfo(getActivity()));
                    //如果顺利，则将消息的参数设置为成功
                    message.obj = "backup success";
                    message.arg1 = SUCCESS_FLAG;
                }catch (Exception e){
                    //如果出现异常，则将消息的参数设置为失败
                    message.obj = "backup fail";
                    message.arg1 = FAIL_FLAG;
                    e.printStackTrace();
                }finally {
                    //最后设置消息来源并发送
                    message.what = BACKUP_WHAT;
                    mBackupAndRecoverProcessHandler.sendMessage(message);
                }
            }
        }).start();
    }

    //与backup基本相同，不再注释
    public void recover_contact(){
        mRecoverContactButton.setIndeterminateProgressMode(true);
        mRecoverContactButton.setProgress(50);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                try {
                    // 获取要恢复的联系人信息
                    List<ContactInfo> infoList = handler.restoreContacts();
                    for (ContactInfo contactInfo : infoList) {
                        // 恢复联系人
                        handler.addContacts(getActivity(), contactInfo);
                    }
                    message.obj = "recover success";
                    message.arg1 = SUCCESS_FLAG;
                } catch (Exception e) {
                    message.obj = "recover fail";
                    message.arg1 = FAIL_FLAG;
                    e.printStackTrace();
                }finally {
                    message.what = RECOVER_WHAT;
                    mBackupAndRecoverProcessHandler.sendMessage(message);
                }
            }
        }).start();
    }
}

