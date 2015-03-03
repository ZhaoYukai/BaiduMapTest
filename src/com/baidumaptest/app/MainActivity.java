package com.baidumaptest.app;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidumaptest.app.MyOrientationListener.OnOrientationListener;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * 如果打开程序之后手机显示一片白色，什么也没有，那是因为开发者key没有对上，再去检查一下
 */
public class MainActivity extends Activity implements OnItemClickListener{
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ArrayList<String> menuLists;
	private ArrayAdapter<String> adapter;
	private ActionBarDrawerToggle mDrawerToggle;
	private String mtitle; //用于保存程序最开始的那个标题
	
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
	
	//用于模式切换的变量
	private LocationMode mLocationMode;
	
	//覆盖物相关
	private BitmapDescriptor mMarker;
	private RelativeLayout mMarkerLy;
	
	
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
        
        setContentView(R.layout.activity_main);
        
        //控件初始化
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        this.context = this;
        
        //字符串列表初始化
        menuLists = new ArrayList<String>();
        menuLists.add("普通地图");
        menuLists.add("卫星地图");
        menuLists.add("实时交通");
        menuLists.add("回到我的位置");
        menuLists.add("普通模式");
        menuLists.add("跟随模式");
        menuLists.add("罗盘模式");
        menuLists.add("添加覆盖物");
        
        
        //初始化一个适配器
        adapter = new ArrayAdapter<String>(MainActivity.this , android.R.layout.simple_list_item_1 , menuLists);
        
        //对抽屉布局左侧的列表设置一个设配器，适配器中已经设置好了布局内容
        mDrawerList.setAdapter(adapter);
        
        //给左侧抽屉布局的菜单项设置监听器，监听点击事件
        mDrawerList.setOnItemClickListener(this);
        
