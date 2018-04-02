package com.starssoft.snake;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;


public class MainActivity extends Activity implements View.OnClickListener, MySnakeView.GameViewInterface, CompoundButton.OnCheckedChangeListener {

    private Button bt_t, bt_b, bt_r, bt_l, bt_set;
    private MySnakeView gv;
    private CheckBox cb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewId();
        initListener();
    }

    private void initViewId() {
        bt_t = (Button) findViewById(R.id.bt_t);
        bt_b = (Button) findViewById(R.id.bt_b);
        bt_l = (Button) findViewById(R.id.bt_l);
        bt_r = (Button) findViewById(R.id.bt_r);
        bt_set = (Button) findViewById(R.id.bt_set);
        cb = (CheckBox) findViewById(R.id.cb);
        gv = (MySnakeView) findViewById(R.id.gv);
    }

    private void initListener() {
        bt_l.setOnClickListener(this);
        bt_b.setOnClickListener(this);
        bt_t.setOnClickListener(this);
        bt_r.setOnClickListener(this);
        bt_set.setOnClickListener(this);
        gv.setGameViewInterface(this);
        cb.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_t:
                gv.t();
                break;
            case R.id.bt_l:
                gv.l();
                break;
            case R.id.bt_r:
                gv.r();
                break;
            case R.id.bt_b:
                gv.b();
                break;
            case R.id.bt_set:
                break;
        }
    }

    @Override
    public void over(int sum) {

        if (sum < 0) {
            sum = 0;
        }
        Log.i("My", "ddd");
        final int finalSum = sum;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("游戏结束!你获得" + finalSum + "分");
                builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                    }
                });
                builder.setNeutralButton("从新开始", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.onCreate(null);
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            cb.setText("继续");
            gv.isPause(true);
        } else {
            cb.setText("暂停");
            gv.isPause(false);
        }
    }
}
