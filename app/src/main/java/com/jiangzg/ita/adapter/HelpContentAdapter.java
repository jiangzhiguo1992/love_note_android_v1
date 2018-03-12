package com.jiangzg.ita.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiangzg.ita.R;
import com.jiangzg.ita.domain.Help;

import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 帮助文档一级问答
 */
public class HelpContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FragmentActivity activity;
    private List<Help.Content> contentList;

    public HelpContentAdapter(FragmentActivity activity, List<Help.Content> contentList) {
        this.activity = activity;
        this.contentList = contentList;
    }

    @Override
    public int getItemCount() {
        if (contentList == null) {
            return 0;
        }
        return contentList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_help_content, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Help.Content content = contentList.get(position);
        String answer = content.getAnswer();
        String question = content.getQuestion();
        ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.tvTop.setText(question);
        itemHolder.tvBottom.setText(answer);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        TextView tvTop;
        TextView tvBottom;

        ItemHolder(View view) {
            super(view);
            tvTop = view.findViewById(R.id.tvTop);
            tvBottom = view.findViewById(R.id.tvBottom);
        }
    }

}
