package com.agehua.pictureviewer.activity;

import com.agehua.pictureviewer.R;
import com.agehua.utils.ImageUtil;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;


public class PicturePreivewFrag extends Fragment implements OnTouchListener, OnClickListener {
	public static final String NOTICE_FROM = "Notice_From";
	private int window_width, window_height;// 控件宽度
	private ImageView dragImageView;
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist;
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	private View view;
	Bundle bundle;

	private PhotoViewAttacher mAttacher;
	public static PicturePreivewFrag fragment;
	
	public static PicturePreivewFrag getInstance(Bundle bundle) {
		fragment = new PicturePreivewFrag();
		if (bundle != null) {
			fragment.setArguments(bundle);
		}
		return fragment;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
				
		view = inflater.inflate(R.layout.pic_pre_viewer_activity, null);
		dragImageView = (ImageView) view.findViewById(R.id.busi_notice_image);
		dragImageView.setOnTouchListener(this);
		mAttacher = new PhotoViewAttacher(dragImageView);
		bundle= getArguments();
		if (null != bundle) {
			checkFrom(bundle.getInt(NOTICE_FROM));
		}
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void checkFrom(int from) {
		switch (from) {
		case 0x06:// 图片预览
			if (!TextUtils.isEmpty(bundle.getString("picPath"))) {
				String path = bundle.getString("picPath");
				byte[] b = ImageUtil.decodeBitmap(path);
				Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
				if (bitmap != null) {
					setDragImageView(bitmap);
					mAttacher.update();
				}
			}
			break;

		default:
			break;
		}
	}


	private void setDragImageView(Bitmap bitmap) {
		/** 获取可見区域高度 **/
		WindowManager manager = getActivity().getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();
		//为了让图片与屏幕宽度一致
		Bitmap bmp = getBitmap(bitmap, window_width, window_height);

		// 设置图片
		dragImageView.setImageBitmap(bmp);

		mAttacher.update();
	}

	/***
	 * 等比例压缩图片
	 * 
	 * @param bitmap
	 * @param screenWidth
	 * @param screenHight
	 * @return
	 */
	public static Bitmap getBitmap(Bitmap bitmap, int screenWidth, int screenHight) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;
		

		// scale = scale < scale2 ? scale : scale2;

		// 保证图片不变形.
		matrix.postScale(scale, scale);
		// w,h是原图的属性.
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 设置拖拉模式
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
			// 设置多点触摸模式
		case MotionEvent.ACTION_POINTER_DOWN:
			//oldDist适用于记录两根手指之间的距离
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
			// 若为DRAG模式，则点击移动图片
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				// 设置位移
				matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
			}
			// 若为ZOOM模式，则多点触摸缩放
			else if (mode == ZOOM) {
				float newDist = spacing(event);

				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					// 设置缩放比例和图片中点位置
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}

		// Perform the transformation
		dragImageView.setImageMatrix(matrix);

		return true;
	}

	// 计算移动距离
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	// 计算中点位置
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}
	}
}
