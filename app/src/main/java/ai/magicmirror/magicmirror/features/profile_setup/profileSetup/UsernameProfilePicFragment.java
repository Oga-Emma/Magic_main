package ai.magicmirror.magicmirror.features.profile_setup.profileSetup;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.utils.ImagePickerUtils;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;

import static ai.magicmirror.magicmirror.utils.ConstantsUtils.Integers.PICK_IMAGE_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsernameProfilePicFragment extends Fragment
        implements View.OnClickListener {

    private static final String TAG = UsernameProfilePicFragment.class.getSimpleName();
    Interface.SelectImage selectImage;
    Interface.SetUsernameAndSelfieImage setUsernameAndSelfieImage;

    Button selectImageButton;
    CircleImageView profileImageView;

    public UsernameProfilePicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.pagesetup_fragment_username_profile_pic, container, false);

        view.findViewById(R.id.profile_setup_select_image_button).setOnClickListener(this::onClick);
        profileImageView = view.findViewById(R.id.profile_setup_profile_image_image_view);
        profileImageView.setOnClickListener(this::onClick);

        EditText usernameEditText = view.findViewById(R.id.username_edit_text);
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()))
                    setUsernameAndSelfieImage.setUsername(s.toString());
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Interface.SetUsernameAndSelfieImage)
            setUsernameAndSelfieImage = ((Interface.SetUsernameAndSelfieImage) context);
        else
            throw new RuntimeException("your activity must implement setUsername interface");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_IMAGE_ID:
                Bitmap selfieImageBitmap = ImagePickerUtils.getImageFromResult(getContext(), resultCode, data);

                File file = ImagePickerUtils.convertBitmapToFile(getContext(), selfieImageBitmap);

                if (file != null) {
                    try {
                        Bitmap selfieImageFull = new Compressor(getContext())
                                .compressToBitmap(file);

                        Bitmap selfieImageThumbnail = new Compressor(getContext())
                                .setMaxWidth(200)
                                .setMaxHeight(200)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                .compressToBitmap(file);

                        if (selfieImageFull != null && selfieImageThumbnail != null) {
                            //do some shits
                            profileImageView.setImageBitmap(selfieImageFull);
                            setUsernameAndSelfieImage.setSelfieImage(selfieImageFull, selfieImageThumbnail);

                        } else {
                            Toasty.error(getContext(), "Error1 reading image, please try again",
                                    Toast.LENGTH_LONG).show();

                        }

//                Toasty.normal(getApplicationContext(), "Image selected", Toast.LENGTH_SHORT).show();
                        break;

                    } catch (Exception e) {
                        e.printStackTrace();

                        Log.e(TAG, e.getMessage(), e);
                        Toasty.error(getContext(), "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                        break;
                    }
                } else {
                    Toasty.error(getContext(), "Error2 reading image, please try again", Toast.LENGTH_LONG).show();
                }

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;

        }
    }


    @Override
    public void onClick(View v) {
//        selectImage.selectImageClicked();
        Intent chooseImageIntent = ImagePickerUtils
                .getPickImageIntent(getContext());
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

}
