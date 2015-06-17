package com.agehua.pictureviewer.activity;

import java.util.ArrayList;









import com.agehua.pictureviewer.CallBack;
import com.agehua.pictureviewer.CommonAdapter;
import com.agehua.pictureviewer.ImageFloder;
import com.agehua.pictureviewer.R;
import com.agehua.pictureviewer.ViewHolder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class PictureDirsChoiceFrag extends Fragment implements OnItemClickListener, OnClickListener {
	private ListView listview;
	private ArrayList<ImageFloder> dirs;
	private TextView head_title;
	private View view;
	private CallBack callback;
	public static PictureDirsChoiceFrag fragment;
	Bundle bundle ;

	public static PictureDirsChoiceFrag getInstance(Bundle bundle) {
		fragment = new PictureDirsChoiceFrag();
		if (bundle != null) {
			fragment.setArguments(bundle);
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		bundle= getArguments();
		dirs = (ArrayList<ImageFloder>) (bundle.getSerializable("dirs"));
		view = inflater.inflate(R.layout.pic_dirs_activity_layout, null);
		initView();
		return view;
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

	private void initView() {
		listview = (ListView) view.findViewById(R.id.id_list_dir);

		listview.setAdapter(new CommonAdapter<ImageFloder>(getActivity().getApplicationContext(), dirs, R.layout.pic_dirs_list_item) {
			@Override
			public void convert(ViewHolder helper, ImageFloder item) {
				helper.setText(R.id.id_dir_item_name, item.getName().indexOf("/")==0?item.getName().substring(item.getName().indexOf("/")+1):item.getName());
				helper.setImageByUrl(R.id.id_dir_item_image, item.getFirstImagePath());
				helper.setText(R.id.id_dir_item_count, item.getCount() + "张");
			}
		});
		listview.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		ImageFloder imgDir = (ImageFloder) parent.getAdapter().getItem(position);
		String title = imgDir.getName().indexOf("/")==0?imgDir.getName().substring(imgDir.getName().indexOf("/")+1):imgDir.getName();
		bundle.putSerializable("mSelectImg", bundle.getSerializable("mSelectImg"));
		bundle.putSerializable("ImageFloder", (ImageFloder) parent.getAdapter().getItem(position));
		bundle.putString("title", title);
		bundle.putInt("imgWay", bundle.getInt("imgWay"));

		bundle.putInt(PicturePreviewerActivity.OP_TYPE, PicturePreviewerActivity.PICIMGCHOICE);
		callback.callback(bundle);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
