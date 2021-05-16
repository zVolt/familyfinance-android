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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import io.github.zkhan93.familyfinance.util.FabHost;

public class HomeActivity extends AppCompatActivity implements AppBarConfiguration.OnNavigateUpListener, FabHost {

    public static String TAG = HomeActivity.class.getSimpleName();
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    public DrawerLayout drawerLayout;
    NavigationView navigationView;
    FloatingActionButton fab;

    TextView txtHeading;
    TextView txtSubheading;
    ImageView imgAvatar;
    ImageButton btnLogout, btnSwitchFamily;

    private String familyId;
    private View.OnClickListener headerActionListener;
    private AppBarConfiguration appBarConfiguration;

    private View.OnClickListener fabClickListener;
    private NavController navController;

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
            int viewId = view.getId();
            if (viewId == R.id.logout) {
                AuthUI.getInstance()
                        .signOut(HomeActivity.this)
                        .addOnCompleteListener(task -> {
                            // user is now signed out
                            startActivity(new Intent(getApplicationContext(),
                                    LoginActivity.class));
                            finish();
                        });
            } else if (viewId == R.id.switch_family) {
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().remove(getString(R.string.pref_family_id)).apply();
                startActivity(new Intent(HomeActivity.this, SelectFamilyActivity.class));
                finish();
            }
        };
        fabClickListener = view -> {
            if (navController.getCurrentDestination()==null)
                return;
            int activeNavItemId = navController.getCurrentDestination().getId();
            if (activeNavItemId == R.id.ccards) {
                DialogFragmentCcard.newInstance(familyId).show(getSupportFragmentManager
                        (), DialogFragmentCcard.TAG);
            } else if (activeNavItemId == R.id.dcards) {
                DialogFragmentDcard.newInstance(familyId).show(getSupportFragmentManager
                        (), DialogFragmentCcard.TAG);
            } else if (activeNavItemId == R.id.credentials) {
                DialogFragmentCredential.getInstance(null, familyId)
                        .show(getSupportFragmentManager(), DialogFragmentViewCard.TAG);
            } else if (activeNavItemId == R.id.members) {
                Intent intent = new Intent(getApplicationContext(), AddMemberActivity.class);
                intent.putExtra(getString(R.string.pref_family_id), familyId);
                startActivity(intent);
            } else if (activeNavItemId == R.id.accounts) {
                DialogFragmentAddAccount.newInstance(familyId).show
                        (getSupportFragmentManager(),
                                DialogFragmentAddAccount.TAG);
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
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        .setOpenableLayout(drawerLayout).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setUpViewRef() {
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        fab = findViewById(R.id.fab);

        View headerView = navigationView.getHeaderView(0);
        txtHeading = headerView.findViewById(R.id.heading);
        txtSubheading = headerView.findViewById(R.id.subheading);
        imgAvatar = headerView.findViewById(R.id.avatar);
        btnLogout = headerView.findViewById(R.id.logout);
        btnSwitchFamily = headerView.findViewById(R.id.switch_family);
        btnLogout.setOnClickListener(headerActionListener);
        btnSwitchFamily.setOnClickListener(headerActionListener);
        fab.setOnClickListener(fabClickListener);
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

    /**
     * implementing methods from @FabHost
     */
    @Override
    public void showFab() {
        if (fab != null)
            fab.show();
    }

    @Override
    public void hideFab() {
        if (fab != null)
            fab.hide();
    }
}
