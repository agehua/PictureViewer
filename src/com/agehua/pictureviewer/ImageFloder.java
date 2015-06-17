package com.agehua.pictureviewer;

import java.io.Serializable;

/**
 * 用于保存第一张图片的路径、图片文件夹的路径、文件夹的名称、文件夹内图片的数量
 * @author Agehua
 *
 */
public class ImageFloder implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8115827768395897376L;

	/**
	 * 图片的文件夹路径
	 */
	private String dir;

	/**
	 * 第一张图片的路径
	 */
	private String firstImagePath;

	/**
	 * 文件夹的名称
	 */
	private String name;

	/**
	 * 图片的数量
	 */
	private int count;

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
		int lastIndexOf = this.dir.lastIndexOf("/");
		this.name = this.dir.substring(lastIndexOf);
	}

	public String getFirstImagePath() {
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath) {
		this.firstImagePath = firstImagePath;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
