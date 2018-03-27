package com.gengli.glservice.bean;

import java.io.Serializable;

public class Fitting implements Serializable{
	private int id;
	private String title;
	private String desc;
	private String imgUrl;

	/**
	 * 申请数量
	 */
	private int chooseCount;

	/**
	 * 价格
	 */
	private String price;

	public int getChooseCount() {
		return chooseCount;
	}

	public void setChooseCount(int chooseCount) {
		this.chooseCount = chooseCount;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
}
