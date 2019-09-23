package com.zhihu.matisse.ui;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.ui.widget.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MatisseCropActivity extends AppCompatActivity {
    private static final String TAG = "MatisseCropActivity";
    private CropImageView cropImageView;
    private TextView cropImageViewCancel;
    private TextView cropImageViewSubmit;
    private Context mContext;
    private File cropCacheFolder;
    private SelectionSpec mSpec;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //transparent status bar
        setStatusBarFullTransparent();
        //create layout
        setContentView(R.layout.activity_crop_image);
        mContext = this;
        cropImageView = findViewById(R.id.crop_image_view);
        cropImageViewCancel = findViewById(R.id.crop_image_view_cancel);
        cropImageViewSubmit = findViewById(R.id.crop_image_view_submit);
        mSpec = SelectionSpec.getInstance();


        List<Uri> uris = getIntent().getParcelableArrayListExtra(MatisseActivity.EXTRA_RESULT_SELECTION);

        cropImageView.setImageURI(uris.get(0));
        cropImageView.setFocusWidth(mSpec.isCropSquare ? getScreenWidth() - getScreenWidth() / 15 : getScreenWidth());
        cropImageView.setFocusHeight(mSpec.isCropSquare ? cropImageView.getFocusWidth() : getScreenHeight() / 4);
        initListener();
    }

    private void initListener() {
        cropImageView.setOnBitmapSaveCompleteListener(new CropImageView.OnBitmapSaveCompleteListener() {
            @Override
            public void onBitmapSaveSuccess(File file) {
                Intent result = new Intent();
                ArrayList<Uri> selectedUris = new ArrayList<>();
                ArrayList<String> selectedPaths = new ArrayList<>();
                selectedUris.add(Uri.parse(file.getPath()));
                selectedPaths.add(file.getPath());
                result.putParcelableArrayListExtra(MatisseActivity.EXTRA_RESULT_SELECTION, selectedUris);
                result.putStringArrayListExtra(MatisseActivity.EXTRA_RESULT_SELECTION_PATH, selectedPaths);
                setResult(RESULT_OK, result);
                finish();
            }

            @Override
            public void onBitmapSaveError(File file) {

            }
        });

        cropImageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        cropImageViewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.saveBitmapToFile(getCropCacheFolder(mContext), cropImageView.getFocusWidth(), cropImageView.getFocusHeight(), false);

            }
        });
    }

    private File getCropCacheFolder(Context context) {
        if (cropCacheFolder == null) {
            cropCacheFolder = new File(context.getExternalFilesDir("image") + "/crop/");
        }
        return cropCacheFolder;
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        //noinspection ConstantConditions
        wm.getDefaultDisplay().getRealSize(point);
        return point.x;
    }

    private int getScreenHeight() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        //noinspection ConstantConditions
        wm.getDefaultDisplay().getRealSize(point);
        return point.y;
    }

    private void setStatusBarFullTransparent() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
