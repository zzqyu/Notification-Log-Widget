package com.dolapps.bank_noti_widget.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.*;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dolapps.bank_noti_widget.R;
import com.dolapps.bank_noti_widget.widget.BalanceWidget;

public class LockActivity extends AppCompatActivity {
    private AppCompatImageView[] imgs;
    private AppCompatEditText et;
    private AppCompatTextView tv;
    private SharedPreferences pref;
    private int state = 0; //0:기존비번, 1:비번, 2:확인
    private String[] titles = {"기존 비밀번호 입력", "비밀번호 입력", "비밀번호 확인"};
    private String[] pws = new String[3];
    private boolean isModi = true;
    private boolean isWidget = false;
    private InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lock);

        isWidget = getIntent().getBooleanExtra("isWidget", false);

        imgs = new AppCompatImageView[]{findViewById(R.id.indicate1), findViewById(R.id.indicate2), findViewById(R.id.indicate3), findViewById(R.id.indicate4), null};
        et = findViewById(R.id.invisibleEditText);
        et.clearFocus();
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        et.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT);


        state = getIntent().getIntExtra("state", 0);

        pref= this.getSharedPreferences("bankNotiWidget", this.MODE_PRIVATE); // 선언
        String tmp = pref.getString("password", "");
        Log.i("pref.getString2",tmp);
        Log.i("!!!!!!!!!!","!!!!!!!!!!!");
        if(state!=2) {
            if (tmp.equals("")) state = 1;
        }
        else {
            isModi = false;
            pws[state-1] = tmp;
        }
        pws[state] = tmp;

        tv = findViewById(R.id.title);
        tv.setText(titles[state]);




        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //imgs[et.getText().length()-1].setImageDrawable(getDrawable(R.drawable.ic_input));
                Log.i("onTextChanged", "s: "+s + ", start: "+ start + " , before: "+ before+ ", count: "+ count);
                if (s.length() > 4) {
                    et.setText(et.getText().toString().substring(0, 4));
                    et.setSelection(et.length());
                }
                else {
                    if(imgs[start]!=null&&count!=4) {
                        if (before == 0) {
                            imgs[start].setImageDrawable(getDrawable(R.drawable.ic_input));
                        } else {
                            imgs[start].setImageDrawable(getDrawable(R.drawable.ic_not_input));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    et.clearFocus();
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    String inputString = et.getText().toString();
                    et.setText("");
                    et.setSelection(et.length());
                    for(int i = 0 ; i<4; i++)
                        imgs[i].setImageDrawable(getDrawable(R.drawable.ic_not_input));


                    if(state==0){
                        if(pws[0].equals(inputString))state++;
                        else{
                            Toast.makeText(LockActivity.this, "비밀번호를 틀렸습니다. 다시 입력하세요. ", Toast.LENGTH_LONG).show();
                        }
                        et.requestFocus();
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                    else if(state == 1){
                        if(inputString.length()!=4){
                            Toast.makeText(LockActivity.this, "비밀번호를 4자리로 입력하세요. ", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(LockActivity.this, "입력한 비밀번호를 다시 입력하세요.", Toast.LENGTH_LONG).show();
                            pws[1] = inputString;
                            state++;
                        }
                        et.requestFocus();
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                    else{
                        if(pws[1].equals(inputString)){
                            if(isModi) {
                                SharedPreferences.Editor editor = pref.edit();// editor에 put 하기
                                editor.putString("password", inputString);
                                editor.commit(); //완료한다.
                                Toast.makeText(LockActivity.this, "비밀번호가 설정되었습니다. ", Toast.LENGTH_LONG).show();
                                LockActivity.this.setResult(222);
                                LockActivity.this.finish();
                            }
                            else{
                                Toast.makeText(LockActivity.this, "비밀번호가 확인되었습니다. ", Toast.LENGTH_LONG).show();
                                Intent broadIntent = new Intent(getApplicationContext(), BalanceWidget.class);
                                broadIntent.setAction(BalanceWidget.ACTION_UNLOCK_SUCESS);
                                getBaseContext().sendBroadcast(broadIntent);
                                if(isWidget){
                                    moveTaskToBack(true);
                                    finish();
                                }
                                else{
                                    LockActivity.this.setResult(111);
                                    LockActivity.this.finish();
                                }
                            }
                        }
                        else{
                            Toast.makeText(LockActivity.this, "비밀번호를 틀렸습니다. 다시 입력하세요. ", Toast.LENGTH_LONG).show();
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                            et.requestFocus();
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        }
                    }
                    tv.setText(titles[state]);
                    return true;
                }
                return false;
            }
        });

        if(isWidget){
            Log.i("isWidget", isWidget+"");
            Log.i("isWidget", "et.isFocused(): "+ et.isFocused());
            Log.i("isWidget", "imm: "+ imm.isActive());
            et.clearFocus();

            Log.i("isWidget", "et.isFocused(): "+ et.isFocused());
            Log.i("isWidget", "imm: "+ imm.isActive());
            et.requestFocus();
            imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT);

            Log.i("isWidget", "et.isFocused(): "+ et.isFocused());
            Log.i("isWidget", "imm: "+ imm.isActive());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
}
}
