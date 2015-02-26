package com.baidumaptest.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MyOrientationListener implements SensorEventListener {
	
	/*
	 * 与控制传感器有关的变量
	 */
	private SensorManager mSensorManager;
	private Context mContext;
	private Sensor mSensor;
	private float lastX;
	
	private OnOrientationListener mOnOrientationListener;
	
	/*
	 * 构造函数
	 */
	public MyOrientationListener(Context context) {
		mContext = context;
	}
	
	/*
	 * 开始监听
	 */
	public void start(){
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		if(mSensorManager != null){
			//获得方向传感器
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		}
		if(mSensor != null){ //如果获得了方向传感器，也就是说手机支持
			mSensorManager.registerListener(this , mSensor , SensorManager.SENSOR_DELAY_UI);
		}
	}
	
	
	/*
	 * 结束监听
	 */
	public void stop(){
		mSensorManager.unregisterListener(this);
	}
	

	/*
	 * 当方向传感器的方向发生变化的时候自动调用
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		//如果event事件返回的传感器类型，对应的是，方向传感器的类型TYPE_ORIENTATION
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			float x = event.values[SensorManager.DATA_X];
			//为了防止更新过快，设置一个门限值
			if(Math.abs(x - lastX) > 1.0){
				if(mOnOrientationListener != null){
					mOnOrientationListener.onOrintationChanged(x);
				}
			}
			
			lastX = x; //更新到最近一次的x坐标
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
        //当精度发生变化时会调用这个函数，不过此处不需要，什么也不往里写
	}
	
	

	/*
	 * 方向传感器的监听器的接口类
	 */
	public interface OnOrientationListener{
		void onOrintationChanged(float x);
	}
	
	/*
	 * 将方向传感器的监听器的实例初始化
	 */
	public void setOnOrientationListener(OnOrientationListener mOnOrientationListener){
		this.mOnOrientationListener = mOnOrientationListener;
	}

}
