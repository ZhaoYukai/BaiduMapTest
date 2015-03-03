package com.baidumaptest.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * ����������洢�븲�����йص���Ϣ
 */
public class Info implements Serializable {
	
	//�������Ҳ�Ǻ���Ҫ�ģ��б�Ҫ����
	private static final long serialVersionUID = -1038145670416694364L;
	
	//��γ�ȵ���Ϣ
	private double latitude;
	private double longtitude;
	
	//ͼƬ��Դ����������ֱ������ͼƬ�������Ҫ��¼��id
	private int imgId;
	
	//�̼ҵ�����
	private String name;
	
	//�ҵ�λ�õ��̼ҵ�ֱ�߾���
	private String distance;
	
	//���޵�����
	private int zan;
	
	
	/*
	 * ����ʵ�����̼ҵ���Ϣ����������Ŀ�ж��Ǵӷ�������ץȡ�ģ����������ʱ��һЩα���������ģ��
	 */
	public static List<Info> infos = new ArrayList<Info>();
	//��α���ݸ���һЩֵ
	static{
		infos.add( new Info(34.242652 , 108.971171 , R.drawable.a01 , "Ӣ�׹���С�ù�" , "����209��" , 1456) );
		infos.add( new Info(34.242952 , 108.972171 , R.drawable.a02 , "ɳ������ϴԡ����" , "����897��" , 456) );
		infos.add( new Info(34.242852 , 108.973171 , R.drawable.a03 , "�廷��װ��" , "����249��" , 1856) );
		infos.add( new Info(34.242152 , 108.971971 , R.drawable.a04 , "���׼�����С��" , "����679��" , 1256) );
	}
	
	
	//���캯��
	public Info(double latitude , double longtitude , int imgId , String name , String distance , int zan) {
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.imgId = imgId;
		this.name = name;
		this.distance = distance;
		this.zan = zan;
	}
	
	
	
	
	/*
	 * ����ȫ�Ǹ�����Ա������get��set����
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
