package com.jinsihou.tldr.viewmodels;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jinsihou.tldr.BasicApplication;
import com.jinsihou.tldr.data.Command;
import com.jinsihou.tldr.repository.CommandRepository;

import java.util.List;
import java.util.Set;


public class CommandViewModel extends AndroidViewModel {
    private final CommandRepository commandRepository;
    private LiveData<Command> mLiveCommand;
    private LiveData<Boolean> like;

    public CommandViewModel(@NonNull Application application) {
        super(application);
        commandRepository = ((BasicApplication) application).getDataRepository();
    }

    public void queryCommand(Command.Index index) {
        mLiveCommand = commandRepository.queryCommandByIndex(index);
    }

    public void queryCommandByName(String name) {
        mLiveCommand = commandRepository.queryCommandByName(name);
    }

    public LiveData<Command> getCommand() {
        return mLiveCommand;
    }

    public LiveData<List<Command.Index>> getCommandIndex(Set<String> platformFilters) {
        return commandRepository.queryCommandIndex(platformFilters);
    }

    public void queryLike(Command.Index index) {
        like = commandRepository.isLike(index);
    }

    public void like(Command.Index index) {
        commandRepository.like(index);
    }

    public void unlike(Command.Index index) {
        commandRepository.unlike(index);
    }

    public LiveData<Boolean> isLike() {
        return like;
    }

    public LiveData<List<Command.Index>> getFavorites(){
        return commandRepository.getFavorites();
    }

    public LiveData<Boolean> isLoad(){
        return commandRepository.isLoad();
    }

}
