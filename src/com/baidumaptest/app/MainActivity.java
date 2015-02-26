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
 * 如果打开程序之后手机显示一片白色，什么也没有，那是因为开发者key没有对上，再去检查一下
 */
public class MainActivity extends Activity {
	
	private MapView mMapView;
	private Context context;
	
	//一个BaiduMap的对象，用于对地图的全局进行设置的变量，很重要的一个全局变量
	private BaiduMap mBaiduMap;
	
	//与定位相关的变量
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener; //定位监听器
	private boolean isFirstIn = true; //判断是否是第一次定位
	private double mLatitude; //保存一个经度值
	private double mLongtitude; //保存一个纬度值
	
	//与资源图片相关的变量
	private BitmapDescriptor mIconLocation;
	
	private MyOrientationListener myOrientationListener;
	
	//记录当前的位置
	private float mCurrentX;
	
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
        
        setContentView(R.layout.activity_main);
        
        this.context = this;
        
        //初始化这个MapView对象
        initView();
        
        //初始化定位
        initLocation();
    }
    
    
    private void initLocation() {
    	mLocationClient = new LocationClient(this);
    	mLocationListener = new MyLocationListener();
    	mLocationClient.registerLocationListener(mLocationListener); //对监听器进行注册，以后就可以使用了
    	
    	LocationClientOption option = new LocationClientOption();
    	option.setCoorType("bd09ll"); //设置坐标类型，必须为bd09LL
    	option.setIsNeedAddress(true); //这是帮我们返回一下当前的位置
    	option.setOpenGps(true); //打开GPS导航
    	option.setScanSpan(1000); //每隔1秒进行一次请求
    	mLocationClient.setLocOption(option); //把设置传进去
    	
    	//把自定义的那个方向标的图片导入进来
    	mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
    	
    	myOrientationListener = new MyOrientationListener(context);
    	myOrientationListener.setOnOrientationListener(new OnOrientationListener() {
			
			@Override
			public void onOrintationChanged(float x) {
				//这个接口的函数直到快被调用了才现实现
				mCurrentX = x;
			}
		});
    	
	}


	private void initView() {
    	mMapView = (MapView) findViewById(R.id.id_bmapView);
    	mBaiduMap = mMapView.getMap();
    	//刚打开程序的时候就会把地图进行放大
    	MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
    	mBaiduMap.setMapStatus(msu);
	}


	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
    }  
    
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
    }  
    
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
    }  
    
    
    @Override
    protected void onStart() {
    	super.onStart();
    	//首先要开启地图定位的允许
    	mBaiduMap.setMyLocationEnabled(true);
    	//然后就可以开启定位了，还得先判断一下，如果没开启才开启
    	if(!mLocationClient.isStarted()){
    		mLocationClient.start();
    	}
    	//开启方向传感器
    	myOrientationListener.start();
    }
    
    
    @Override
    protected void onStop() {
    	super.onStop();
    	//首先要关闭地图定位的允许
    	mBaiduMap.setMyLocationEnabled(false);
    	//然后就可以关闭定位了，直接关
    	mLocationClient.stop();
    	//关闭方向传感器
    	myOrientationListener.stop();
    }
    
    


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //自带的一个现成的菜单
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //当上面那个菜单的某一个菜单项被选中的时候
    	switch(item.getItemId()){
    	case R.id.id_map_common:
    		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); //显示的是普通的地图
    		break;
    	case R.id.id_map_site:
    		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE); //显示的是卫星地图
    		break;
    	case R.id.id_map_traffic:
    		if(mBaiduMap.isTrafficEnabled()){ //如果当前的实时交通状态是打开的
    			mBaiduMap.setTrafficEnabled(false); //那就关闭它
    			item.setTitle("实时交通(关)"); //改变菜单项上的文字
    		}
    		else{
    			mBaiduMap.setTrafficEnabled(true); //那就开启它
    			item.setTitle("实时交通(开)"); //改变菜单项上的文字
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
     * 定位到我的位置
     */
	private void centerToMyLocation() {
		//把location的经纬度信息保存在MapStatusUpdate对象中
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng( new LatLng(mLatitude , mLongtitude) );
		//使用动画的效果将定位转过去
		mBaiduMap.animateMapStatus(msu);
	}
    
    
    //定义一个内部类，用于实现BDLocationListener
    private class MyLocationListener implements BDLocationListener{
    	
    	/*
    	 * 定位成功后的回调
    	 */
		@Override
		public void onReceiveLocation(BDLocation location) {
			//当我们接收到location的时候，我们需要对location进行一个转换
			MyLocationData data = new MyLocationData.Builder() //
				.direction(mCurrentX) //
				.accuracy(location.getRadius()) //
				.latitude(location.getLatitude()) //
				.longitude(location.getLongitude()) //
				.build();
			
			mBaiduMap.setMyLocationData(data);
			
			//这只自定义方向图标
			MyLocationConfiguration config = new MyLocationConfiguration(com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL , true , mIconLocation);
			mBaiduMap.setMyLocationConfigeration(config);
			
			//将返回来的标示位置location的经纬度取出来
			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();
			
			/*
			 * 只有第一次定位的时候才会定位到中心点
			 */
			if(isFirstIn){
				//把location的经纬度信息保存在MapStatusUpdate对象中
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng( new LatLng(mLatitude , mLongtitude) );
				//使用动画的效果将定位转过去
		    	mBaiduMap.animateMapStatus(msu);
		    	//这样之后就不是第一次进入了，把isFirstIn设置为false
		    	isFirstIn = false;
		    	Toast.makeText(MainActivity.this , location.getAddrStr() , Toast.LENGTH_SHORT).show();
			}
			
		}
    	
    }
    
    
    
}
