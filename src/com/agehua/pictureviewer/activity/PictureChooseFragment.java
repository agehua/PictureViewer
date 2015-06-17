package com.agehua.pictureviewer.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;








import com.agehua.pictureviewer.CallBack;
import com.agehua.pictureviewer.ImageFloder;
import com.agehua.pictureviewer.ImgChoiceAdapter;
import com.agehua.pictureviewer.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PictureChooseFragment extends Fragment implements OnClickListener {
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs;
	private GridView gridView;
	private ImageFloder imgDir;
	private ImgChoiceAdapter mAdapter;
	public ImageView head_back;
	public Button confirm;
	public TextView head_title;
	private CallBack callback;
	private View view;
	public static PictureChooseFragment fragment;
	Bundle bundle;
//	private static ArrayList<ArrayList<String>> mSelectedImages;
	private static ArrayList<String> mSelectedImage;
	private int imgIndex =0;

	public static PictureChooseFragment getInstance(Bundle bundle) {
		fragment = new PictureChooseFragment();
		if (bundle != null) {
			fragment.setArguments(bundle);
		}
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		callback = (CallBack) activity;
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

		view = inflater.inflate(R.layout.pic_upload_img, null);
		bundle = getArguments();
		imgDir = (ImageFloder) (bundle.getSerializable("ImageFloder"));
		imgIndex = bundle.getInt("imgWay");
		gridView = (GridView) view.findViewById(R.id.id_gridView);
		confirm = (Button)view.findViewById(R.id.btn_confirm);
		confirm.setOnClickListener(this);
		initData();
		return view;
	}


	private void initData() {
		mImgDir = new File(imgDir.getDir());
		/**
		 * 重新查找目录内符合格式的图片
		 */
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")){
					if(filename.contains(".")&&filename.indexOf(".")>0){
						return true;
					}
				}
				return false;
			}
		}));
		
//		if (bundle.getSerializable("mSelectImg")==null){
		//去掉了记录选择的图片
			mSelectedImage = new ArrayList<String>();
			mSelectedImage.add(" ");
			mSelectedImage.add(" ");
			mSelectedImage.add(" ");
//		}else {
//			mSelectedImage = (ArrayList<String>) bundle.getSerializable("mSelectImg");
//		}
		
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new ImgChoiceAdapter(callback, getActivity().getApplicationContext(),
				mSelectedImage, imgIndex,
				1, mImgs, R.layout.pic_viewer_grid_item, mImgDir.getAbsolutePath());

		gridView.setAdapter(mAdapter);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			if (mAdapter.getSelectedImage().size() != 0) {
				bundle.putInt(PicturePreviewerActivity.OP_TYPE, PicturePreviewerActivity.ONRESULT);
				bundle.putStringArrayList("List", mAdapter.getSelectedImage());
				bundle.putInt("imgWay", imgIndex);
				callback.callback(bundle);
			} else {
				Toast.makeText(getActivity(), "您还没有选择图片", Toast.LENGTH_LONG);
			}
			break;

		default:
			break;
		}
	}

}
