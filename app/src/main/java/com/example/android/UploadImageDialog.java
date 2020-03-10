package com.example.android;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadImageDialog extends AppCompatActivity {

    @BindView(R.id.btnOpenCamera)
    Button btnOpenCamera;
    @BindView(R.id.btnOpenGallery)
    Button btnOpenGallery;
    private File tempFile;
    private String TAG = "UploadImageDialog";
    private Boolean isCamera = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_upload_image);
        tedPermission();
        ButterKnife.bind(this);

        View.OnClickListener listener = view -> {
            switch (view.getId()) {
                case R.id.btnOpenGallery:
                    Intent openGallery = new Intent(Intent.ACTION_PICK);
                    goToAlbum();
                    openGallery.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(openGallery, 1);
                    break;
                case R.id.btnOpenCamera:
                    Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePhoto();
                    try {
                        tempFile = createImageFile();
                    } catch (IOException e) {
                        Toast.makeText(UploadImageDialog.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                        Log.e(TAG, "오류났다 ㅎㅎ 오류내용 = " + e);
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                        Uri photoUri = FileProvider.getUriForFile(UploadImageDialog.this, "android.provider", tempFile);
                        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(openCamera, 2);

                    } else {

                        Uri photoUri = Uri.fromFile(tempFile);
                        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(openCamera, 2);

                    }
                    break;

            }
        };
        btnOpenCamera.setOnClickListener(listener);
        btnOpenGallery.setOnClickListener(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                Uri photoUri = data.getData();
                Cursor cursor = null;

try {

                    /*
                     *  Uri 스키마를
                     *  content:/// 에서 file:/// 로  변경한다.
                     */
                    String[] proj = {MediaStore.Images.Media.DATA};

                    assert photoUri != null;
                    cursor = getContentResolver().query(photoUri, proj, null, null, null);

                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    cursor.moveToFirst();

                    tempFile = new File(cursor.getString(column_index));

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                Log.e(TAG, "isCamera = " + isCamera);
                //받아온 이미지의 절대경로를 부모 액티비티로 보냄.
                Intent savedImage = new Intent();
                try {
                    ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);
                    savedImage.putExtra("image", tempFile.getAbsolutePath());
                } catch (Exception e) {
                    savedImage.putExtra("image", "null");
                    Toast.makeText(getApplicationContext(), "이미지를 선택하지 않음.", Toast.LENGTH_SHORT).show();
                }

                setResult(RESULT_OK, savedImage);
                finish();

            } else if (requestCode == 2) {
                //받아온 이미지의 절대경로를 부모 액티비티로 보냄.
                Intent savedImage = new Intent();
                try {
                    ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);
                    savedImage.putExtra("image", tempFile.getAbsolutePath());
                } catch (Exception e) {
                    savedImage.putExtra("image", "null");
                    Toast.makeText(getApplicationContext(), "이미지를 선택하지 않음.", Toast.LENGTH_SHORT).show();
                }

                setResult(RESULT_OK, savedImage);
                finish();

            }
        } else {
            Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show();
        }

    }

    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( cameraImage_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "cameraImage_" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( cameraImage )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/cameraImage/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    private void goToAlbum() {
        isCamera = false;

    }

    private void takePhoto() {
        isCamera = true;

    }

    private void setImage() {

ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.e(TAG, "setImage : " + tempFile.getAbsolutePath());

}
}