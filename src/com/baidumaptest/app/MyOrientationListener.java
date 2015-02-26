package com.baidumaptest.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MyOrientationListener implements SensorEventListener {
	
	/*
	 * ����ƴ������йصı���
	 */
	private SensorManager mSensorManager;
	private Context mContext;
	private Sensor mSensor;
	private float lastX;
	
	private OnOrientationListener mOnOrientationListener;
	
	/*
	 * ���캯��
	 */
	public MyOrientationListener(Context context) {
		mContext = context;
	}
	
	/*
	 * ��ʼ����
	 */
	public void start(){
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		if(mSensorManager != null){
			//��÷��򴫸���
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		}
		if(mSensor != null){ //�������˷��򴫸�����Ҳ����˵�ֻ�֧��
			mSensorManager.registerListener(this , mSensor , SensorManager.SENSOR_DELAY_UI);
		}
	}
	
	
	/*
	 * ��������
	 */
	public void stop(){
		mSensorManager.unregisterListener(this);
	}
	

	/*
	 * �����򴫸����ķ������仯��ʱ���Զ�����
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		//���event�¼����صĴ��������ͣ���Ӧ���ǣ����򴫸���������TYPE_ORIENTATION
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			float x = event.values[SensorManager.DATA_X];
			//Ϊ�˷�ֹ���¹��죬����һ������ֵ
			if(Math.abs(x - lastX) > 1.0){
				if(mOnOrientationListener != null){
					mOnOrientationListener.onOrintationChanged(x);
				}
			}
			
			lastX = x; //���µ����һ�ε�x����
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
        //�����ȷ����仯ʱ�������������������˴�����Ҫ��ʲôҲ������д
	}
	
	

	/*
	 * ���򴫸����ļ������Ľӿ���
	 */
	public interface OnOrientationListener{
		void onOrintationChanged(float x);
	}
	
	/*
	 * �����򴫸����ļ�������ʵ����ʼ��
	 */
	public void setOnOrientationListener(OnOrientationListener mOnOrientationListener){
		this.mOnOrientationListener = mOnOrientationListener;
	}

}
