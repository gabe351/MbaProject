package com.gabia.mbaproject.application.modules.admin.finance;

import static com.gabia.mbaproject.utils.DateUtils.monthAndYear;
import static com.gabia.mbaproject.utils.FloatUtils.moneyFormat;
import static com.gabia.mbaproject.utils.StringUtils.capitalizeFirstLetter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gabia.mbaproject.R;
import com.gabia.mbaproject.application.ActionsListener;
import com.gabia.mbaproject.application.SelectListener;
import com.gabia.mbaproject.application.modules.admin.finance.payment.PaymentAdapter;
import com.gabia.mbaproject.application.modules.admin.finance.payment.PaymentFormActivity;
import com.gabia.mbaproject.application.modules.member.payment.MemberPaymentListViewModel;
import com.gabia.mbaproject.databinding.ActivityMemberDetailBinding;
import com.gabia.mbaproject.model.Member;
import com.gabia.mbaproject.model.Payment;
import com.gabia.mbaproject.model.PaymentResponse;
import com.gabia.mbaproject.model.enums.Situation;
import com.gabia.mbaproject.utils.DateUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MemberDetailActivity extends AppCompatActivity implements ActionsListener<PaymentResponse> {

    public static final String MEMBER_KEY = "com.gabia.mbaproject.application.modules.admin.finance.MEMBER_KEY";

    private ActivityMemberDetailBinding binding;
    private PaymentAdapter adapter;
    private Member currentMember;

    public static Intent createIntent(Context context, Member member) {
        Intent intent = new Intent(context, MemberDetailActivity.class);
        intent.putExtra(MEMBER_KEY, member);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_member_detail);
        binding.setActivity(this);
        binding.setIsLoading(true);
        adapter = new PaymentAdapter(this);
        binding.paymentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.paymentsRecyclerView.setAdapter(adapter);

        currentMember = (Member) getIntent().getSerializableExtra(MEMBER_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentMember != null) {
            fetchPayments(currentMember.getId());
            bind();
        } else {
            Toast.makeText(this, "Falha ao carregar membro", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    @Override
    public void edit(PaymentResponse item) {
        startActivity(PaymentFormActivity.createIntent(this, item, currentMember.getId()));
    }

    @Override
    public void delete(PaymentResponse item) {
        String relativeDate = DateUtils.changeFromIso(monthAndYear, item.getDate());
        String message = "Tem certeza que deseja deletar o pagamento \n\n" +
                "Mês de referência: " + relativeDate + "\n" +
                "Valor: " + moneyFormat(item.getPaymentValue()) + "\n";
        new AlertDialog.Builder(this, R.style.DeleteDialogTheme)
                .setIcon(R.drawable.ic_delete_red)
                .setTitle("Deletar pagamento")
                .setMessage(message)
                .setPositiveButton("Deletar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MemberDetailActivity.this, "Deletado", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void editMemberDidPress(View view) {
        Toast.makeText(this, "VOU FAZER A TELA DE EDITAR USER", Toast.LENGTH_SHORT).show();
    }

    public void addPaymentDidPress(View view) {
        startActivity(PaymentFormActivity.createIntent(this, null, currentMember.getId()));
    }

    private void bind() {
        String instrumentText = "Ala: " + capitalizeFirstLetter(currentMember.getInstrument());
        String associatedText = currentMember.getAssociated() ? "Sócio" : "Membro";
        Situation userSituation = Situation.valueOf(currentMember.getSituation());

        binding.memberNameText.setText(currentMember.getName());
        binding.instrumentText.setText(instrumentText);
        binding.associatedText.setText(associatedText);
        binding.memberSituationTag.setBackgroundTintList(getResources().getColorStateList(userSituation.getSituationColor()));
        binding.memberSituationTag.setText(userSituation.getFormattedValue());
    }

    private void fetchPayments(int memberId) {
        MemberPaymentListViewModel viewModel = new ViewModelProvider(this).get(MemberPaymentListViewModel.class);
        viewModel.getPaymentListLiveData().observe(this, paymentList -> {
            if (paymentList != null) {
                orderByDate(paymentList);
                adapter.setPaymentList(paymentList);
            } else {
                Toast.makeText(this, "Erro ao carregar os pagamentos", Toast.LENGTH_SHORT).show();
            }
            binding.setIsLoading(false);
        });
        viewModel.fetchPayments(memberId, code -> {
            runOnUiThread(() -> Toast.makeText(MemberDetailActivity.this, "Erro ao carregar os pagamentos code: " + code , Toast.LENGTH_SHORT).show());
            binding.setIsLoading(false);
            return null;
        });
    }

    private void orderByDate(List<PaymentResponse> paymentList) {
        paymentList.sort((o1, o2) -> {
            Date firstDate = DateUtils.toDate(DateUtils.isoDateFormat, o1.getDate());
            Date secondDate = DateUtils.toDate(DateUtils.isoDateFormat, o2.getDate());
            return secondDate.compareTo(firstDate);
        });
    }
}
