package com.gabia.mbaproject.application.adminmodules.finance;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gabia.mbaproject.R;
import com.gabia.mbaproject.databinding.ActivityMemberDetailBinding;
import com.gabia.mbaproject.model.Payment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MemberDetailActivity extends AppCompatActivity {

    private ActivityMemberDetailBinding binding;
    private PaymentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_member_detail);
        binding.setActivity(this);
        adapter = new PaymentAdapter();
        binding.paymentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.paymentsRecyclerView.setAdapter(adapter);

        fetchPayments();
    }

    public void editMemberDidPress(View view) {
        Toast.makeText(this, "VOU CRIAR A TELA DE ADICIONAR USUARIO", Toast.LENGTH_SHORT).show();
    }

    public void addPaymentDidPress(View view) {
        Toast.makeText(this, "VOU CRIAR A TELA DE ADICIONAR PAGAMENTO", Toast.LENGTH_SHORT).show();
    }

    private void fetchPayments() {
        List<Payment> paymentList = Arrays.asList(
                new Payment(15.0f, "pagou metade esse mês", new Date()),
                new Payment(30.50f, "pagou Inteira", new Date()),
                new Payment(30.50f, "pagou Inteira", new Date()),
                new Payment(30.50f, "pagou Inteira", new Date()),
                new Payment(30.50f, "pagou Inteira", new Date()),
                new Payment(15.0f, "pagou metade esse mês", new Date()),
                new Payment(30.50f, "pagou Inteira", new Date())
        );

        adapter.setPaymentList(paymentList);
    }
}
