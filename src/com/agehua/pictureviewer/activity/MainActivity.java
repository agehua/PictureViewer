package com.agehua.pictureviewer.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashSet;

import com.agehua.pictureviewer.ImageFloder;
import com.agehua.pictureviewer.R;
import com.agehua.utils.ImageUtil;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity{

	Button btnStart;
	ImageView imgPreview;
	private static int SELECT_PIC_BY_PICK_PHOTO_NO_SDCARD = 0x3;

	/**
	 * 已选择的图片
	 * */
	private ArrayList<String> mSelectedImg = new ArrayList<String>();

	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();
	int totalCount = 0;
	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private ArrayList<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 选中图片的uri
	 */
	private Uri photoUri;

	private Bitmap bitmap = null;
	private SoftReference<Bitmap> srf = null;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case 3:
				if (mImageFloders.size() == 0) {
				} else {
					Bundle bundle = new Bundle();
					bundle.putSerializable("dirs", mImageFloders);
					bundle.putSerializable("mSelectImg", mSelectedImg);
					//imgWay区分是拍照，还是本地图片
					//					bundle.putInt("imgWay", imgWay);
					bundle.putInt("Flag", 0x02);
					Intent intent = new Intent(MainActivity.this, PicturePreviewerActivity.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, PicturePreviewerActivity.PICK_PHOTO_BY_PREVIEWER);
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btnStart =(Button)findViewById(R.id.btn_start_previewer);
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != mImageFloders) {
					mImageFloders.clear();
					getImages();
				}
			}
		});

		imgPreview =(ImageView)findViewById(R.id.img_previewer);
	}


	/**
	 * 先判断有无sdcard，有则利用ContentProvider扫描手机中的图片，
	 * 此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			//		Toast.makeText(activity, "暂无外部存储", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO_NO_SDCARD);
		}else {
			new Thread(new Runnable() {
				@Override
				public void run() {

					String firstImage = null;

					Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					ContentResolver mContentResolver = getContentResolver();

					// 只查询jpeg和png的图片
					Cursor mCursor = mContentResolver.query(mImageUri, null, 
							MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?", 
							new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
					while (mCursor.moveToNext()) {
						// 获取图片的路径
						String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

						// 拿到第一张图片的路径
						if (firstImage == null)
							firstImage = path;
						// 获取该图片的父路径名
						File parentFile = new File(path).getParentFile();
						if (parentFile == null)
							continue;
						String dirPath = parentFile.getAbsolutePath();
						ImageFloder imageFloder = null;
						// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
						if (null == mDirPaths) {
							mDirPaths = new HashSet<String>();
						}
						if (mDirPaths.contains(dirPath)) {
							continue;
						} else {
							mDirPaths.add(dirPath);
							// 初始化imageFloder
							imageFloder = new ImageFloder();
							imageFloder.setDir(dirPath);
							imageFloder.setFirstImagePath(path);
						}
						Log.d("!AAAAAAAAAA", parentFile+"");

						String imgs[]= null;
						imgs = parentFile.list(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String filename) {
								if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")) {
									if (filename.contains(".") && filename.indexOf(".") > 0) {
										return true;
									}
								}
								return false;
							}
						});
						int picSize =0;
						if (null!= imgs){
							picSize= imgs.length;
						}
						totalCount += picSize;

						imageFloder.setCount(picSize);
						mImageFloders.add(imageFloder);

						//记录下图片数量最多的那个目录
						if (picSize > mPicsSize) {
							mPicsSize = picSize;
							mImgDir = parentFile;
						}
					}
					mCursor.close();

					// 扫描完成，辅助的HashSet也就可以释放内存了
					mDirPaths = null;

					// 通知Handler扫描图片完成
					handler.sendEmptyMessage(3);
				}
			}).start();
		}

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			if (requestCode == PicturePreviewerActivity.PICK_PHOTO_BY_PREVIEWER) {
				if (null != data){
					if (null != data.getStringArrayListExtra("List")) {
						ArrayList<String> img_paths = data.getStringArrayListExtra("List");
						int imgIndex = data.getIntExtra("imgWay", 0);
						for (String path : img_paths) {
							if (!mSelectedImg.contains(path)) {
								mSelectedImg.add(imgIndex,path);
							}
						}

						setImage(img_paths.get(0));
						imgPreview.setImageBitmap(srf.get());
					}
				}
				return;
			}else if (requestCode == SELECT_PIC_BY_PICK_PHOTO_NO_SDCARD) {
				if (data == null) {
					Toast.makeText(getApplicationContext(), "error file", Toast.LENGTH_LONG);
					return;
				}
				photoUri = data.getData();

				String[] pojo = { MediaStore.Images.Media.DATA };

				Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
				String picPath ="";
				if (cursor != null) {
					int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
					cursor.moveToFirst();
					picPath = cursor.getString(columnIndex);
				}
				if (picPath != null && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG") || picPath.endsWith(".jpeg") || picPath.endsWith(".JPEG") || picPath.endsWith(".bmp") || picPath.endsWith(".BMP"))) {

				} else {
					Toast.makeText(getApplicationContext(), "格式暂不支持", Toast.LENGTH_LONG);
				}
				File file = new File(picPath);// 可以 用于判断文件的大小
				setImage(picPath);
				imgPreview.setImageBitmap(srf.get());
			}


			System.gc();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}



	private void setImage(String picPath) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		opts.inSampleSize = ImageUtil.computeSampleSize(opts, -1, 800 * 600);
		opts.inJustDecodeBounds = false;
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inDither = false;
		opts.inPurgeable = true;
		opts.inTempStorage = new byte[16 * 1024];
		byte[] b = ImageUtil.decodeBitmap(picPath);
		bitmap = BitmapFactory.decodeByteArray(b, 0, b.length, opts);
		if (bitmap == null)
			return;
		Bitmap scaleBitmap = ImageUtil.zoomDrawable(bitmap, 150, 200);
		scaleBitmap = ImageUtil.getRoundedCornerBitmap(scaleBitmap);
		srf = new SoftReference<Bitmap>(bitmap);

	}

}
