package com.agehua.pictureviewer.activity;

import com.agehua.pictureviewer.CallBack;
import com.agehua.pictureviewer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class PicturePreviewerActivity extends FragmentActivity implements CallBack {

	public static final int PICDIRSCHOICE = 0x01;
	public static final int PICIMGCHOICE = 0x02;
	public static final int PICPREVIEW = 0x03;
	public static final int ONBACKPRESSED = 0x05;
	public static final int  PICK_PHOTO_BY_PREVIEWER = 0x11;
	public static final int ONRESULT = 0x06;
	public static final int ONRESULT_CHANGE_PHONE = 0x07;
	public static final int ONRESULT_FORGET_PW = 0x08;
	public static final int ONRESULT_RESET_PW = 0x09;
	public static final String OP_TYPE = "op_type";

	public static boolean isException = false;// 是否为异常情况,且在确认页点击取消预约
	public static final String ISEXCEPTIONKEY = "isExceptionKey";
	public static final String EXCEPTION_JSON = "exception_json";// 出现异常时,从新进入录入页,带过来的参数


	private FragmentManager fm;
	private int level;
	private int flag= 0x01;

	private Fragment newFragment;

	public Button head_confirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fundoutaccount);
		fm = getSupportFragmentManager();

		Bundle bundlePre = getIntent().getExtras();
		if (bundlePre != null){
			flag = bundlePre.getInt("Flag");
			bundlePre.putInt(OP_TYPE, PICDIRSCHOICE);
		}else {
			bundlePre = new Bundle();
			bundlePre.putInt(OP_TYPE, PICDIRSCHOICE);
		}
		callback(bundlePre);
	}

	//	@Override
	//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	//		// TODO Auto-generated method stub
	//		super.onActivityResult(requestCode, resultCode, data);
	//	}

	@Override
	public Bundle callback(Bundle args) {
		// TODO Auto-generated method stub

		if (args != null && args.containsKey(OP_TYPE)) {
			int op_type = args.getInt(OP_TYPE);
			FragmentTransaction ft = fm.beginTransaction();
			ft.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out, R.anim.push_left_in, R.anim.push_right_out);
			switch (op_type) {
			case PICDIRSCHOICE:
				// 显示图片目录
				if (newFragment == null) {
					level = 1;
					newFragment = PictureDirsChoiceFrag.getInstance(args);

					ft.add(R.id.TransFragment, newFragment, level + "");
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
					ft.addToBackStack(level + "");
					ft.commit();
				} else {
					if (newFragment instanceof PictureDirsChoiceFrag) {
					} else {
						level = 1;
						ft.hide(newFragment);
						newFragment = PictureDirsChoiceFrag.getInstance(args);
						ft.add(R.id.TransFragment, newFragment, level + "");
						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
						ft.addToBackStack(level + "");
						ft.commit();
					}
				}
				break;
			case PICIMGCHOICE:
				//选择图片
				level = 2;
				ft.hide(newFragment);
				newFragment = PictureChooseFragment.getInstance(args);
				ft.add(R.id.TransFragment, newFragment, level + "");
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				ft.addToBackStack(level + "");
				ft.commit();
				break;
			case PICPREVIEW:
				// 选中图片预览
				level = 3;
				ft.hide(newFragment);
				newFragment = PicturePreivewFrag.getInstance(args);
				ft.add(R.id.TransFragment, newFragment, level + "");
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				ft.addToBackStack(level + "");
				ft.commit();
				break;
			case ONBACKPRESSED:
				onBackPressed();
				break;
			case ONRESULT: 
				switch (flag) {
				case 0x01://跳转到其他的AccountChangeCardActivity
//					Intent intent = new Intent(
//							PicturePreviewerActivity.this,
//							AccountChangeCardActivity.class);
//					intent.putExtras(args);
//					setResult(PICK_PHOTO_BY_PREVIEWER, intent);
//					finish();

					break;
				case 0x02:  //跳转到MainActivity
					Intent intent1 = new Intent(
							PicturePreviewerActivity.this,
							MainActivity.class);
					intent1.putExtras(args);
					setResult(RESULT_OK, intent1);
					finish();

					break;
					
				case 0x03:  //跳转到ChangePhoneActivity
//					Intent intent2 = new Intent(
//							PicturePreviewerActivity.this,
//							ChangePhoneActivity.class);
//					intent2.putExtras(args);
//					setResult(RESULT_OK, intent2);
//					finish();
					break;
					
				case 0x04:  //跳转到SetTradePwResetActivity
//					Intent intent3 = new Intent(
//							PicturePreviewerActivity.this,
//							SetTradePwResetActivity.class);
//					intent3.putExtras(args);
//					setResult(PICK_PHOTO_BY_PREVIEWER, intent3);
//					finish();
					break;

				default:
					break;
				}
				break;
//			case ONRESULT_FORGET_PW: //跳转到ForgetPwUploadCardActivity
//				Intent intent1 = new Intent(
//						PicturePreviewerActivity.this,
//						ForgetPwUploadCardActivity.class);
//				intent1.putExtras(args);
//				setResult(PICK_PHOTO_BY_PREVIEWER, intent1);
//				finish();
//				break;
//
//			case ONRESULT_CHANGE_PHONE: //跳转到ChangePhoneActivity
//				Intent intent2 = new Intent(
//						PicturePreviewerActivity.this,
//						ChangePhoneActivity.class);
//				intent2.putExtras(args);
//				//				setResult(ChangePhoneActivity.PICK_PHOTO_BY_PREVIEWER, intent2);
//				finish();
//				break;
//
//			case ONRESULT_RESET_PW: //跳转到SetTradePwResetActivity
//				Intent intent3 = new Intent(
//						PicturePreviewerActivity.this,
//						SetTradePwResetActivity.class);
//				intent3.putExtras(args);
//				//				setResult(SetTradePwResetActivity.PICK_PHOTO_BY_PREVIEWER, intent3);
//				finish();
//				break;

			}
		}
		return null;
	}

	/**
	 * 堆栈后进先出，堆栈顶的就是用户能看到的对象
	 */
	@Override
	public void onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		if (level == 3) {
			//			finish();
			fm.popBackStackImmediate(level + "", FragmentManager.POP_BACK_STACK_INCLUSIVE);
			level--;
			newFragment = fm.findFragmentByTag(level + "");
		} else if (level == 2) {
			fm.popBackStackImmediate(level + "", FragmentManager.POP_BACK_STACK_INCLUSIVE);
			level--;
			newFragment = fm.findFragmentByTag(level + "");
		} else if (level == 1) {
			level--;
			finish();
		} else {
			finish();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
