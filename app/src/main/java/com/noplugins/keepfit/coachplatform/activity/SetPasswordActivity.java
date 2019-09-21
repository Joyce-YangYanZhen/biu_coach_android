package com.noplugins.keepfit.coachplatform.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.noplugins.keepfit.coachplatform.MainActivity;
import com.noplugins.keepfit.coachplatform.R;
import com.noplugins.keepfit.coachplatform.base.BaseActivity;
import com.noplugins.keepfit.coachplatform.bean.LoginBean;
import com.noplugins.keepfit.coachplatform.global.AppConstants;
import com.noplugins.keepfit.coachplatform.util.SpUtils;
import com.noplugins.keepfit.coachplatform.util.net.Network;
import com.noplugins.keepfit.coachplatform.util.net.entity.Bean;
import com.noplugins.keepfit.coachplatform.util.net.progress.ProgressSubscriber;
import com.noplugins.keepfit.coachplatform.util.net.progress.SubscriberOnNextListener;
import com.noplugins.keepfit.coachplatform.util.ui.LoadingButton;
import rx.Subscription;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetPasswordActivity extends BaseActivity {
    @BindView(R.id.tiaoguo_tv)
    TextView tiaoguo_tv;
    @BindView(R.id.sure_btn)
    LoadingButton sure_btn;
    @BindView(R.id.edit_password_number)
    EditText edit_password_number;
    @BindView(R.id.edit_password_again)
    EditText edit_password_again;

    @Override
    public void initBundle(Bundle parms) {

    }

    @Override
    public void initView() {
        setContentLayout(R.layout.activity_set_password);
        ButterKnife.bind(this);
        isShowTitle(false);
    }

    @Override
    public void doBusiness(Context mContext) {
        tiaoguo_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetPasswordActivity.this, SelectRoleActivity.class);
                startActivity(intent);
                finish();
            }
        });
        sure_btn.setBtnOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edit_password_number.getText().toString())) {
                    Toast.makeText(SetPasswordActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(edit_password_again.getText().toString())) {
                    Toast.makeText(SetPasswordActivity.this, "再次输入的密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!edit_password_number.getText().toString().equals(edit_password_again.getText().toString())) {
                    Toast.makeText(SetPasswordActivity.this, "两次密码输入不一致！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String passRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,18}$";
                    Pattern p = Pattern.compile(passRegex);
                    Matcher m = p.matcher(edit_password_number.getText().toString());
                    boolean b = m.matches();
                    if (!b) {
                        Toast.makeText(SetPasswordActivity.this, "请输入正确的格式！", Toast.LENGTH_SHORT).show();
                        return;
                    } else {



                        set_password();
                        Intent intent = new Intent(SetPasswordActivity.this, SelectRoleActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }

            }
        });

    }

    private void set_password() {
        Map<String, Object> params = new HashMap<>();
        if (null != SpUtils.getString(getApplicationContext(), AppConstants.USER_NAME)) {
            params.put("userNum", SpUtils.getString(getApplicationContext(), AppConstants.USER_NAME));

        }
        params.put("password", edit_password_again.getText().toString());
        Subscription subscription = Network.getInstance("设置密码", this)
                .set_password(params,
                        new ProgressSubscriber<>("设置密码", new SubscriberOnNextListener<Bean<String>>() {
                            @Override
                            public void onNext(Bean<String> result) {
                                sure_btn.loadingComplete();

                                if(null!=SpUtils.getString(getApplicationContext(),AppConstants.TEACHER_TYPE)){
                                    if(SpUtils.getString(getApplicationContext(),AppConstants.TEACHER_TYPE).length()>0){//已经审核过了
                                        Intent intent = new Intent(SetPasswordActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }else{//未审核
                                        Intent intent = new Intent(SetPasswordActivity.this, SelectRoleActivity.class);
                                        startActivity(intent);
                                    }
                                }

                            }

                            @Override
                            public void onError(String error) {
                                sure_btn.loadingComplete();


                            }
                        }, this, false));
    }
}
