package com.gabia.mbaproject.application.modules.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;

import com.gabia.mbaproject.R;
import com.gabia.mbaproject.application.modules.admin.dashboard.AdminDashboardActivity;
import com.gabia.mbaproject.application.modules.member.HomeActivity;
import com.gabia.mbaproject.databinding.ActivityLoginBinding;
import com.gabia.mbaproject.infrastructure.local.UserDefaults;
import com.gabia.mbaproject.infrastructure.remote.api.AuthApiDataSource;
import com.gabia.mbaproject.infrastructure.remote.providers.ApiDataSourceProvider;
import com.gabia.mbaproject.infrastructure.remote.remotedatasource.AuthRemoteDataSource;
import com.gabia.mbaproject.infrastructure.utils.BaseCallBack;
import com.gabia.mbaproject.model.AuthRequest;
import com.gabia.mbaproject.model.User;
import com.gabia.mbaproject.model.enums.UserLevel;
import com.google.gson.Gson;

import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setActivity(this);
        binding.setIsLoading(false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public void loginDidPress(View view) {
        if (binding.inputEmail.getText().toString().isEmpty() || binding.inputPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show();
        } else {
            executeLogin();
        }
    }

    private void executeLogin() {
        String email = binding.inputEmail.getText().toString();
        String password = binding.inputPassword.getText().toString();

        AuthRemoteDataSource remoteDataSource = new AuthRemoteDataSource(ApiDataSourceProvider.Companion.getAuthApiDataSource());
        binding.setIsLoading(true);
        remoteDataSource.login(new AuthRequest(email, password), new BaseCallBack<User>() {
             @Override
             public void onSuccess(User result) {
                 new UserDefaults(getApplicationContext()).save(result);

                 boolean isAdmin = result.getAdminLevel() >= UserLevel.ROLE_ADMIN.getValue();;
                 Intent intent = isAdmin ?
                         new Intent(getApplicationContext(), AdminDashboardActivity.class) :
                         new Intent(getApplicationContext(), HomeActivity.class);

                 startActivity(intent);
                 binding.setIsLoading(false);
             }

            @Override
             public void onError(int code) {
                 binding.setIsLoading(false);
                 runOnUiThread(() -> {
                     showError(code);
                 });
             }
         });
    }

    private void showError(int code) {
        String message = "falha ao entrar - Codigo" + code;
        if (code == 401) {
            message = "Login ou senha incorretos";
        }
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
