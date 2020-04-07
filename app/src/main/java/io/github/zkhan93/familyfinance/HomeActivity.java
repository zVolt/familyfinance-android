package io.github.zkhan93.familyfinance;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements AppBarConfiguration.OnNavigateUpListener {

    public static String TAG = HomeActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.drawer_layout)
    public DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;


    TextView txtHeading;
    TextView txtSubheading;
    ImageView imgAvatar;
    ImageButton btnLogout, btnSwitchFamily;

    private String familyId;
    private View.OnClickListener headerActionListener;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        familyId =
                PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_family_id), null);
        initListeners();
        setUpViewRef();
        setSupportActionBar(toolbar);
        setUpNavigationDrawer();
        setUpHeaderContent();
    }

    private void initListeners() {
        headerActionListener = view -> {
            switch (view.getId()) {
                case R.id.logout:
                    AuthUI.getInstance()
                            .signOut(HomeActivity.this)
                            .addOnCompleteListener(task -> {
                                // user is now signed out
                                startActivity(new Intent(getApplicationContext(),
                                        LoginActivity.class));
                                finish();
                            });
                    break;
                case R.id.switch_family:
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().remove(getString(R.string.pref_family_id)).apply();
                    startActivity(new Intent(HomeActivity.this, SelectFamilyActivity.class));
                    finish();
                    break;
            }
        };
    }

    private void setUpHeaderContent() {
        txtSubheading.setText(familyId);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            txtHeading.setText(user.getDisplayName());
            Glide.with(getApplicationContext()).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(imgAvatar);
        }
    }

    private void setUpNavigationDrawer() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        .setOpenableLayout(drawerLayout).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setUpViewRef() {
        ButterKnife.bind(this);
        View headerView = navigationView.getHeaderView(0);
        txtHeading = headerView.findViewById(R.id.heading);
        txtSubheading = headerView.findViewById(R.id.subheading);
        imgAvatar = headerView.findViewById(R.id.avatar);
        btnLogout = headerView.findViewById(R.id.logout);
        btnSwitchFamily = headerView.findViewById(R.id.switch_family);
        btnLogout.setOnClickListener(headerActionListener);
        btnSwitchFamily.setOnClickListener(headerActionListener);
    }

    /**
     * setup toolbar hamburger menu to open the navigation drawer
     *
     * @return boolean
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
