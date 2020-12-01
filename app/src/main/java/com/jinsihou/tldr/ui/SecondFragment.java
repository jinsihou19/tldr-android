package com.jinsihou.tldr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
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


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setExpanded(true);
        CommandViewModel model = new ViewModelProvider(mainActivity).get(CommandViewModel.class);
        LifecycleOwner owner = getViewLifecycleOwner();
        LiveData<Command> command = model.getCommand();
        if (command == null) {
            // 无法获取数据就返回查询页面
            NavHostFragment
                    .findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        } else {
            command.observe(owner, cmd -> {
                Command.Index index = cmd.getIndex();
                mainActivity.setText(cmd.getName());
                renderMarkdown(cmd);
                model.queryLike(index);
                mainActivity.getFAB().setOnClickListener(v -> {
                    LiveData<Boolean> like = model.isLike();
                    if (like.getValue() == null || !like.getValue()) {
                        model.like(index);
                    } else {
                        model.unlike(index);
                    }
                });
                model.isLike().observe(owner, isLike -> {
                    if (isLike) {
                        mainActivity.getFAB().setImageResource(R.drawable.ic_favorite_full);
                    } else {
                        mainActivity.getFAB().setImageResource(R.drawable.ic_favorite_border);
                    }
                });
            });
        }
    }

    private void renderMarkdown(Command cmd) {
        String body;
        if (Command.EMPTY.equals(cmd)) {
            ((MainActivity) requireActivity()).getFAB().setVisibility(View.INVISIBLE);
            String tpl = getString(R.string.html_template_not_available);
            body = String.format(tpl, getStyle());
        } else {
            String markdown = Processor.process(cmd.getText())
                    .replace("{{", "<span class=\"parameter\">")
                    .replace("}}", "</span>");
            String tpl = getString(R.string.html_template);
            body = String.format(tpl, markdown, getStyle());
        }

        binding.webview.loadDataWithBaseURL(Constants.ASSETS_BASE_URL, body,
                Constants.MIME_TYPE_HTML, StandardCharsets.UTF_8.toString(), null);
    }

    private String getStyle() {
        return UIUtils.isDarkTheme(requireContext()) ? "dark_clearness.css" : "light_clearness.css";
    }
}