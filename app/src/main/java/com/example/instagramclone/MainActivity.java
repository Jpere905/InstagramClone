package com.example.instagramclone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    // for camera intent usage

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 20;
    public String photoFileName = "InstagramCloneUserImg";
    private File photoFile;

    private EditText etDescription;
    private Button btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDescription = findViewById(R.id.etDescription);
        btnCaptureImage = findViewById(R.id.btnCaptureImage);
        ivPostImage = findViewById(R.id.ivPostImage);
        btnSubmit = findViewById(R.id.btnSubmit);

        //queryPosts();

        // performs actions to open camera app via implicit intent
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        // performs actions to submit the post
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String description = etDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Description cannot be empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (photoFile == null || ivPostImage.getDrawable() == null){
                    Toast.makeText(MainActivity.this, "There is no image",
                            Toast.LENGTH_SHORT).show();
                    return ;
                }
                // at this point, the desc is valid

                // get the user, i.e. whoever is signed in right now
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, photoFile);

            }

        });
    }

    private void savePost(String description, ParseUser currentUser, File photoFile) {
        Post post = new Post();
        // remember that the setter and getter methods for the post class implement methods defined
        // in the ParseObject class which make it easy to save this data
        post.setDescription(description);
        post.setUser(currentUser);
        post.setImage(new ParseFile(photoFile));
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving post", e);
                    Toast.makeText(MainActivity.this, "Error while saving post",
                            Toast.LENGTH_SHORT).show();
                    return ;
                }
                // if at this point, post was saved successfully
                Log.i(TAG, "Post save was successful!");
                // reset description text
                etDescription.setText("");
                // 0 is empty data, so it resets the image
                ivPostImage.setImageResource(0);
            }
        });
    }

    // get a post from our posts database
    private void queryPosts(){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error with retrieving list of posts", e);
                    return ;
                }
                // if reached this point, data fetch is successful, so iterate thru each post
                for (Post post: posts){
                    Log.i(TAG, "Post: " + post.getDescription() +
                            ", Username: " + post.getUser().getUsername());
                }

            }
        });
    }


    // ****************** code for using the camera app **********************

    private void launchCamera() {
        // create intent to take a picture and then return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(
                MainActivity.this, "com.example.instagramclone", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        // if we call startActivityForResult() using an intent that no app can handle,
        // the app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // once in the camera app, this overridden method will bring us back to our app
    // is invoked when the tiled application returns to parent application
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // resize bitmap
                // load taken image into a preview
                ivPostImage.setImageBitmap(takenImage);
            }
            // result is a failure
            else {
                Toast.makeText(this, "Picture wasn't taken", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // returns the file for a photo stored on disk given the photoFileName
    private File getPhotoFileUri(String photoFileName) {

        // get safe storage directory for photos
        // use 'getExternalFilesDir' on Context to access package-specific directories
        // this way, we don't need to request external read/write runtime permissions
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.e(TAG, "Failed to create directory for image");
        }

        // return the file target for the photo based on filename
        File file = new File (mediaStorageDir.getPath() + File.separator + photoFileName);

        return file;
    }

    // ****************** end code for using the camera app **********************

}