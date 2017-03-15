package com.cybercareinfoways.aisha.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.WebApi;
import com.cybercareinfoways.webapihelpers.ProfileResponse;
import com.cybercareinfoways.webapihelpers.SimpleWebRequest;
import com.cybercareinfoways.webapihelpers.SimpleWebResponse;
import com.cybercareinfoways.webapihelpers.UpdateProfileRequest;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private static final int REQUEST_CAMERA = 21;
    private static final int SELECT_FILE = 22;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    @BindView(R.id.btn_edit)
    ImageButton btnEdit;
    String userId;
    ProgressDialog dialog;
    Call<ProfileResponse> profileResponseCall;
    Call<SimpleWebResponse> updateProfileCall;
    private String userChoosenTask;
    private Bitmap bm = null;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        userId = getActivity().getSharedPreferences(AishaConstants.USERPREFS, Context.MODE_PRIVATE)
                .getString(AishaConstants.USERID, "NA");
        Log.i("user", userId);
        if (!userId.equals("NA"))
            getProfile();
        return view;
    }

    private void getProfile() {
        if (AishaUtilities.isConnectingToInternet(getActivity())) {
            callProfileWebService();
        } else {
            Snackbar snackbar = Snackbar.make(etName, AishaConstants.ACCECPTTERMS, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tvMessage.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void callProfileWebService() {
        dialog = AishaUtilities.showProgressDialog(getActivity(), AishaConstants.PROFILEMSG);
        dialog.show();
        WebApi api = AishaUtilities.setupRetrofit();
        profileResponseCall = api.getProfile(new SimpleWebRequest(userId));
        profileResponseCall.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == AishaConstants.SUCCESS) {
                        int imgStat = response.body().getImage_status();
                        Log.i("msg", response.body().getMessage());
                        String name = response.body().getName();
                        etName.setText(name);
                        if (imgStat == AishaConstants.SUCCESS) {
                            String imageUrl = response.body().getImage_url();
                            Picasso.with(getActivity()).load(imageUrl).resize(160, 160).into(ivProfile);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                dialog.dismiss();
                Snackbar snackbar = Snackbar.make(etName, AishaConstants.PROFILEFETCHERROR, Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.RED);
                // Changing action button text color
                View sbView = snackbar.getView();
                TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                tvMessage.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        });
    }

    @OnClick(R.id.btn_edit)
    void editPhotos() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = AishaUtilities.checkSDPermission(getActivity());
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result) cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result) galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case AishaUtilities.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (userChoosenTask.equals("Take Photo"))
                cameraIntent();
            else if (userChoosenTask.equals("Choose from Library"))
                galleryIntent();
        } else {
            Toast.makeText(getActivity(), "Please grant permission from application's settings", Toast.LENGTH_SHORT).show();
        }
//                break;
//        }
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        bm = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO upload image
        ivProfile.setImageBitmap(bm);

    }

    private void onSelectFromGalleryResult(Intent data) {

        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //TODO upload image
        ivProfile.setImageBitmap(bm);
    }

    @OnClick(R.id.btn_update)
    public void uploadImage() {
        if (AishaUtilities.isConnectingToInternet(getActivity())) {
            callUploadWebservice();
        } else {
            Snackbar snackbar = Snackbar.make(etName, AishaConstants.ACCECPTTERMS, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tvMessage.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void callUploadWebservice() {
        dialog = AishaUtilities.showProgressDialog(getActivity(), AishaConstants.UPDATEPROFILEMSG);
        dialog.show();
        String userName = etName.getText().toString();
        UpdateProfileRequest req = new UpdateProfileRequest(userId, userName);
        if (bm != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            req.setImage(encodedImage);
        } else {
            req.setImage("");
        }
        Log.i(userId, req.getImage());
        WebApi api = AishaUtilities.setupRetrofit();
        updateProfileCall = api.updateProfile(req);
        updateProfileCall.enqueue(new Callback<SimpleWebResponse>() {
            @Override
            public void onResponse(Call<SimpleWebResponse> call, Response<SimpleWebResponse> response) {
                dialog.dismiss();
                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<SimpleWebResponse> call, Throwable t) {
                dialog.dismiss();
                Snackbar snackbar = Snackbar.make(etName, AishaConstants.UPDATEPROFILEERROR, Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.RED);
                // Changing action button text color
                View sbView = snackbar.getView();
                TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                tvMessage.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (profileResponseCall != null && !profileResponseCall.isExecuted()) {
            profileResponseCall.cancel();
        }
        if (updateProfileCall != null && !updateProfileCall.isExecuted()) {
            updateProfileCall.cancel();
        }
    }
}
