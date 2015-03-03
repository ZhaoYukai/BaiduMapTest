package com.baidumaptest.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * 这个类用来存储与覆盖物有关的信息
 */
public class Info implements Serializable {
	
	//这个编码也是很重要的，有必要生成
	private static final long serialVersionUID = -1038145670416694364L;
	
	//经纬度的信息
	private double latitude;
	private double longtitude;
	
	//图片资源。由于这里直接引入图片，因此需要记录其id
	private int imgId;
	
	//商家的名称
	private String name;
	
	//我的位置到商家的直线距离
	private String distance;
	
	//点赞的数量
	private int zan;
	
	
	/*
	 * 由于实际上商家的信息在真正的项目中都是从服务器中抓取的，因此这里暂时用一些伪造的数据来模拟
	 */
	public static List<Info> infos = new ArrayList<Info>();
	//给伪数据赋上一些值
	static{
		infos.add( new Info(34.242652 , 108.971171 , R.drawable.a01 , "英伦贵族小旅馆" , "距离209米" , 1456) );
		infos.add( new Info(34.242952 , 108.972171 , R.drawable.a02 , "沙井国际洗浴会所" , "距离897米" , 456) );
		infos.add( new Info(34.242852 , 108.973171 , R.drawable.a03 , "五环服装城" , "距离249米" , 1856) );
		infos.add( new Info(34.242152 , 108.971971 , R.drawable.a04 , "老米家泡馍小炒" , "距离679米" , 1256) );
	}
	
	
	//构造函数
	public Info(double latitude , double longtitude , int imgId , String name , String distance , int zan) {
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.imgId = imgId;
		this.name = name;
		this.distance = distance;
		this.zan = zan;
	}
	
	
	
	
	/*
	 * 下面全是各个成员变量的get和set方法
	 */
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public int getZan() {
		return zan;
	}

	public void setZan(int zan) {
		this.zan = zan;
	}

}
