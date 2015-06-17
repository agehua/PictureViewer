package com.agehua.pictureviewer;

import android.os.Bundle;

/**
 * 用于Activity和Fragment之间交互的回调接口<br />
 * 由Activity实现该接口
 * 
 * @author Yong Zhao
 * @created 2013-07-30 14:39:23
 * 
 */
public interface CallBack {
	/**
	 * 回调方法
	 * 
	 * @param args
	 *            传递的参数
	 * @return bundle
	 */
	public Bundle callback(Bundle args);
}
