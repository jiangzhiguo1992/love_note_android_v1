package com.jiangzg.ita.adapter;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.ita.R;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.utils.TimeUtils;
import com.jiangzg.ita.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 意见反馈适配器
 */
public class SuggestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private FragmentActivity activity;
    private List<Suggest> suggestList;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEAD = 1;
    private final int colorGrey;
    private final int colorPrimary;

    public SuggestAdapter(FragmentActivity activity) {
        this.activity = activity;
        suggestList = new ArrayList<>();
        colorGrey = ContextCompat.getColor(activity, R.color.icon_grey);
        int rId = ViewUtils.getColorPrimary(activity);
        colorPrimary = ContextCompat.getColor(activity, rId);
    }

    public void setData(List<Suggest> suggestList) {
        this.suggestList = suggestList;
        notifyDataSetChanged();
    }

    public void addData(List<Suggest> suggestList) {
        int insert = this.suggestList.size();
        this.suggestList.addAll(suggestList);
        notifyItemInserted(insert);
    }

    @Override
    public int getItemCount() {
        if (suggestList == null) {
            return 1;
        }
        return suggestList.size() + 1;
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
            View view = LayoutInflater.from(activity).inflate(R.layout.list_head_suggest, parent, false);
            return new HeadHolder(view);
        } else {
            View view = LayoutInflater.from(activity).inflate(R.layout.list_item_suggest, parent, false);
            return new ItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeadHolder) {
            // data
            //String desc = help.getDesc();
            //List<Help.Content> contentList = help.getContentList();
            //// view
            //HeadHolder headHolder = (HeadHolder) holder;
            //headHolder.tvDesc.setText(desc);
            //// list
            //headHolder.rv.setLayoutManager(new LinearLayoutManager(activity));
            //HelpContentAdapter adapter = new HelpContentAdapter(activity, contentList);
            //headHolder.rv.setAdapter(adapter);
        } else {
            ItemHolder itemHolder = (ItemHolder) holder;
            // data
            final Suggest suggest = suggestList.get(position - 1);
            String title = suggest.getTitle();
            long createdAt = suggest.getCreatedAt();
            String create = TimeUtils.getSuggestShowBySecond(createdAt);
            String createShow = String.format(activity.getString(R.string.create_at_holder), create);
            long updatedAt = suggest.getUpdatedAt();
            String update = TimeUtils.getSuggestShowBySecond(updatedAt);
            String updatedShow = String.format(activity.getString(R.string.update_at_holder), update);
            final int watchCount = suggest.getWatchCount();
            String watchShow;
            if (watchCount <= 0) {
                watchShow = activity.getString(R.string.follow);
            } else {
                watchShow = String.format("%d", watchCount);
            }
            int commentCount = suggest.getCommentCount();
            String commentShow;
            if (commentCount <= 0) {
                commentShow = activity.getString(R.string.comment);
            } else {
                commentShow = String.format("%d", commentCount);
            }
            final boolean watch = suggest.isWatch();
            boolean comment = suggest.isComment();
            // view
            itemHolder.tvTitle.setText(title);
            itemHolder.tvCreateAt.setText(createShow);
            itemHolder.tvUpdateAt.setText(updatedShow);
            itemHolder.tvWatch.setText(watchShow);
            itemHolder.tvComment.setText(commentShow);
            if (watch) {
                itemHolder.ivWatch.setImageResource(R.drawable.ic_visibility_on_primary);
            } else {
                itemHolder.ivWatch.setImageResource(R.drawable.ic_visibility_off_grey);
            }
            if (comment) {
                itemHolder.ivComment.setImageTintList(ColorStateList.valueOf(colorPrimary));
            } else {
                itemHolder.ivComment.setImageTintList(ColorStateList.valueOf(colorGrey));
            }
            // listener
            itemHolder.llTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // todo 详情页跳转
                }
            });
            itemHolder.llWatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean willWatch = !watch;
                    suggest.setWatch(willWatch);
                    int willWatchCount;
                    if (willWatch) {
                        willWatchCount = watchCount + 1;
                    } else {
                        willWatchCount = watchCount - 1;
                    }
                    if (willWatchCount <= 0) {
                        willWatchCount = 0;
                    }
                    suggest.setWatchCount(willWatchCount);
                    notifyItemChanged(position);
                    // todo api
                }
            });
            itemHolder.llComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // todo 详情页跳转 位置到评论
                }
            });
        }
    }

    // todo
    public class HeadHolder extends RecyclerView.ViewHolder {

        //TextView tvDesc;
        //RecyclerView rv;

        HeadHolder(View view) {
            super(view);
            //tvDesc = view.findViewById(R.id.tvDesc);
            //rv = view.findViewById(R.id.rv);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        CardView root;
        LinearLayout llTitle;
        TextView tvTitle;
        TextView tvCreateAt;
        TextView tvUpdateAt;
        LinearLayout llWatch;
        ImageView ivWatch;
        TextView tvWatch;
        LinearLayout llComment;
        ImageView ivComment;
        TextView tvComment;

        ItemHolder(View view) {
            super(view);
            root = view.findViewById(R.id.root);
            llTitle = view.findViewById(R.id.llTitle);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvCreateAt = view.findViewById(R.id.tvCreateAt);
            tvUpdateAt = view.findViewById(R.id.tvUpdateAt);
            llWatch = view.findViewById(R.id.llWatch);
            ivWatch = view.findViewById(R.id.ivWatch);
            tvWatch = view.findViewById(R.id.tvWatch);
            llComment = view.findViewById(R.id.llComment);
            ivComment = view.findViewById(R.id.ivComment);
            tvComment = view.findViewById(R.id.tvComment);
        }
    }

}
