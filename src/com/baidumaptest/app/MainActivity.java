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
 * ����򿪳���֮���ֻ���ʾһƬ��ɫ��ʲôҲû�У�������Ϊ������keyû�ж��ϣ���ȥ���һ��
 */
public class MainActivity extends Activity implements OnItemClickListener{
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ArrayList<String> menuLists;
	private ArrayAdapter<String> adapter;
	private ActionBarDrawerToggle mDrawerToggle;
	private String mtitle; //���ڱ�������ʼ���Ǹ�����
	
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
	
	//����ģʽ�л��ı���
	private LocationMode mLocationMode;
	
	//���������
	private BitmapDescriptor mMarker;
	private RelativeLayout mMarkerLy;
	
	
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
        SDKInitializer.initialize(getApplicationContext());  
        
        setContentView(R.layout.activity_main);
        
        //�ؼ���ʼ��
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        this.context = this;
        
        //�ַ����б��ʼ��
        menuLists = new ArrayList<String>();
        menuLists.add("��ͨ��ͼ");
        menuLists.add("���ǵ�ͼ");
        menuLists.add("ʵʱ��ͨ");
        menuLists.add("�ص��ҵ�λ��");
        menuLists.add("��ͨģʽ");
        menuLists.add("����ģʽ");
        menuLists.add("����ģʽ");
        menuLists.add("��Ӹ�����");
        
        
        //��ʼ��һ��������
        adapter = new ArrayAdapter<String>(MainActivity.this , android.R.layout.simple_list_item_1 , menuLists);
        
        //�Գ��벼�������б�����һ�������������������Ѿ����ú��˲�������
        mDrawerList.setAdapter(adapter);
        
        //�������벼�ֵĲ˵������ü���������������¼�
        mDrawerList.setOnItemClickListener(this);
        
