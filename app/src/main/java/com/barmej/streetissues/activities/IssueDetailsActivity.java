package com.barmej.streetissues.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.barmej.entity.Issue;
import com.barmej.streetissues.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class IssueDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String ISSUE_DATA = "issue_data";

    private ImageView mIssueImageView;
    private TextView mDescriptionTextView;
    private Issue issue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIssueImageView = findViewById(R.id.image_view_issue_photo);
        mDescriptionTextView = findViewById(R.id.issue_description);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);


        if (getIntent() != null && getIntent().getExtras() != null) {
            issue = getIntent().getExtras().getParcelable(ISSUE_DATA);
            if (issue != null) {
                getSupportActionBar().setTitle(issue.getTitle());
                mDescriptionTextView.setText(issue.getDescription());
                Glide.with(this).load(issue.getPhoto()).into(mIssueImageView);

            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        LatLng latLng = new LatLng(issue.getLocation().getLatitude(), issue.getLocation().getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
}