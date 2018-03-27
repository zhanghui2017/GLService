package com.gengli.glservice.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.util.LogUtils;
import com.gengli.glservice.util.PhotoBitmapUtil;

public class PicEditActivity extends Activity {

    private TextView pic_edit_commit;
    private ImageView pic_edit_img;
    private Bitmap alteredBitmap; // 备份图片
    private Canvas canvas; // 画布
    private Paint paint; // 画刷
    private Bitmap bmp; // 载入图片
    private Bitmap mbmp; // 复制模版
    private Matrix matrix = new Matrix();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_edit);
        initView();
        Intent intent = getIntent();
        String path = intent.getStringExtra("img_path"); // 对应putExtra("path", path);
        ShowPhotoByImageView(path);
    }

    private void initView() {
        pic_edit_img = (ImageView) findViewById(R.id.pic_edit_img);
        pic_edit_commit = (TextView) findViewById(R.id.pic_edit_commit);
        pic_edit_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mbmp == null) { // 防止出现mbmp空
                        mbmp = bmp;
                    }
                    // 图像上传 先保存 后传递图片路径
                    Uri uri = PhotoBitmapUtil.loadBitmap(PicEditActivity.this, mbmp);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(uri);
                    sendBroadcast(intent);
                    // 上传图片*
                    Intent intentPut = new Intent(PicEditActivity.this, CreatFormActivity.class);
                    intentPut.putExtra("pathProcess", PhotoBitmapUtil.pathPicture);

                    setResult(RESULT_OK, intentPut);
                    // 返回上一界面
                    LogUtils.showCenterToast(PicEditActivity.this, "图片上传成功");
                    PicEditActivity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.showCenterToast(PicEditActivity.this, "图像上传失败");
                }
            }
        });

    }

    private void ShowPhotoByImageView(String path) {
        if (null == path) {
            LogUtils.showCenterToast(this, "加载失败");
            finish();
        }

        // 获取分辨率
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels; // 屏幕水平分辨率
        int height = dm.heightPixels; // 屏幕垂直分辨率
        try {
            // Load up the image's dimensions not the image itself
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = true;
            bmp = BitmapFactory.decodeFile(path, bmpFactoryOptions);
            int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
            int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);
            // 压缩显示
            if (heightRatio > 1 && widthRatio > 1) {
                if (heightRatio > widthRatio) {
                    bmpFactoryOptions.inSampleSize = heightRatio * 2;
                } else {
                    bmpFactoryOptions.inSampleSize = widthRatio * 2;
                }
            }
            // 图像真正解码
            bmpFactoryOptions.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeFile(path, bmpFactoryOptions);
            mbmp = bmp.copy(Bitmap.Config.ARGB_8888, true);

            // 加载备份图片 绘图使用
            alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
            canvas = new Canvas(alteredBitmap); // 画布
            paint = new Paint(); // 画刷
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(5);
            paint.setTextSize(30);
            paint.setTypeface(Typeface.DEFAULT_BOLD); // 无线粗体
            matrix = new Matrix();
            canvas.drawBitmap(bmp, matrix, paint);
            pic_edit_img.setImageBitmap(bmp); // 显示照片

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
