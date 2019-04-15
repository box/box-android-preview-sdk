package com.box.box_preview_sample;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.box.androidsdk.browse.activities.BoxBrowseFileActivity;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxFolder;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.utils.SdkUtils;

/**
 * Extend BoxBrowseFileActivity to add custom menu items for demoing a few auth options
 */
public class CustomBrowseFileActivity extends BoxBrowseFileActivity {

    static final int RESULT_LOGIN = 2;
    static final int RESULT_LOGOUT = 3;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int numAccounts = BoxAuthentication.getInstance().getStoredAuthInfo(this).keySet().size();
        menu.findItem(R.id.logout).setVisible(numAccounts > 0);
        menu.findItem(R.id.login).setVisible(numAccounts == 0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.login:
                setResult(RESULT_LOGIN);
                finish();
                return true;
            case R.id.logout:
                setResult(RESULT_LOGOUT);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public static Intent getLaunchIntent(Context context, BoxFolder folder, BoxSession session) {
        if (folder != null && !SdkUtils.isBlank(folder.getId())) {
            if (session != null && session.getUser() != null && !SdkUtils.isBlank(session.getUser().getId())) {
                Intent intent = new Intent(context, CustomBrowseFileActivity.class);
                intent.putExtra("extraItem", folder);
                intent.putExtra("extraUserId", session.getUser().getId());
                return intent;
            } else {
                throw new IllegalArgumentException("A valid user must be provided to browse");
            }
        } else {
            throw new IllegalArgumentException("A valid folder must be provided to browse");
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("custom browse", "back");
        super.onBackPressed();
    }

}
