package com.jinsihou.tldr.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.jinsihou.tldr.R;
import com.jinsihou.tldr.data.Command;
import com.jinsihou.tldr.databinding.FragmentFirstBinding;
import com.jinsihou.tldr.viewmodels.CommandViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FirstFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private FragmentFirstBinding binding;
    private CommandViewModel model;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setText(R.string.app_name);
        mainActivity.setExpanded(false);

        model = new ViewModelProvider(requireActivity()).get(CommandViewModel.class);
        setupIndex(getPlatformFilter(sharedPreferences));
        binding.search.setThreshold(1);
        binding.search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                if (binding.search.getText().length() == 0) {
                    binding.search.setError(getString(R.string.search_empty_keyword));
                } else {
                    model.queryCommandByName(binding.search.getText().toString());
                    gotoContent();
                }
            }
            return true;
        });
        binding.search.setOnItemClickListener((parent, itemView, position, id) -> {
            TextView nameView = itemView.findViewById(R.id.cmd_name);
            TextView platformView = itemView.findViewById(R.id.cmd_platform);
            binding.search.setText(nameView.getText().toString());
            Command.Index index = new Command.Index(nameView.getText().toString(), platformView.getText().toString());
            model.queryCommand(index);
            gotoContent();
        });
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private Set<String> getPlatformFilter(SharedPreferences sharedPreferences) {
        Set<String> platforms = new HashSet<>();
        platforms.add("common");
        platforms.add("linux");
        platforms.add("osx");
        platforms.add("sunox");
        platforms.add("windows");
        return sharedPreferences.getStringSet("platform", platforms);
    }

    private final Observer<List<Command.Index>> indexLister = indexList -> binding.search.setAdapter(
            new SimpleAdapter(
                    requireContext(),
                    indexList.stream()
                            .map(Command.Index::toMap)
                            .collect(Collectors.toList()),
                    R.layout.item,
                    Command.COLUMNS_INDEX,
                    new int[]{R.id.cmd_name, R.id.cmd_platform}
            ));

    private void setupIndex(Set<String> platformFilters) {
        LiveData<List<Command.Index>> commandIndex = model.getCommandIndex(platformFilters);
        commandIndex.removeObserver(indexLister);
        commandIndex.observe(getViewLifecycleOwner(), indexLister);
    }

    private void gotoContent() {
        NavHostFragment
                .findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment);
        closeSoftKeyboard();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        binding = null;
    }

    private void closeSoftKeyboard() {
        InputMethodManager manager = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(binding.search.getWindowToken(), 0);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("platform".equals(key)) {
            Set<String> platformFilter = getPlatformFilter(sharedPreferences);
            platformFilter.add("common");
            setupIndex(platformFilter);
        }
    }
}