package com.box.box_preview_sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.box.androidsdk.browse.activities.BoxBrowseFileActivity;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxFolder;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.preview.BoxPreviewActivity;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Sample activity that demonstrates preview functionality for BoxFiles of an authenticated user
 */
public class MainActivity extends AppCompatActivity implements BoxAuthentication.AuthListener {


    BoxSession mSession = null;

    private static final int BROWSE_FILE_REQUEST_CODE = 101;
    private static final int PREVIEW_FILE_REQUEST_CODE = 102;
    private static final String ROOT_FOLDER_ID = "0";

    /** Maintaining state to support browsing back up to selected folders */
    private ArrayList<BoxFolder> mPathToRoot;
    private boolean mLoadedRoot;
    private static final String ROOT_IS_LOADED = "Bundle.Current_folder";
    private static final String PATH_TO_ROOT = "Bundle.Path_To_Root";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BoxConfig.IS_LOG_ENABLED = true;
        BoxConfig.CLIENT_ID = "kjilhl3575vip5dfq0j6blljdb55ofb6";
        BoxConfig.CLIENT_SECRET = "2O1lXEOB0B5OMCbWqLx1I2neS3r0vMtC";

        // needs to match redirect uri in developer settings if set.
        BoxConfig.REDIRECT_URL = "https://example.com/oauth2callback";

        if (savedInstanceState != null) {
            mPathToRoot = (ArrayList<BoxFolder>) savedInstanceState.getSerializable(PATH_TO_ROOT);
            mLoadedRoot =  savedInstanceState.getBoolean(ROOT_IS_LOADED);
        }
        initialize(false);
    }

    /**
     * Open the box preview activity for previewing this file
     *
     * @param file
     */
    private void launchPreview(BoxFile file) {
        mPathToRoot = file.getPathCollection().getEntries();
        BoxFolder parentFolder = file.getParent() == null ? BoxFolder.createFromId("0") : file.getParent();
        BoxPreviewActivity.IntentBuilder builder = BoxPreviewActivity.createIntentBuilder(this, mSession, file)
                .setBoxFolder(parentFolder);
        //set the box folder, so the sdk can page through other files in the directory for images, audio or video
        startActivityForResult(builder.createIntent(), PREVIEW_FILE_REQUEST_CODE);
    }

    /**
     * Browsing files in the folder using the box browse sdk.
     * @param folder BoxFolder to browse
     */
    private void browseFolder(BoxFolder folder) {
        startActivityForResult(CustomBrowseFileActivity.
                        getLaunchIntent(MainActivity.this, folder, mSession),
                BROWSE_FILE_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Response of the box browse api
        if (requestCode == BROWSE_FILE_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (data.getSerializableExtra(BoxBrowseFileActivity.EXTRA_BOX_FILE) instanceof BoxFile) {
                    launchPreview((BoxFile) data.getSerializableExtra(BoxBrowseFileActivity.EXTRA_BOX_FILE));
                } else {
                    Toast.makeText(this, com.box.androidsdk.preview.R.string
                            .box_previewsdk_unable_to_access_file, Toast.LENGTH_LONG).show();
                }
                break;
                case Activity.RESULT_CANCELED:
                    up();
                    break;
                case CustomBrowseFileActivity.RESULT_LOGOUT:
                    if (mSession == null) {
                        return;
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                mSession.logout().get();
                                mLoadedRoot = false;
                                initialize(true);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
            }


        } else if (requestCode == PREVIEW_FILE_REQUEST_CODE) { //Preview api response
            up();
        }
    }

    //Browse the parent folder or exit if we are already at the root
    private void up() {
        if (mPathToRoot == null || mPathToRoot.isEmpty()) {
            finish();
        } else {
            BoxFolder folder = mPathToRoot.get(mPathToRoot.size() - 1);
            mPathToRoot.remove(folder);
            browseFolder(folder);

        }
    }

    /**
     * Authenticate using the box sdk.
     */
    private void initialize(boolean showUserPicker) {
        mSession = showUserPicker ? new BoxSession(this, null) : new BoxSession(this);
        mSession.setSessionAuthListener(this);
        mSession.authenticate();
    }

    @Override
    public void onRefreshed(BoxAuthentication.BoxAuthenticationInfo info) {
    }

    @Override
    public void onAuthCreated(BoxAuthentication.BoxAuthenticationInfo info) {
        if (!mLoadedRoot) {
            loadRootFolder();
        }
    }

    @Override
    public void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
        if (ex != null) {
            mLoadedRoot = false;
            initialize(false);
        } else {
            finish();
        }
    }

    @Override
    public void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
        mLoadedRoot = false;
    }


    private void loadRootFolder() {
        mLoadedRoot = true;
        browseFolder(BoxFolder.createFromId(ROOT_FOLDER_ID));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PATH_TO_ROOT, mPathToRoot);
        outState.putBoolean(ROOT_IS_LOADED, mLoadedRoot);
    }


}
