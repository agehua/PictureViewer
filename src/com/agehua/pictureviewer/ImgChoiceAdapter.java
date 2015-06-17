package com.agehua.pictureviewer;

import java.util.ArrayList;
import java.util.List;





import com.agehua.pictureviewer.activity.PicturePreivewFrag;
import com.agehua.pictureviewer.activity.PicturePreviewerActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;


public class ImgChoiceAdapter extends CommonAdapter<String> {
	//	private Activity activity;
	private int limit;// 限制上传张数
	private CallBack callback;
	Context context;
	private int imgIndex;
	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	private static ArrayList<String> mSelectedImage = new ArrayList<String>();

	/**
	 * 文件夹路径
	 */
	private String mDirPath;

	public ImgChoiceAdapter(CallBack callback, Context context,ArrayList<String> mSelectedImage, 
			int imgIndex,int limit, List<String> mDatas, int itemLayoutId, String dirPath) {
		super(context, mDatas, itemLayoutId);
		this.context = context;
		this.callback = callback;
		this.limit = limit;
		this.mDirPath = dirPath;
		this.imgIndex = imgIndex;
		this.mSelectedImage=mSelectedImage;
	}

	public ImgChoiceAdapter(Context context, int limit, List<String> mImgs, int gridItem, String absolutePath) {
		super(context, mImgs, gridItem);
		this.context = context;
		this.limit = limit;
		this.mDirPath = absolutePath;
	}

	public ArrayList<String> getSelectedImage() {
		return mSelectedImage;
	}

	public void clearSelectedImage(){
		mSelectedImage.clear();
	}
	@Override
	public void convert(final ViewHolder helper, final String item) {
		// 设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		// 设置no_selected
		helper.setImageResource(R.id.id_item_select, R.drawable.picture_unselected);
		// 设置图片
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);

		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);

		// 设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//				Intent intent = new Intent(activity, PicturePreivewFrag.class);
				Bundle bundle = new Bundle();
				bundle.putInt(PicturePreivewFrag.NOTICE_FROM, 0x06);
				bundle.putString("picPath", mDirPath + "/" + item);
				bundle.putInt(PicturePreviewerActivity.OP_TYPE, PicturePreviewerActivity.PICPREVIEW);
				callback.callback(bundle);
			}
		});
		mSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 已经选择过该图片
				if (mSelectedImage.get(imgIndex).equals(mDirPath + "/" + item)) {
					mSelectedImage.set(imgIndex, " ");	
					mSelect.setImageResource(R.drawable.picture_unselected);
				} else { // 未选择该图片
					if(mSelectedImage.get(imgIndex).trim().equals("")){//未超过限制的张数,添加进去
						mSelectedImage.add(imgIndex,mDirPath + "/" + item);
						mSelect.setImageResource(R.drawable.pictures_selected);
					}else{//已超过,弹框提示已满
					}
				}
			}
		});
		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImage.get(imgIndex).equals(mDirPath + "/" + item)) {
			mSelect.setImageResource(R.drawable.pictures_selected);
		}

	}

}
