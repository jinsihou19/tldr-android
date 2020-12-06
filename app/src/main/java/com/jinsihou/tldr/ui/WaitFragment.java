package com.jinsihou.tldr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.jinsihou.tldr.R;
import com.jinsihou.tldr.viewmodels.CommandViewModel;


public class WaitFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wait, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CommandViewModel model = new ViewModelProvider(requireActivity()).get(CommandViewModel.class);
        model.isLoad().observe(getViewLifecycleOwner(), isLoad -> {
            if (!isLoad) {
                NavHostFragment
                        .findNavController(WaitFragment.this)
                        .navigate(R.id.action_waitFragment_to_SecondFragment);
            }
        });
    }
}