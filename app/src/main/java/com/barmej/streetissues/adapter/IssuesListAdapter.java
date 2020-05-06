package com.barmej.streetissues.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barmej.entity.Issue;
import com.barmej.streetissues.R;
import com.bumptech.glide.Glide;
import java.util.List;

public class IssuesListAdapter extends RecyclerView.Adapter<IssuesListAdapter.IssuesViewHolder> {

    public interface onIssueClickListener {
        void onIssueClick(Issue issue);
    }

    private List<Issue> mIssuesList;
    private onIssueClickListener mOnIssueClickListener;

    public IssuesListAdapter(List<Issue> issuesList, onIssueClickListener onIssueClickListener) {
        this.mIssuesList = issuesList;
        this.mOnIssueClickListener = onIssueClickListener;
    }

    @NonNull
    @Override
    public IssuesListAdapter.IssuesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue, parent, false);
        return new IssuesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssuesListAdapter.IssuesViewHolder holder, int position) {
        holder.bind(mIssuesList.get(position));
    }

    @Override
    public int getItemCount() {
        return mIssuesList.size();
    }

    public class IssuesViewHolder extends RecyclerView.ViewHolder {
        TextView issueTitleTextView;
        ImageView issuePhotoImageView;
        Issue issue;

        public IssuesViewHolder(@NonNull View itemView) {
            super(itemView);
            issueTitleTextView = itemView.findViewById(R.id.title_taxt_view);
            issuePhotoImageView = itemView.findViewById(R.id.photo_image_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnIssueClickListener.onIssueClick(issue);
                }
            });
        }

        public void bind(Issue issue) {
            this.issue = issue;
            issueTitleTextView.setText(issue.getTitle());
            Glide.with(issuePhotoImageView).load(issue.getPhoto()).into(issuePhotoImageView);
        }
    }
}
