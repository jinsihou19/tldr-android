package com.jinsihou.tldr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.github.rjeschke.txtmark.Processor;
import com.jinsihou.tldr.R;
import com.jinsihou.tldr.data.Command;
import com.jinsihou.tldr.databinding.FragmentSecondBinding;
import com.jinsihou.tldr.util.Constants;
import com.jinsihou.tldr.util.UIUtils;
import com.jinsihou.tldr.viewmodels.CommandViewModel;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;


public class SecondFragment extends Fragment {
    private FragmentSecondBinding binding;
    private NavController nav;
    private CommandViewModel model;
    private MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nav = NavHostFragment.findNavController(this);
        mainActivity = (MainActivity) requireActivity();
        model = new ViewModelProvider(requireActivity()).get(CommandViewModel.class);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        nav.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.SecondFragment) {
                mainActivity.setExpanded(true);
            } else {
                mainActivity.setExpanded(false);
                mainActivity.getFAB().setVisibility(View.GONE);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LifecycleOwner owner = getViewLifecycleOwner();
        Observer<Command> cmdObserver = cmd -> {
            if (Command.EMPTY.equals(cmd)) {
                Observer<Boolean> observer = isLoad -> {
                    if (isLoad) {
                        nav.navigate(R.id.action_SecondFragment_to_waitFragment);
                    }
                };
                model.isLoad().removeObserver(observer);
                model.isLoad().observe(owner, observer);
                dealWithEmpty();
            } else {
                createContent(cmd);
            }
        };
        model.getCommand().removeObserver(cmdObserver);
        model.getCommand().observe(owner, cmdObserver);
    }

    private void createContent(Command cmd) {
        dealWithCMD(cmd);
        Command.Index index = cmd.getIndex();
        mainActivity.setText(index.name);
        mainActivity.getFAB().setVisibility(View.VISIBLE);
        model.queryLike(index);
        mainActivity.getFAB().setOnClickListener(v -> {
            LiveData<Boolean> like = model.isLike();
            if (like.getValue() == null || !like.getValue()) {
                model.like(index);
            } else {
                model.unlike(index);
            }
        });
        model.isLike().observe(getViewLifecycleOwner(), isLike -> {
            if (isLike) {
                mainActivity.getFAB().setImageResource(R.drawable.ic_favorite_full);
            } else {
                mainActivity.getFAB().setImageResource(R.drawable.ic_favorite_border);
            }
        });
    }

    private void dealWithEmpty() {
        String tpl = getString(R.string.html_template_not_available);
        String body = String.format(tpl, getStyle());
        renderMarkdown(body);
    }

    private void dealWithCMD(Command cmd) {
        String markdown = Processor.process(cmd.getText())
                .replace("{{", "<span class=\"parameter\">")
                .replace("}}", "</span>");
        String tpl = getString(R.string.html_template);
        String body = String.format(tpl, markdown, getStyle());
        renderMarkdown(body);
    }

    private void renderMarkdown(String body) {
        binding.webview.loadDataWithBaseURL(Constants.ASSETS_BASE_URL, body,
                Constants.MIME_TYPE_HTML, StandardCharsets.UTF_8.toString(), null);
    }

    private String getStyle() {
        return UIUtils.isDarkTheme(requireContext()) ? "dark_clearness.css" : "light_clearness.css";
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}