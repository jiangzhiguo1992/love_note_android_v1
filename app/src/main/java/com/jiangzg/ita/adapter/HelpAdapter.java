package com.jiangzg.ita.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.domain.Help;

import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 帮助列表适配器
 */
public class HelpAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private FragmentActivity activity;
    private Help help;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEAD = 1;

    public HelpAdapter(FragmentActivity activity) {
        this.activity = activity;
        help = new Help();
    }

    public void setData(Help help) {
        this.help = help;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        List<Help> subList = help.getSubList();
        if (subList == null) {
            return 1;
        }
        return subList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            View view = LayoutInflater.from(activity).inflate(R.layout.list_head_help, parent, false);
            return new HeadHolder(view);
        } else {
            View view = LayoutInflater.from(activity).inflate(R.layout.list_item_help, parent, false);
            return new ItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeadHolder) {
            // data
            String desc = help.getDesc();
            List<Help.Content> contentList = help.getContentList();
            // view
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.tvDesc.setText(desc);
            // list
            headHolder.rv.setLayoutManager(new LinearLayoutManager(activity));
            HelpContentAdapter adapter = new HelpContentAdapter(activity, contentList);
            headHolder.rv.setAdapter(adapter);
        } else {
            ItemHolder itemHolder = (ItemHolder) holder;
            List<Help> subList = help.getSubList();
            Help help = subList.get(position - 1);
            itemHolder.tvSubTitle.setText(help.getTitle());
            itemHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HelpActivity.goActivity(activity);
                }
            });
        }
    }

    public class HeadHolder extends RecyclerView.ViewHolder {

        TextView tvDesc;
        RecyclerView rv;

        HeadHolder(View view) {
            super(view);
            tvDesc = view.findViewById(R.id.tvDesc);
            rv = view.findViewById(R.id.rv);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        CardView root;
        TextView tvSubTitle;

        ItemHolder(View view) {
            super(view);
            root = view.findViewById(R.id.root);
            tvSubTitle = view.findViewById(R.id.tvSubTitle);
        }
    }

}
