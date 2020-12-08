package com.jinsihou.tldr.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jinsihou.tldr.R;
import com.jinsihou.tldr.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.appbarLayout.setExpanded(false);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(
                binding.collapsingToolbarLayout,
                binding.toolbar,
                navController,
                appBarConfiguration);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public FloatingActionButton getFAB() {
        return binding.fab;
    }

    public void setText(int titleId) {
        binding.collapsingToolbarLayout.setTitle(getString(titleId));
    }

    public void setText(String title) {
        binding.collapsingToolbarLayout.setTitle(title);
    }

    public void setExpanded(boolean expanded) {
        binding.appbarLayout.setExpanded(expanded);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }
}