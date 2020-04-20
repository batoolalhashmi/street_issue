package com.barmej.streetissues;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class IssueDetailsActivity extends AppCompatActivity {
    public static final String ISSUE_DATA = "issue_data";

    private ImageView mIssueImageView;
    private TextView mDescriptionTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIssueImageView = findViewById(R.id.image_view_issue_photo);
        mDescriptionTextView = findViewById(R.id.issue_description);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Issue issue = getIntent().getExtras().getParcelable(ISSUE_DATA);
            if (issue != null) {
                getSupportActionBar().setTitle(issue.getTitle());
                mDescriptionTextView.setText(issue.getDescription());
                Glide.with(this).load(issue.getPhoto()).into(mIssueImageView);
            }
        }
    }
}
