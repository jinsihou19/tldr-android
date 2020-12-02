package com.jinsihou.tldr.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jinsihou.tldr.R;
import com.jinsihou.tldr.data.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Command}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<Command.Index> favorites = new ArrayList<>();
    private Function<Command.Index, Void> callBack;

    public FavoritesAdapter() {

    }

    public void setFavorites(List<Command.Index> favorites) {
        this.favorites = favorites;
    }

    public void setCallBack(Function<Command.Index, Void> callBack) {
        this.callBack = callBack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view, callBack);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = favorites.get(position);
        holder.name.setText(favorites.get(position).name);
        holder.platform.setText(favorites.get(position).platform);
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView name;
        public final TextView platform;
        public Command.Index mItem;

        public ViewHolder(View view, Function<Command.Index, Void> callBack) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.item_number);
            platform = view.findViewById(R.id.content);
            mView.setOnClickListener(v -> {
                if (callBack != null) {
                    callBack.apply(getIndex());
                }
            });
        }

        public Command.Index getIndex() {
            return new Command.Index(name.getText().toString(), platform.getText().toString());
        }

        @Override
        public String toString() {
            return super.toString() + " '" + platform.getText() + "'";
        }
    }
}