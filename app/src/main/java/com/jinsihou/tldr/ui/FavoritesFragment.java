package com.jinsihou.tldr.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jinsihou.tldr.R;
import com.jinsihou.tldr.viewmodels.CommandViewModel;

/**
 * 收藏夹，使用列表展示收藏的命令
 */
public class FavoritesFragment extends Fragment {
    private CommandViewModel model;
    private MainActivity mainActivity;
    private NavController navController;

    FavoritesAdapter adapter = new FavoritesAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        navController = NavHostFragment.findNavController(FavoritesFragment.this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter.setCallBack(index -> {
            model.queryCommand(index);
            navController.navigate(R.id.action_favoritesFragment_to_SecondFragment);
            return null;
        });
        model = new ViewModelProvider(requireActivity()).get(CommandViewModel.class);
        model.getFavorites().observe(getViewLifecycleOwner(), favorites -> {
            adapter.setFavorites(favorites);
            adapter.notifyDataSetChanged();
        });
        navController.addOnDestinationChangedListener((controller, destination, arguments)
                -> mainActivity.setExpanded(destination.getId() == R.id.favoritesFragment));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}