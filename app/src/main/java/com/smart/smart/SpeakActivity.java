package com.smart.smart;


import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smart.smart.orderEntity.OrderEntity;
import com.smart.smart.speechRecog.CommonRecogParams;
import com.smart.smart.speechRecog.IStatus;
import com.smart.smart.speechRecog.MessageRecogListener;
import com.smart.smart.speechRecog.MyRecog;
import com.smart.smart.wordSegment.SegmentResult;

import java.util.ArrayList;
import java.util.Map;

public class SpeakActivity extends AppCompatActivity implements IStatus {
    protected TextView txtResult;
    protected TextView txtLog;
    protected Button speakBtn;

    protected MyRecog myRecog;
    private Handler handler;

    protected boolean enableOffline = false;


    private static final String TAG = "SpeakActivity";

    /**
     * 控制UI按钮的状态
     */
    protected int status;

    /*
     * Api的参数类，仅仅用于生成调用START的json字符串，本身与SDK的调用无关
     */
    protected CommonRecogParams apiParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak);
       ActionBar actionBar=this.getSupportActionBar();
       // ActionBar actionBar=getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        initPermission();
        initView();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMsg(msg);
            }
        };
        initRecog();
    }

    /**
     * 销毁时需要释放识别资源。
     */
    @Override
    protected void onDestroy() {
        myRecog.release();
        Log.i(TAG,"onDestroy");
        super.onDestroy();
    }
    /*
   开始录音，点击“开始”按钮后调用
    */
    protected void start(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SpeakActivity.this);
        //  上面的获取是为了生成下面的Map， 自己集成时可以忽略
        // / 集成时不需要上面的代码，只需要params参数。
        final Map<String, Object> params = apiParams.fetch(sp);
        // 这里打印出params， 填写至您自己的app中，直接调用下面这行代码即可。
        myRecog.start(params);
    }

    /**
     * 在onCreate中调用。初始化识别控制类MyRecognizer
     */
    protected void  initRecog(){
        MessageRecogListener listener = new MessageRecogListener(handler);
        myRecog = new MyRecog(this, listener);
        apiParams = getApiParams();
        status = STATUS_NONE;
    }

    protected  CommonRecogParams getApiParams(){
        return new CommonRecogParams(this);
    };

    /*
    结束录音，手动停止录音。SDK会识别在此过程中的录音。点击“停止”按钮后调用
     */
    private void stop(){
        myRecog.stop();
    }
    /**
     * 开始录音后，取消这次录音。SDK会取消本次识别，回到原始状态。点击“取消”按钮后调用。
     */
    private void cancel() {
        myRecog.cancel();
    }



    //以上为语音SDK 调用，以下为UI部分

    protected void  initView(){
        txtResult=findViewById(R.id.txtResult);
        txtLog=findViewById(R.id.txtLog);
        speakBtn=findViewById(R.id.speakBtn);
        txtResult.setText("");
        speakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (status) {
                    case STATUS_NONE: // 初始状态
                        start();
                        status = STATUS_WAITING_READY;
                        updateBtnTextByStatus();
                        txtResult.setText("");
                        txtLog.setText("");

                        break;
                    case STATUS_WAITING_READY: // 调用本类的start方法后，即输入START事件后，等待引擎准备完毕。
                    case STATUS_READY: // 引擎准备完毕。
                    case STATUS_SPEAKING:
                    case STATUS_RECOGNITION:
                        stop();
                        status = STATUS_STOPPED; // 引擎识别中
                        updateBtnTextByStatus();
                        break;
                    case STATUS_STOPPED: // 引擎识别中
                        cancel();
                        status = STATUS_NONE; // 识别结束，回到初始状态
                        updateBtnTextByStatus();
                        break;
                    default:
                        break;
                }

            }
        });

    }




    protected void handleMsg(Message msg) {
        if (txtResult != null && msg.obj != null&&!(msg.obj instanceof SegmentResult)&& msg.obj!=""&&msg.arg2==1) {
            txtResult.setText(msg.obj.toString());
        }
        if(msg.obj instanceof  SegmentResult){
            Log.i(TAG,"get segmentResult");
            Log.i(TAG,"equip :"+((SegmentResult) msg.obj).getEquipment());
            Log.i(TAG,"what:"+msg.what);
           // String[] order={result.getEquipment(),result.getAction(),result.getAction(),result.getMode()};
            AlertDialog.Builder builder = new AlertDialog.Builder(SpeakActivity.this);
            //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
            View view = LayoutInflater.from(SpeakActivity.this).inflate(R.layout.dialog_segement, null);
            builder.setView(view);
            final EditText edit_equi = (EditText)view.findViewById(R.id.edit_equi);
            final EditText edit_act=(EditText)view.findViewById(R.id.edit_act);
            final EditText edit_value=(EditText)view.findViewById(R.id.edit_value);
            final EditText edit_mode=(EditText)view.findViewById(R.id.edit_mode);
            edit_equi.setText(((SegmentResult) msg.obj).getEquipment());
            edit_act.setText(((SegmentResult) msg.obj).getAction());
            edit_value.setText(((SegmentResult) msg.obj).getValue());
            edit_mode.setText(((SegmentResult) msg.obj).getMode());
            builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //edit之后确认的指令实体！！！！！！
                    OrderEntity orderEntity=new OrderEntity(edit_equi.getText().toString(),edit_act.getText().toString(),edit_value.getText().toString(),edit_mode.getText().toString());
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //cancel 在设置了cancelListener时有区别，在这里没必要设置
                }
            });

            builder.setTitle("指令确认");
            builder.create().show();

        }
        switch(msg.what) { // 处理MessageStatusRecogListener中的状态回调
            case STATUS_FINISHED:
                if (msg.arg2 == 1) {
                    assert msg.obj != null;
                    txtResult.setText(msg.obj.toString());
                }
                status = msg.what;
                updateBtnTextByStatus();
                break;
            case STATUS_NONE:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                status = msg.what;
                Log.i(TAG,"status "+status);
                updateBtnTextByStatus();
                break;
            default:
                break;
        }
    }

    private void updateBtnTextByStatus() {
        switch (status) {
            case STATUS_NONE:
                speakBtn.setText("开始录音");
                speakBtn.setEnabled(true);
                break;
            case STATUS_WAITING_READY:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                speakBtn.setText("停止录音");
                speakBtn.setEnabled(true);
                break;

            case STATUS_STOPPED:
                speakBtn.setText("取消识别");
                speakBtn.setEnabled(true);
                break;
            default:
                break;
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Back 选择操作

       if(item.getItemId()==16908332){
       // if(item.getItemId()==R.id.homeAsUp){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

}
