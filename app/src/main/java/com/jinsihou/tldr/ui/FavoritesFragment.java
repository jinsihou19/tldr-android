package com.jinsihou.tldr.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jinsihou.tldr.R;
import com.jinsihou.tldr.viewmodels.CommandViewModel;

/**
 * A fragment representing a list of Items.
 */
public class FavoritesFragment extends Fragment {
    private CommandViewModel model;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FavoritesFragment() {
    }

    FavoritesAdapter adapter = new FavoritesAdapter();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setText(R.string.favorites_activity_title);
        mainActivity.setExpanded(true);
        mainActivity.getFAB().setVisibility(View.INVISIBLE);


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }
        adapter.setCallBack(index -> {
            model.queryCommand(index);
            NavHostFragment
                    .findNavController(FavoritesFragment.this)
                    .navigate(R.id.action_favoritesFragment_to_SecondFragment);
            return null;
        });
        model = new ViewModelProvider(requireActivity()).get(CommandViewModel.class);
        model.getFavorites().observe(getViewLifecycleOwner(), favorites -> {
            adapter.setFavorites(favorites);
            adapter.notifyDataSetChanged();
        });
        return view;
    }
}