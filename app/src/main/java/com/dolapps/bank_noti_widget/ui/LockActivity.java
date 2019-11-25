package com.dolapps.bank_noti_widget.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.*;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
    private boolean isMain = false;
    private LinearLayoutCompat keypad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        isWidget = getIntent().getBooleanExtra("isWidget", false);
        isMain = getIntent().getBooleanExtra("isMain", false);

        imgs = new AppCompatImageView[]{findViewById(R.id.indicate1), findViewById(R.id.indicate2), findViewById(R.id.indicate3), findViewById(R.id.indicate4), null};
        et = findViewById(R.id.invisibleEditText);
        et.clearFocus();
        et.requestFocus();


        state = getIntent().getIntExtra("state", 0);

        pref= this.getSharedPreferences("bankNotiWidget", this.MODE_PRIVATE); // 선언
        String tmp = pref.getString("password", "");
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

        keypad = findViewById(R.id.num_keypad);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatButton button = (AppCompatButton)v;
                et.setText(et.getText().toString() + button.getText());
                et.setSelection(et.length());
            }
        };
        for(int i = 0; i < keypad.getChildCount()-1; i++){
            LinearLayoutCompat row = (LinearLayoutCompat)keypad.getChildAt(i);
            for(int j = 0; j < row.getChildCount(); j++){
                AppCompatButton button = (AppCompatButton)row.getChildAt(j);
                button.setOnClickListener(listener);
            }
        }
        ((LinearLayoutCompat)keypad.getChildAt(keypad.getChildCount()-1)).getChildAt(1).setOnClickListener(listener);
        ((LinearLayoutCompat)keypad.getChildAt(keypad.getChildCount()-1))
                .getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et.length()>0) {
                    et.setText(et.getText().toString().substring(0, et.length() - 1));
                    et.setSelection(et.length());
                }
            }
        });

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
                    if(count<=4 && before<=4) {
                        if (before < count) {
                            imgs[before].setImageDrawable(getDrawable(R.drawable.ic_input));
                        } else {
                            imgs[count].setImageDrawable(getDrawable(R.drawable.ic_not_input));
                        }
                    }
                }
                if(s.length()==4){
                    et.clearFocus();
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
                                    onBackPressed();
                                }
                                else{
                                    LockActivity.this.setResult(111);
                                    LockActivity.this.finish();
                                }
                            }
                        }
                        else{
                            Toast.makeText(LockActivity.this, "비밀번호를 틀렸습니다. 다시 입력하세요. ", Toast.LENGTH_LONG).show();
                            et.requestFocus();
                        }
                    }
                    tv.setText(titles[state]);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isWidget||isMain){
            ActivityCompat.finishAffinity(this);
        }
        else{
            finish();
        }
    }
}
