package io.github.zkhan93.familyfinance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.models.CCard;

public class CardDetailActivity extends AppCompatActivity {

    private String cardType;
    private CCard ccard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            cardType = bundle.getString("type");
    }
}