        //mDrawerToggle用于表示抽屉是被打开还是关闭
        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this , mDrawerLayout , R.drawable.ic_drawer , R.string.drawer_open , R.string.drawer_close){
        	
        	@Override
        	public void onDrawerOpened(View drawerView) {
        		// 当抽屉打开的时候执行这个方法
        		super.onDrawerOpened(drawerView);
        		//更改标题
        		mtitle = getActionBar().getTitle().toString();
        		getActionBar().setTitle("请选择");
        	}
        	
        	@Override
        	public void onDrawerClosed(View drawerView) {
        		// 当抽屉关闭的时候执行这个方法
        		super.onDrawerClosed(drawerView);
        		//还原原来的那个标题
        		getActionBar().setTitle(mtitle);
        	}
        };
        
        //设置一个监听器，对mDrawerToggle进行监听
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        //开启ActionBar上App Icon的功能，之后才能单击图片就能把抽屉打开
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        
        //初始化这个MapView对象
        initView();
        
        //初始化定位
        initLocation();
        
        //初始化跟覆盖物相关的一些操作
        initMarker();
        
        //添加一个对Marker点击事件的监听器,当点击覆盖物的时候会触发激活一个显示商家信息的布局
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				Bundle extraInfo = marker.getExtraInfo();
				Info info = (Info) extraInfo.getSerializable("info"); //根据键值对取出数据
				//下面是从获取到控件的实例
				ImageView iv = (ImageView) mMarkerLy.findViewById(R.id.id_info_img);
				TextView name = (TextView) mMarkerLy.findViewById(R.id.id_info_name);
				TextView distance = (TextView) mMarkerLy.findViewById(R.id.id_info_distance);
				TextView zan = (TextView) mMarkerLy.findViewById(R.id.id_info_zan);
				//然后把info中的信息替换到控件实例中
				iv.setImageResource(info.getImgId());
				name.setText(info.getName());
				distance.setText(info.getDistance());
				zan.setText(info.getZan() + ""); //注意，info.getZan()返回来的是int型，需要加上空字符串自动把它转换为字符串
				//最后把显示商家信息的布局设置为可见
				mMarkerLy.setVisibility(View.VISIBLE);
				
				//在地图上显示一个气泡窗口，里面显示点击的商家名称，不过这里只是初步设置了这个TextView，还没进行放置
				TextView tv = new TextView(context);
				tv.setBackgroundResource(R.drawable.location_tips);
				tv.setPadding(30, 20, 30, 50);
				tv.setText(info.getName());
				tv.setTextColor(Color.parseColor("#ffffff"));
				
				//将经纬度坐标进行一个小小的偏移，因为TextView得往上移动一些才不至于遮住原有的覆盖物
				final LatLng latLng = marker.getPosition(); //获得点击位置的地图上的经纬度坐标
				Point p = mBaiduMap.getProjection().toScreenLocation(latLng); //把经纬度坐标转换成手机屏幕上对应的像素点的位置
				p.y -= 47; //进行一个小小的偏移，很必要
				LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p); //再转回来
				
				//设定一个点击InfoWindow对象的监听器，如果点击了就隐藏这个信息显示框
				BitmapDescriptor lobd = BitmapDescriptorFactory.fromView(tv);
				InfoWindow infoWindow = new InfoWindow(lobd, ll, 0 , new OnInfoWindowClickListener() {
					
					@Override
					public void onInfoWindowClick() {
						mBaiduMap.hideInfoWindow();
					}
				});
				
				//默认是显示这个信息显示框的
				mBaiduMap.showInfoWindow(infoWindow);
				
				return true;
			}
		});
        
        
        //添加一个对地图点击事件的监听器，我们希望当点击地图的时候，那个商家信息的布局能消失
        mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onMapClick(LatLng arg0) {
				mMarkerLy.setVisibility(View.GONE); //让那个布局消失
				mBaiduMap.hideInfoWindow(); //让那个信息显示框消失
			}
		});
    }
    
    
    /*
     * 初始化跟覆盖物相关的一些操作
     */
    private void initMarker() {
    	
    	mMarker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
    	mMarkerLy = (RelativeLayout) findViewById(R.id.id_maker_ly);
	}


	/*
     * 需要将ActionDrawerToggle与DrawerLayout的状态同步
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
    	super.onPostCreate(savedInstanceState);
    	//将ActionDrawerToggle与DrawerLayout的状态同步
    	//也需要将ActionDrawerToggle中的drawer图标设置成新的
    	mDrawerToggle.syncState();
    }
    
    
    /*
     * 当屏幕旋转的时候，对Configuration重新进行配置
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    
    
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

    	if(position == 0){
    		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); //显示的是普通的地图
    	}
    	else if(position == 1){
    		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE); //显示的是卫星地图
    	}
    	else if(position == 2){
    		if(mBaiduMap.isTrafficEnabled()){ //如果当前的实时交通状态是打开的
    			mBaiduMap.setTrafficEnabled(false); //那就关闭它
    			Toast.makeText(this , "实时交通已关闭" , Toast.LENGTH_SHORT).show();
    		}
    		else{
    			mBaiduMap.setTrafficEnabled(true); //那就开启它
    			Toast.makeText(this , "实时交通已开启" , Toast.LENGTH_SHORT).show();
    		}
    	}
    	else if(position == 3){ //回到我的位置
    		centerToMyLocation();
    	}
    	else if(position == 4){ //普通模式
    		mLocationMode = LocationMode.NORMAL;
    	}
    	else if(position == 5){ //跟随模式
    		mLocationMode = LocationMode.FOLLOWING;
    	}
    	else if(position == 6){ //罗盘模式
    		mLocationMode = LocationMode.COMPASS;
    	}
    	else if(position == 7){ //添加覆盖物
    		//自定义的方法，用于添加覆盖物
    		addOverlays(Info.infos);
    	}
    	
    	
    	/*
    	 * 当点击完菜单之后，当然要让左侧的布局自动隐藏了
    	 */
    	mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    
    
    /*
     * 自定义的方法，用于添加覆盖物
     */
    private void addOverlays(List<Info> infos) {
    	//由于下面要添加覆盖物了，因此这里先把定位那些图标清除掉
    	mBaiduMap.clear();
    	
    	LatLng latLng = null; //声明一个经纬度变量
    	
    	Marker marker  = null;
    	OverlayOptions options;
    	
    	for(Info info : infos){
    		//得到infos中每个元素的经纬度信息
    		latLng = new LatLng(info.getLatitude() , info.getLongtitude());
    		//把infos元素中每个存储的图标【icon(mMarker)】提取出来，然后设置覆盖到指定的经纬度坐标【position(latLng)】上，同时指定图层的层级【zIndex(5)】
    		//而这一系列指定这指定那的总控制开关，就是这个OverlayOptions对象（图层对象）
    		options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
    		//注意此处的addOverlay和本方法名addOverlays是两个东西，不过二者的翻译都是添加图层
    		marker = (Marker) mBaiduMap.addOverlay(options);
    		//不过在使用marker之前需要往里面传递info信息，这一步很必要
    		Bundle arg0 = new Bundle();
    		arg0.putSerializable("info" , info); //键值对
    		marker.setExtraInfo(arg0);
    	}
    	
    	//跳转到刚才添加的这些覆盖物所在的位置，方便查看
    	MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
    	mBaiduMap.setMapStatus(msu);
		
	}//自定义方法addOverlays()结束
    


	private void initLocation() {
    	mLocationMode = LocationMode.NORMAL; //模式默认是普通模式
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
    	
    	mBaiduMap.setTrafficEnabled(false); //实时交通默认关闭
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
			MyLocationConfiguration config = new MyLocationConfiguration(mLocationMode , true , mIconLocation);
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