        //mDrawerToggle���ڱ�ʾ�����Ǳ��򿪻��ǹر�
        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this , mDrawerLayout , R.drawable.ic_drawer , R.string.drawer_open , R.string.drawer_close){
        	
        	@Override
        	public void onDrawerOpened(View drawerView) {
        		// ������򿪵�ʱ��ִ���������
        		super.onDrawerOpened(drawerView);
        		//���ı���
        		mtitle = getActionBar().getTitle().toString();
        		getActionBar().setTitle("��ѡ��");
        	}
        	
        	@Override
        	public void onDrawerClosed(View drawerView) {
        		// ������رյ�ʱ��ִ���������
        		super.onDrawerClosed(drawerView);
        		//��ԭԭ�����Ǹ�����
        		getActionBar().setTitle(mtitle);
        	}
        };
        
        //����һ������������mDrawerToggle���м���
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        //����ActionBar��App Icon�Ĺ��ܣ�֮����ܵ���ͼƬ���ܰѳ����
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        
        //��ʼ�����MapView����
        initView();
        
        //��ʼ����λ
        initLocation();
        
        //��ʼ������������ص�һЩ����
        initMarker();
        
        //���һ����Marker����¼��ļ�����,������������ʱ��ᴥ������һ����ʾ�̼���Ϣ�Ĳ���
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				Bundle extraInfo = marker.getExtraInfo();
				Info info = (Info) extraInfo.getSerializable("info"); //���ݼ�ֵ��ȡ������
				//�����Ǵӻ�ȡ���ؼ���ʵ��
				ImageView iv = (ImageView) mMarkerLy.findViewById(R.id.id_info_img);
				TextView name = (TextView) mMarkerLy.findViewById(R.id.id_info_name);
				TextView distance = (TextView) mMarkerLy.findViewById(R.id.id_info_distance);
				TextView zan = (TextView) mMarkerLy.findViewById(R.id.id_info_zan);
				//Ȼ���info�е���Ϣ�滻���ؼ�ʵ����
				iv.setImageResource(info.getImgId());
				name.setText(info.getName());
				distance.setText(info.getDistance());
				zan.setText(info.getZan() + ""); //ע�⣬info.getZan()����������int�ͣ���Ҫ���Ͽ��ַ����Զ�����ת��Ϊ�ַ���
				//������ʾ�̼���Ϣ�Ĳ�������Ϊ�ɼ�
				mMarkerLy.setVisibility(View.VISIBLE);
				
				//�ڵ�ͼ����ʾһ�����ݴ��ڣ�������ʾ������̼����ƣ���������ֻ�ǳ������������TextView����û���з���
				TextView tv = new TextView(context);
				tv.setBackgroundResource(R.drawable.location_tips);
				tv.setPadding(30, 20, 30, 50);
				tv.setText(info.getName());
				tv.setTextColor(Color.parseColor("#ffffff"));
				
				//����γ���������һ��СС��ƫ�ƣ���ΪTextView�������ƶ�һЩ�Ų�������סԭ�еĸ�����
				final LatLng latLng = marker.getPosition(); //��õ��λ�õĵ�ͼ�ϵľ�γ������
				Point p = mBaiduMap.getProjection().toScreenLocation(latLng); //�Ѿ�γ������ת�����ֻ���Ļ�϶�Ӧ�����ص��λ��
				p.y -= 47; //����һ��СС��ƫ�ƣ��ܱ�Ҫ
				LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p); //��ת����
				
				//�趨һ�����InfoWindow����ļ��������������˾����������Ϣ��ʾ��
				BitmapDescriptor lobd = BitmapDescriptorFactory.fromView(tv);
				InfoWindow infoWindow = new InfoWindow(lobd, ll, 0 , new OnInfoWindowClickListener() {
					
					@Override
					public void onInfoWindowClick() {
						mBaiduMap.hideInfoWindow();
					}
				});
				
				//Ĭ������ʾ�����Ϣ��ʾ���
				mBaiduMap.showInfoWindow(infoWindow);
				
				return true;
			}
		});
        
        
        //���һ���Ե�ͼ����¼��ļ�����������ϣ���������ͼ��ʱ���Ǹ��̼���Ϣ�Ĳ�������ʧ
        mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onMapClick(LatLng arg0) {
				mMarkerLy.setVisibility(View.GONE); //���Ǹ�������ʧ
				mBaiduMap.hideInfoWindow(); //���Ǹ���Ϣ��ʾ����ʧ
			}
		});
    }
    
    
    /*
     * ��ʼ������������ص�һЩ����
     */
    private void initMarker() {
    	
    	mMarker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
    	mMarkerLy = (RelativeLayout) findViewById(R.id.id_maker_ly);
	}


	/*
     * ��Ҫ��ActionDrawerToggle��DrawerLayout��״̬ͬ��
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
    	super.onPostCreate(savedInstanceState);
    	//��ActionDrawerToggle��DrawerLayout��״̬ͬ��
    	//Ҳ��Ҫ��ActionDrawerToggle�е�drawerͼ�����ó��µ�
    	mDrawerToggle.syncState();
    }
    
    
    /*
     * ����Ļ��ת��ʱ�򣬶�Configuration���½�������
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    
    
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

    	if(position == 0){
    		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); //��ʾ������ͨ�ĵ�ͼ
    	}
    	else if(position == 1){
    		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE); //��ʾ�������ǵ�ͼ
    	}
    	else if(position == 2){
    		if(mBaiduMap.isTrafficEnabled()){ //�����ǰ��ʵʱ��ͨ״̬�Ǵ򿪵�
    			mBaiduMap.setTrafficEnabled(false); //�Ǿ͹ر���
    			Toast.makeText(this , "ʵʱ��ͨ�ѹر�" , Toast.LENGTH_SHORT).show();
    		}
    		else{
    			mBaiduMap.setTrafficEnabled(true); //�ǾͿ�����
    			Toast.makeText(this , "ʵʱ��ͨ�ѿ���" , Toast.LENGTH_SHORT).show();
    		}
    	}
    	else if(position == 3){ //�ص��ҵ�λ��
    		centerToMyLocation();
    	}
    	else if(position == 4){ //��ͨģʽ
    		mLocationMode = LocationMode.NORMAL;
    	}
    	else if(position == 5){ //����ģʽ
    		mLocationMode = LocationMode.FOLLOWING;
    	}
    	else if(position == 6){ //����ģʽ
    		mLocationMode = LocationMode.COMPASS;
    	}
    	else if(position == 7){ //��Ӹ�����
    		//�Զ���ķ�����������Ӹ�����
    		addOverlays(Info.infos);
    	}
    	
    	
    	/*
    	 * �������˵�֮�󣬵�ȻҪ�����Ĳ����Զ�������
    	 */
    	mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    
    
    /*
     * �Զ���ķ�����������Ӹ�����
     */
    private void addOverlays(List<Info> infos) {
    	//��������Ҫ��Ӹ������ˣ���������ȰѶ�λ��Щͼ�������
    	mBaiduMap.clear();
    	
    	LatLng latLng = null; //����һ����γ�ȱ���
    	
    	Marker marker  = null;
    	OverlayOptions options;
    	
    	for(Info info : infos){
    		//�õ�infos��ÿ��Ԫ�صľ�γ����Ϣ
    		latLng = new LatLng(info.getLatitude() , info.getLongtitude());
    		//��infosԪ����ÿ���洢��ͼ�꡾icon(mMarker)����ȡ������Ȼ�����ø��ǵ�ָ���ľ�γ�����꡾position(latLng)���ϣ�ͬʱָ��ͼ��Ĳ㼶��zIndex(5)��
    		//����һϵ��ָ����ָ���ǵ��ܿ��ƿ��أ��������OverlayOptions����ͼ�����
    		options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
    		//ע��˴���addOverlay�ͱ�������addOverlays�������������������ߵķ��붼�����ͼ��
    		marker = (Marker) mBaiduMap.addOverlay(options);
    		//������ʹ��marker֮ǰ��Ҫ�����洫��info��Ϣ����һ���ܱ�Ҫ
    		Bundle arg0 = new Bundle();
    		arg0.putSerializable("info" , info); //��ֵ��
    		marker.setExtraInfo(arg0);
    	}
    	
    	//��ת���ղ���ӵ���Щ���������ڵ�λ�ã�����鿴
    	MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
    	mBaiduMap.setMapStatus(msu);
		
	}//�Զ��巽��addOverlays()����
    


	private void initLocation() {
    	mLocationMode = LocationMode.NORMAL; //ģʽĬ������ͨģʽ
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
    	
    	mBaiduMap.setTrafficEnabled(false); //ʵʱ��ͨĬ�Ϲر�
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
			MyLocationConfiguration config = new MyLocationConfiguration(mLocationMode , true , mIconLocation);
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
