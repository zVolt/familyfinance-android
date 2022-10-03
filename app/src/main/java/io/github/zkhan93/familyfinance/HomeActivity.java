package io.github.zkhan93.familyfinance;

import static io.github.zkhan93.familyfinance.FragmentMembers.PERMISSION_REQUEST_CODE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.github.zkhan93.familyfinance.vm.AppState;

public class HomeActivity extends AppCompatActivity implements AppBarConfiguration.OnNavigateUpListener {

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
    private AppState appState;
    private SharedPreferences spf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appState = new ViewModelProvider(this).get(AppState.class);
        setContentView(R.layout.activity_home);
        spf = PreferenceManager.getDefaultSharedPreferences(this);
        familyId = spf.getString(getString(R.string.pref_family_id), null);
        initListeners();
        setUpViewRef();
        setSupportActionBar(toolbar);
        setUpNavigationDrawer();
        setUpHeaderContent();
        appState.getFabIcon().observe(this, icon -> fab.setImageResource(icon));
        appState.getFabShow().observe(this, show -> {
            if (show)
                fab.show();
            else
                fab.hide();
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        checkRequiredPermissions();
        ((App)getApplication()).requestAuth(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((App)getApplication()).requestAuth(this);
    }

    private void checkRequiredPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest
                .permission.RECEIVE_SMS) & ContextCompat.checkSelfPermission(this, android.Manifest
                .permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest
                    .permission.RECEIVE_SMS)) {
                //explain the need of this permission
                //TODO: show a dialog and then on positive show request permission
                Log.d(TAG, "lol we need it :D");
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission
                        .READ_PHONE_STATE
                }, PERMISSION_REQUEST_CODE);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission
                        .READ_PHONE_STATE
                }, PERMISSION_REQUEST_CODE);
            }
        }
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
            // send click event back to registered listeners (Fragments)
            appState.onFabAction();
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
        navController = ((NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)).getNavController();
        appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        .setOpenableLayout(drawerLayout).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            AppState appState = new ViewModelProvider(HomeActivity.this).get(AppState.class);
            appState.disableFab();
        });
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

}
