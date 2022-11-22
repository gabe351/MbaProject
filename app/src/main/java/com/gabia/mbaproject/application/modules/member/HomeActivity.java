package com.gabia.mbaproject.application.modules.member;

import static com.gabia.mbaproject.utils.StringUtils.capitalizeFirstLetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gabia.mbaproject.R;
import com.gabia.mbaproject.application.App;
import com.gabia.mbaproject.application.modules.login.LoginActivity;
import com.gabia.mbaproject.application.modules.member.editdata.EditPasswordActivity;
import com.gabia.mbaproject.application.modules.member.payment.MemberPaymentListFragment;
import com.gabia.mbaproject.application.modules.member.rollcall.RollCallFragment;
import com.gabia.mbaproject.databinding.ActivityHomeBinding;
import com.gabia.mbaproject.infrastructure.local.UserDefaults;
import com.gabia.mbaproject.model.User;
import com.gabia.mbaproject.model.enums.Situation;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.setActivity(this);
        currentUser = UserDefaults.getInstance(getApplicationContext()).getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Sem usuário na sessão", Toast.LENGTH_SHORT).show();
            App.logout(this);
        } else {
            bindUser();
            setupBottomTabs();
        }
    }

    private void bindUser() {
        String instrumentText = "Ala: " + capitalizeFirstLetter(currentUser.getInstrument());
        String associatedText = currentUser.getAssociated() ? "Sócio" : "Membro";
        Situation userSituation = Situation.valueOf(currentUser.getSituation());

        binding.memberNameText.setText(currentUser.getName());
        binding.instrumentText.setText(instrumentText);
        binding.associatedText.setText(associatedText);
        binding.memberSituationTag.setBackgroundTintList(getResources().getColorStateList(userSituation.getSituationColor()));
        binding.memberSituationTag.setText(userSituation.getFormattedValue());
    }

    public void editMemberDidPress(View view) {
        Intent intent = new Intent(getApplicationContext(), EditPasswordActivity.class);
        startActivity(intent);
    }

    public void logoutDidPress(View view) {
        new AlertDialog.Builder(this, R.style.DeleteDialogTheme)
                .setTitle("Sair")
                .setMessage("Tem certeza que deseja sair do app?")

                .setPositiveButton("Sair", (dialog, which) -> {
                    App.logout(this);
                })
                .setNegativeButton("Ficar", null)
                .show();
    }

    private void setupBottomTabs() {
        replaceFragment(MemberPaymentListFragment.newInstance(currentUser.getId()));

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.paymentTab:
                    replaceFragment(MemberPaymentListFragment.newInstance(currentUser.getId()));
                    break;
                case R.id.rollCallTab:
                    replaceFragment(RollCallFragment.newInstance(currentUser.getId()));
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeContentFrame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {}
}