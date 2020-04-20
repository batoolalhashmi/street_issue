package com.barmej.streetissues;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddNewIssueActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = AddNewIssueActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_ACCESS_LOCATION = 1;
    private static final int PERMISSION_REQUEST_READ_STORAGE = 2;
    private static final int REQUEST_GET_PHOTO = 3;
    private static final LatLng DEFAULT_LOCATION = new LatLng(24.412582, 54.484793);
    private boolean mLocationPermissionGranted;
    private boolean mReadStoragePermissionGranted;
    private Uri mIssuePhotoUri;
    private FusedLocationProviderClient mLocationProviderClient;
    private Location mLastKnownLocation;
    private LatLng mSelectedLatlng;
    private ConstraintLayout mConstraintLayout;
    private EditText mIssueTitleEditText;
    private EditText mIssueDescriptionEditTextView;
    private ImageView mIssuePhotoImageView;
    private Button mAddIssueButton;
    private GoogleMap mGoogleMap;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_issue);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);

        mConstraintLayout = findViewById(R.id.add_issue_constraint_layout);
        mIssuePhotoImageView = findViewById(R.id.issue_photo_image_view);
        mAddIssueButton = findViewById(R.id.file_issue);
        mIssueTitleEditText = findViewById(R.id.issue_title);
        mIssueDescriptionEditTextView = findViewById(R.id.text_issue_description);
        requestExternalStoragePermission();
        requestLocationPermission();
        mAddIssueButton.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   mIssueTitleEditText.setError(null);
                                                   mIssueDescriptionEditTextView.setError(null);
                                                   if (TextUtils.isEmpty(mIssueTitleEditText.getText())) {
                                                       mIssueTitleEditText.setError(getString(R.string.error_msg_title));
                                                   } else if (TextUtils.isEmpty(mIssueDescriptionEditTextView.getText())) {
                                                       mIssueDescriptionEditTextView.setError(getString(R.string.error_msg_description));
                                                   } else if (mIssuePhotoUri != null) {
                                                       addIssueToFirebase();
                                                   }

                                               }
                                           }
        );

        mIssuePhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGalleryIntent();
            }
        });
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (mLocationPermissionGranted) {
            requestDeviceCurrentLocation();
        }
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mSelectedLatlng = latLng;
                mGoogleMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(mSelectedLatlng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mGoogleMap.addMarker(markerOptions);
            }
        });
    }

    private void addIssueToFirebase() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        final StorageReference photoStorageReference = storageReference.child(UUID.randomUUID().toString());
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        mDialog = new ProgressDialog(this);
        mDialog.setIndeterminate(true);
        mDialog.setTitle(R.string.app_name);
        mDialog.setMessage(getString(R.string.uploading_photo));
        mDialog.show();
        photoStorageReference.putFile(mIssuePhotoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    photoStorageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                final Issue issue = new Issue();
                                issue.setTitle(mIssueTitleEditText.getText().toString());
                                issue.setDescription(mIssueDescriptionEditTextView.getText().toString());
                                issue.setPhoto(task.getResult().toString());
                                issue.setLocation(new GeoPoint(mSelectedLatlng.latitude, mSelectedLatlng.longitude));
                                firebaseFirestore.collection("issues").add(issue).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            Snackbar.make(mConstraintLayout, R.string.add_issue_success, Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback() {
                                                @Override
                                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                                    super.onDismissed(transientBottomBar, event);
                                                    mDialog.dismiss();
                                                    finish();
                                                }
                                            }).show();
                                        } else {
                                            Snackbar.make(mConstraintLayout, R.string.add_issue_failed, Snackbar.LENGTH_SHORT).show();
                                            mDialog.dismiss();
                                        }
                                    }
                                });
                            } else {
                                Snackbar.make(mConstraintLayout, R.string.upload_task_failed, Snackbar.LENGTH_SHORT).show();
                                mDialog.dismiss();

                            }
                        }
                    });
                } else {
                    Snackbar.make(mConstraintLayout, R.string.upload_task_failed, Snackbar.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    mIssuePhotoUri = data.getData();
                    mIssuePhotoImageView.setImageURI(mIssuePhotoUri);
                } catch (Exception e) {
                    Snackbar.make(mConstraintLayout, R.string.photo_selection_error, Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private void requestLocationPermission() {
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_LOCATION);
        }
    }

    private void requestExternalStoragePermission() {
        mReadStoragePermissionGranted = false;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mReadStoragePermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_ACCESS_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void requestDeviceCurrentLocation() {
        Task<Location> locationResult = mLocationProviderClient.getLastLocation();
        locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mLastKnownLocation = location;
                    mSelectedLatlng = new com.google.android.gms.maps.model.LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mSelectedLatlng, 15));
                    mGoogleMap.setMyLocationEnabled(true);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(mSelectedLatlng);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                } else {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_LOCATION:
                mLocationPermissionGranted = false;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    requestDeviceCurrentLocation();
                }
                break;
            case PERMISSION_REQUEST_READ_STORAGE:
                mReadStoragePermissionGranted = false;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mReadStoragePermissionGranted = true;
                }
                break;
        }
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photo)), REQUEST_GET_PHOTO);
    }

}
