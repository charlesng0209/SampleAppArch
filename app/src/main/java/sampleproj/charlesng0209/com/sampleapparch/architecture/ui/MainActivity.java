package sampleproj.charlesng0209.com.sampleapparch.architecture.ui;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.TextView;

import sampleproj.charlesng0209.com.sampleapparch.R;
import sampleproj.charlesng0209.com.sampleapparch.architecture.viewmodel.UserProfileViewModel;

public class MainActivity extends LifecycleActivity {

    private TextView mTextMessage;
    private UserProfileViewModel userProfileViewModel;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        userProfileViewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
        userProfileViewModel.init("Test");
        userProfileViewModel.getUser().observe(this, user -> mTextMessage.setText(user.getName()));

    }
}
