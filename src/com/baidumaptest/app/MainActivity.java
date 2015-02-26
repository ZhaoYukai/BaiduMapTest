package com.baidumaptest.app;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidumaptest.app.MyOrientationListener.OnOrientationListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

/*
 * ����򿪳���֮���ֻ���ʾһƬ��ɫ��ʲôҲû�У�������Ϊ������keyû�ж��ϣ���ȥ���һ��
 */
public class MainActivity extends Activity {
	
	private MapView mMapView;
	private Context context;
	
	//һ��BaiduMap�Ķ������ڶԵ�ͼ��ȫ�ֽ������õı���������Ҫ��һ��ȫ�ֱ���
	private BaiduMap mBaiduMap;
	
	//�붨λ��صı���
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener; //��λ������
	private boolean isFirstIn = true; //�ж��Ƿ��ǵ�һ�ζ�λ
	private double mLatitude; //����һ������ֵ
	private double mLongtitude; //����һ��γ��ֵ
	
	//����ԴͼƬ��صı���
	private BitmapDescriptor mIconLocation;
	
	private MyOrientationListener myOrientationListener;
	
	//��¼��ǰ��λ��
	private float mCurrentX;
	
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
        SDKInitializer.initialize(getApplicationContext());  
        
        setContentView(R.layout.activity_main);
        
        this.context = this;
        
        //��ʼ�����MapView����
        initView();
        
        //��ʼ����λ
        initLocation();
    }
    
    
    private void initLocation() {
    	mLocationClient = new LocationClient(this);
    	mLocationListener = new MyLocationListener();
    	mLocationClient.registerLocationListener(mLocationListener); //�Լ���������ע�ᣬ�Ժ�Ϳ���ʹ����
    	
    	LocationClientOption option = new LocationClientOption();
    	option.setCoorType("bd09ll"); //�����������ͣ�����Ϊbd09LL
    	option.setIsNeedAddress(true); //���ǰ����Ƿ���һ�µ�ǰ��λ��
    	option.setOpenGps(true); //��GPS����
    	option.setScanSpan(1000); //ÿ��1�����һ������
    	mLocationClient.setLocOption(option); //�����ô���ȥ
    	
    	//���Զ�����Ǹ�������ͼƬ�������
    	mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
    	
    	myOrientationListener = new MyOrientationListener(context);
    	myOrientationListener.setOnOrientationListener(new OnOrientationListener() {
			
			@Override
			public void onOrintationChanged(float x) {
				//����ӿڵĺ���ֱ���챻�����˲���ʵ��
				mCurrentX = x;
			}
		});
    	
	}


	private void initView() {
    	mMapView = (MapView) findViewById(R.id.id_bmapView);
    	mBaiduMap = mMapView.getMap();
    	//�մ򿪳����ʱ��ͻ�ѵ�ͼ���зŴ�
    	MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
    	mBaiduMap.setMapStatus(msu);
	}


	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
    }  
    
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
    }  
    
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
    }  
    
    
    @Override
    protected void onStart() {
    	super.onStart();
    	//����Ҫ������ͼ��λ������
    	mBaiduMap.setMyLocationEnabled(true);
    	//Ȼ��Ϳ��Կ�����λ�ˣ��������ж�һ�£����û�����ſ���
    	if(!mLocationClient.isStarted()){
    		mLocationClient.start();
    	}
    	//�������򴫸���
    	myOrientationListener.start();
    }
    
    
    @Override
    protected void onStop() {
    	super.onStop();
    	//����Ҫ�رյ�ͼ��λ������
    	mBaiduMap.setMyLocationEnabled(false);
    	//Ȼ��Ϳ��Թرն�λ�ˣ�ֱ�ӹ�
    	mLocationClient.stop();
    	//�رշ��򴫸���
    	myOrientationListener.stop();
    }
    
    


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //�Դ���һ���ֳɵĲ˵�
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //�������Ǹ��˵���ĳһ���˵��ѡ�е�ʱ��
    	switch(item.getItemId()){
    	case R.id.id_map_common:
    		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); //��ʾ������ͨ�ĵ�ͼ
    		break;
    	case R.id.id_map_site:
    		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE); //��ʾ�������ǵ�ͼ
    		break;
    	case R.id.id_map_traffic:
    		if(mBaiduMap.isTrafficEnabled()){ //�����ǰ��ʵʱ��ͨ״̬�Ǵ򿪵�
    			mBaiduMap.setTrafficEnabled(false); //�Ǿ͹ر���
    			item.setTitle("ʵʱ��ͨ(��)"); //�ı�˵����ϵ�����
    		}
    		else{
    			mBaiduMap.setTrafficEnabled(true); //�ǾͿ�����
    			item.setTitle("ʵʱ��ͨ(��)"); //�ı�˵����ϵ�����
    		}
    		break;
    	case R.id.id_map_location:
    		centerToMyLocation();
    		break;
    	default:
    		break;
    	}
        
        return super.onOptionsItemSelected(item);
    }

    /*
     * ��λ���ҵ�λ��
     */
	private void centerToMyLocation() {
		//��location�ľ�γ����Ϣ������MapStatusUpdate������
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng( new LatLng(mLatitude , mLongtitude) );
		//ʹ�ö�����Ч������λת��ȥ
		mBaiduMap.animateMapStatus(msu);
	}
    
    
    //����һ���ڲ��࣬����ʵ��BDLocationListener
    private class MyLocationListener implements BDLocationListener{
    	
    	/*
    	 * ��λ�ɹ���Ļص�
    	 */
		@Override
		public void onReceiveLocation(BDLocation location) {
			//�����ǽ��յ�location��ʱ��������Ҫ��location����һ��ת��
			MyLocationData data = new MyLocationData.Builder() //
				.direction(mCurrentX) //
				.accuracy(location.getRadius()) //
				.latitude(location.getLatitude()) //
				.longitude(location.getLongitude()) //
				.build();
			
			mBaiduMap.setMyLocationData(data);
			
			//��ֻ�Զ��巽��ͼ��
			MyLocationConfiguration config = new MyLocationConfiguration(com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL , true , mIconLocation);
			mBaiduMap.setMyLocationConfigeration(config);
			
			//���������ı�ʾλ��location�ľ�γ��ȡ����
			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();
			
			/*
			 * ֻ�е�һ�ζ�λ��ʱ��Żᶨλ�����ĵ�
			 */
			if(isFirstIn){
				//��location�ľ�γ����Ϣ������MapStatusUpdate������
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng( new LatLng(mLatitude , mLongtitude) );
				//ʹ�ö�����Ч������λת��ȥ
		    	mBaiduMap.animateMapStatus(msu);
		    	//����֮��Ͳ��ǵ�һ�ν����ˣ���isFirstIn����Ϊfalse
		    	isFirstIn = false;
		    	Toast.makeText(MainActivity.this , location.getAddrStr() , Toast.LENGTH_SHORT).show();
			}
			
		}
    	
    }
    
    
    
}
