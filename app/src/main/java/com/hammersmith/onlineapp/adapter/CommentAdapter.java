package com.hammersmith.onlineapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hammersmith.onlineapp.ActivityComment;
import com.hammersmith.onlineapp.R;
import com.hammersmith.onlineapp.model.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thuon on 6/30/2016.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private ClickListener clickListener;
    Context context;
    Comment comment;
    ArrayList<Comment> comments = new ArrayList<>();

    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.custom_comment, parent, false);
        CommentViewHolder commentViewHolder = new CommentViewHolder(root);

        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        comment = comments.get(position);
        holder.txt_comment.setText(comment.getComment());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txt_comment;
        public CommentViewHolder(View itemView) {
            super(itemView);
            txt_comment = (TextView) itemView.findViewById(R.id.txt_comment);
//            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            if (clickListener != null) {
//                clickListener.itemClicked(v, getLayoutPosition());
//            }
//            if (v.getId() == txt_reply.getId()){
//                l_reply.setVisibility(View.VISIBLE);
//            }else{
//                Toast.makeText(v.getContext(),"hello",Toast.LENGTH_SHORT).show();
//            }
        }
    }

    public interface ClickListener {
        public void itemClicked(View view, int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
