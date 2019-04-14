package com.example.note.justdo.Amap;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.note.justdo.Amap.overlay.PoiOverlay;
import com.example.note.justdo.Amap.util.Constants;
import com.example.note.justdo.Amap.util.ToastUtil;
import com.example.note.justdo.App;
import com.example.note.justdo.R;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import java.util.List;

public class NewMap extends AppCompatActivity implements AMap.OnMyLocationChangeListener,View.OnClickListener,
        PoiSearch.OnPoiSearchListener,GeocodeSearch.OnGeocodeSearchListener, SeekBar.OnSeekBarChangeListener {

    //private MapView mapView;//定义一个容器
    AMap amap;//定义地图对象
    MyLocationStyle myLocationStyle;//定义蓝点类型
    Marker marker;
    private String mKeyWords = "";// 要输入的poi搜索关键字
    private ProgressDialog progDialog = null;// 搜索时进度条

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 1;
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    //声明搜索框
    private TextView mKeywordsTextView;
    //声明一个marker
    private Marker mPoiMarker;
    //声明搜索框的叉按钮
    private ImageView mCleanKeyWords;
    //声明一个相机处理对象
    CameraUpdateFactory cameraUpdateFactory;
    //GeocodeSearch geocodeSearch;
    //声明字符串“城市”
    String city;
    public static final int REQUEST_CODE = 100;
    public static final int RESULT_CODE_INPUTTIPS = 101;
    public static final int RESULT_CODE_KEYWORDS = 102;
    //int totalMarker;
    //int WRITE_COARSE_LOCATION_REQUEST_CODE;
    //声明一个返回当前位置的按钮
    Button DW;
    //MapFragment mapFragment;
    //声明我的位置
    LatLng mylatlng;
    //声明一个圆
    Circle circle;
    //声明一个seekbar
    private SeekBar seekBar;
    //声明默认半径
    private int RADIUS = 1000;
    private LatLng position;//Marker的position
    private String place;//marker的位置名称
    public App myApp;
    String message;

    private static final int LOCATION_CODE = 1;
    private LocationManager lm;//【位置管理】

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //绑定布局
        //postion权限
        requestPosition();
        setContentView(R.layout.amap);
        //给定位按钮绑定id
        DW = findViewById(R.id.DW);
        //设置定位按钮的点击监听
        DW.setOnClickListener(this);
        //给seekBar绑定id
        seekBar = findViewById(R.id.seekBar);
        //设置seekbar监听
        seekBar.setOnSeekBarChangeListener(this);
        //设置seekbar不可见
        seekBar.setVisibility(View.INVISIBLE);
        //初始化地图控件
        // mapView = (MapView) findViewById(R.id.map);
        //必须要写
        //LixianMap = (Button) findViewById(R.id.lixianmap);
        //点击事件监听，在下方的onclick方法里
        //LixianMap.setOnClickListener(this);
        //super.onCreate(savedInstanceState);
        // totalMarker = 0;
        //mListView = (ListView) findViewById(R.id.listview);
        //mAdapter = new ArrayAdapter(NewMap.this, android.R.layout.simple_list_item_1, data);
        //mListView.setAdapter(mAdapter);
        //mListView.setTextFilterEnabled(true);
        //mSearchView = (SearchView) findViewById(R.id.searview);
        //mSearchView.setSubmitButtonEnabled(true);//搜索框展开时显示提交按钮
        //mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        //    @Override
        //   public boolean onQueryTextSubmit(String query) {
        //      return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                mAdapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//        //搜索图标按钮的点击事件
//        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(NewMap.this, "打开搜索框", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//        //搜索框内容变化监听
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {//点击提交按钮时
//                Toast.makeText(NewMap.this, "Submit---提交", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {//搜索框内容变化时
//                if (!TextUtils.isEmpty(newText)) {
////              mListView.setFilterText(newText);
//                    mAdapter.getFilter().filter(newText);
//                } else {
//                    mListView.clearTextFilter();
//                }
//                return true;
//            }
//        });
//
//        //搜索框展开时点击叉叉按钮关闭搜索框的点击事件
//        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                Toast.makeText(NewMap.this, "关闭搜索框", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
        //搜索框叉绑定id
        mCleanKeyWords = (ImageView) findViewById(R.id.clean_keywords);
        //设置监听
        mCleanKeyWords.setOnClickListener(this);
        //搜索文本框绑定id
        mKeywordsTextView = (TextView) findViewById(R.id.main_keywords);
        //点击监听
        mKeywordsTextView.setOnClickListener(this);

        mKeyWords = "";
        //初始化地图
        map();
        myApp = (App) this.getApplication();


    }

    //显示进度框
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + mKeyWords);
        progDialog.show();
    }

    //加载地图并设置监听
    public void map() {
        //如果没有地图创建地图
        if (amap == null) {
            amap = ((SupportMapFragment) this.getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            //放大到15级缩放
            amap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        myLocationStyle = new MyLocationStyle();
        //定义蓝点样式
        myLocationStyle.myLocationType((MyLocationStyle.LOCATION_TYPE_LOCATE));
         amap.setMyLocationStyle(myLocationStyle);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        amap.setMyLocationEnabled(true);
        myLocationStyle.showMyLocation(false);
        //设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。

        //设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。

        amap.setOnMyLocationChangeListener(this);
        //长按监听，长按会显示marker
        amap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                //定义当前位置
                // LatLng mylatlng2;
                //如果已经存在圆则删除圆
                if (circle != null) {
                    circle.remove();
                }
                //给我的位置赋值
                // mylatlng2 = new LatLng(amap.getMyLocation().getLatitude(), amap.getMyLocation().getLongitude());
                //获取选择位置与当前位置的距离
                //float dis = AMapUtils.calculateLineDistance(mylatlng2, latLng);
                if (marker == null) {

                    //显示距离当前位置的直线距离
                    marker = amap.addMarker(new MarkerOptions().position(latLng).title(""));
                } else {
                    marker.destroy();
                    //marker = amap.addMarker(new MarkerOptions().position(latLng).title("").snippet("半径：" + RADIUS+"米."));
                    marker = amap.addMarker(new MarkerOptions().position(latLng).title(""));
                    //显示距离当前位置的直线距离
                    // marker = amap.addMarker(new MarkerOptions().position(latLng).title("").snippet("直线距离：" + dis));
                }
                //位置数据回调
                final GeocodeSearch geocodeSearch = new GeocodeSearch(getApplicationContext());
                geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                    @Override
                    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                        if (i == 0) {
                            System.out.println("数据回调出错\n");
                        } else {
                            //获取附近地点名称
                            List<PoiItem> poiItemList;
                            poiItemList = regeocodeResult.getRegeocodeAddress().getPois();
                            // place=getNearestName(poiItemList, latLng) ;
                            marker.setTitle(regeocodeResult.getRegeocodeAddress().getDistrict() + getNearestName(poiItemList, latLng) + "附近");
                        }
                    }

                    @Override
                    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                    }
                });
                LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
                RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500, GeocodeSearch.AMAP);
                geocodeSearch.getFromLocationAsyn(query);
            }
        });
        amap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            //marker的点击监听
            public boolean onMarkerClick(Marker marker) {
                //获取marker当前位置
                position = marker.getPosition();
                place = marker.getTitle();
                //添加一个默认的圆，奴国没有圆则创建圆，有圆则重新创建圆
                if (circle == null) {
                    circle = amap.addCircle(new CircleOptions().
                            center(position).//圆心
                            radius(RADIUS).//半径
                            fillColor(Color.argb(100, 1, 1, 1)).//圆颜色及透明度
                            strokeColor(Color.argb(255, 1, 1, 1)).//圆边缘颜色及透明度
                            strokeWidth(1));//边缘宽度
                    //marker.setSnippet("半径：" + RADIUS+"米.");
                } else {
                    if (position != circle.getCenter()) {
                        circle.remove();
                        circle = amap.addCircle(new CircleOptions().
                                center(position).
                                radius(RADIUS).
                                fillColor(Color.argb(100, 1, 1, 1)).
                                strokeColor(Color.argb(255, 1, 1, 1)).strokeWidth(1));
                        // marker.setSnippet("半径：" + RADIUS+"米.");
                    }
                }
                //设置默认圆为不可见
                circle.setVisible(false);
                if (!marker.isInfoWindowShown()) {
                    //显示信息及圆，seekbar
                    circle.setVisible(true);
                    marker.showInfoWindow();
                    seekBar.setVisibility(View.VISIBLE);
                } else {
                    //取消显示信息及圆，seekbar等
                    circle.setVisible(false);
                    marker.hideInfoWindow();
                    seekBar.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });
    }

    //隐藏进度框
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    //开始进行poi搜索
    protected void doSearchQuery(String keywords) {
        showProgressDialog();// 显示进度框
//        GeocodeSearch geocodeSearch2 = new GeocodeSearch(getApplicationContext());
//        geocodeSearch2.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
//            @Override
//            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
//                if (i == 0) {
//                    System.out.println("i=0!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
//                } else {
//                    city=regeocodeResult.getRegeocodeAddress().getCity();
//                }
//            }
//
//            @Override
//            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
//
//            }
//        });
//        LatLonPoint latLonPoint = new LatLonPoint(amap.getCameraPosition().target.latitude, amap.getCameraPosition().target.longitude);
//        RegeocodeQuery query2 = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
//        geocodeSearch2.getFromLocationAsyn(query2);

        currentPage = 1;
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        Log.d("TAG", "确定" + city);
        query = new PoiSearch.Query(keywords, "", city);
        // 设置每页最多返回多少条poiitem
        query.setPageSize(10);
        // 设置查第一页
        query.setPageNum(currentPage);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(NewMap.this, infomation);

    }


    /**
     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        amap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(amap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        //showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(NewMap.this,
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil.show(NewMap.this,
                        R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem item, int rCode) {
        // TODO Auto-generated method stub

    }

    /**
     * 输入提示activity选择结果后的处理逻辑
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* super.onActivityResult(requestCode, resultCode, data);
        //LogUtil.d("SaleMapActivity","sdjflksdfjdlskf");
                // LogUtil.d("SaleMapActivity","onActivityResult===========");
                Bundle bundle = data.getBundleExtra("searAddr");
                GeocodeAddress address = bundle.getParcelable("addr");
                //根据传回来的地址坐标获取该地点的位置详细信息
                //通过 RegeocodeQuery(LatLonPoint point, float radius, java.lang.String latLonType) 设置查询参数
                RegeocodeQuery rQuery = new RegeocodeQuery(address.getLatLonPoint(),200,GeocodeSearch.AMAP);
                //调用 GeocodeSearch 的 getFromLocationAsyn(RegeocodeQuery regeocodeQuery) 方法发起请求。
                //通过回调接口 onRegeocodeSearched 解析返回的结果
                geocodeSearch.getFromLocationAsyn(rQuery);*/
        if (resultCode == RESULT_CODE_INPUTTIPS && data
                != null) {
            amap.clear();
            Tip tip = data.getParcelableExtra(Constants.EXTRA_TIP);
            if (tip.getPoiID() == null || tip.getPoiID().equals("")) {
                doSearchQuery(tip.getName());
            } else {
                addTipMarker(tip);
            }
            mKeywordsTextView.setText(tip.getName());
            if (!tip.getName().equals("")) {
                mCleanKeyWords.setVisibility(View.VISIBLE);
            }
        } else if (resultCode == RESULT_CODE_KEYWORDS && data != null) {
            amap.clear();
            String keywords = data.getStringExtra(Constants.KEY_WORDS_NAME);
            if (keywords != null && !keywords.equals("")) {
                doSearchQuery(keywords);
            }
            mKeywordsTextView.setText(keywords);
            if (!keywords.equals("")) {
                mCleanKeyWords.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 用marker展示输入提示list选中数据
     *
     * @param tip
     */
    private void addTipMarker(Tip tip) {
        if (tip == null) {
            return;
        }
        marker = amap.addMarker(new MarkerOptions());
        LatLonPoint point = tip.getPoint();
        if (point != null) {
            LatLng markerPosition = new LatLng(point.getLatitude(), point.getLongitude());
            marker.setPosition(markerPosition);
            amap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 15));
        }
        marker.setTitle(tip.getName());
        //place=tip.getName();
        marker.setSnippet(tip.getAddress());
    }
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }

    @Override
    public void onMyLocationChange(Location location) {
        mylatlng = new LatLng(location.getLatitude(), location.getLongitude());
        float dis = AMapUtils.calculateLineDistance(mylatlng, marker.getPosition());
        marker.setSnippet("直线距离：" + String.valueOf(dis));
    }

    //获取最近的地点名称
    public String getNearestName(List<PoiItem> poiItemList, LatLng targetLocation) {
        double minDis = 500, nowDis;
        String ret = "";
        for (int i = 0; i <= poiItemList.size() - 1; i++) {
            PoiItem poiItem;
            poiItem = poiItemList.get(i);
            LatLng poilatlng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
            nowDis = AMapUtils.calculateLineDistance(targetLocation, poilatlng);
            if (nowDis < minDis) {
                minDis = nowDis;
                ret = poiItem.toString();
            }
        }
        return ret;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_keywords:
                GeocodeSearch geocodeSearch3 = new GeocodeSearch(getApplicationContext());
                // Log.d("TAG","1");
                geocodeSearch3.setOnGeocodeSearchListener(this);
                //  Log.d("TAG","3");
                LatLng mylatlng = new LatLng(amap.getMyLocation().getLatitude(), amap.getMyLocation().getLongitude());
                LatLonPoint latLonPoint = new LatLonPoint(mylatlng.latitude, mylatlng.longitude);
                // Log.d("TAG","4");
                RegeocodeQuery query3 = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
                geocodeSearch3.getFromLocationAsyn(query3);
                // Log.d("TAG","5");
                break;
            case R.id.DW:
                //清初所有marker
                amap.clear();
                if (circle != null) {
                    circle = null;
                }
                //定义我的位置
                LatLng mylatlng1 = new LatLng(amap.getMyLocation().getLatitude(), amap.getMyLocation().getLongitude());
                marker = amap.addMarker(new MarkerOptions().position(mylatlng1).title("我的位置"));
                //移动到我的位置
                amap.moveCamera(cameraUpdateFactory.changeLatLng(mylatlng1));
                //缩放至15
                amap.moveCamera(CameraUpdateFactory.zoomTo(15));
                //删除圆
                if (circle != null) {
                    circle.remove();
                }
                seekBar.setVisibility(View.INVISIBLE);
                break;
            case R.id.clean_keywords:
                mKeywordsTextView.setText("");
                amap.clear();
                mCleanKeyWords.setVisibility(View.GONE);
            default:
                break;
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
//        this.getSupportFragmentManager().findFragmentById(R.id.map).onDestroy();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
//        this.getSupportFragmentManager().findFragmentById(R.id.map).onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
//        this.getSupportFragmentManager().findFragmentById(R.id.map).onPause();
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        this.getSupportFragmentManager().findFragmentById(R.id.map).onSaveInstanceState(outState);
    }

    @Override
    //GeocodeSearch.OnGeocodeSearchListener
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (i == 0) {
            System.out.println("i=0!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
        } else {
            city = regeocodeResult.getRegeocodeAddress().getCity();
            Log.d("TAG", "候选2" + city);
            Intent intent = new Intent(this, InputTipsActivity.class);
            Log.d("TAG", "候选" + city);
            //  Log.d("TAG","6");
            intent.putExtra("activityMain", "数据来自activityMain");
            intent.putExtra("city", city);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    //GeocodeSearch.OnGeocodeSearchListener
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    //SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        circle.setRadius(i);
        RADIUS = i;
    }

    @Override
    //SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    //SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        // marker.setSnippet("半径："+RADIUS+"米。");
    }

    //    @Override
//    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
//        if (rCode == 1000) {
//            if (result != null && result.getRegeocodeAddress() != null
//                    && result.getRegeocodeAddress().getCity() != null) {
//
//                city = result.getRegeocodeAddress().getCity();
//                Toast.makeText(this, "" + city, Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    @Override
//    public void onGeocodeSearched(GeocodeResult result, int rCode) {
//    }
    public void onBackPressed() {
        if (circle == null) {
            super.onBackPressed();
        } else if (circle.isVisible() == false) {
            super.onBackPressed();
        } else {
            //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
            AlertDialog.Builder builder = new AlertDialog.Builder(NewMap.this);
            //    设置Title的内容
            builder.setTitle("设置位置提醒");
            //    设置Content来显示一个信息
            LatLng mylatlng2 = new LatLng(amap.getMyLocation().getLatitude(), amap.getMyLocation().getLongitude());
            float distance = AMapUtils.calculateLineDistance(mylatlng2, position);
            //解决title为空的问题
//            if (marker.getTitle() == null) {
//                marker.setTitle("我的位置");
//            }
            if (distance <= circle.getRadius()) {
                builder.setMessage("确定在离开" + marker.getTitle() + circle.getRadius() + "米后提醒您吗？");
                message = "离开" + " " + marker.getTitle() + circle.getRadius() + "米";
                //    设置一个PositiveButton
            } else {
                builder.setMessage("确定在进入" + place + circle.getRadius() + "米后提醒您吗？");
                message = "进入" + " " + marker.getTitle() + circle.getRadius() + "米";
            }
            //    设置一个PositiveButton
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    myApp.setLatlng(position);
                    myApp.setRadius(circle.getRadius());
                    myApp.setPlace(message);
                    NewMap.super.onBackPressed();
                }
            });
            //    设置一个NegativeButton
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    myApp.setRadius(0);
                    NewMap.super.onBackPressed();
                }
            });
            //    设置一个NeutralButton
//                builder.setNeutralButton("忽略", new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        Toast.makeText(MainActivity.this, "neutral: " + which, Toast.LENGTH_SHORT).show();
//                    }
//                });
            //    显示出该对话框
            builder.show();
        }
//            Log.d("TAG","半径"+circle.getRadius());
//           myApp.setLatlng(marker.getPosition());
//    myApp.setRadius(circle.getRadius());
//    myApp.setPlace(place);
//            Log.d("TAG","地点"+place);
//           // Toast.makeText(NewMap.this, place+circle.getRadius()+"范围内", Toast.LENGTH_SHORT).show();
    }

//    public void quanxian(){
//        lm = (LocationManager) NewMap.this.getSystemService(NewMap.this.LOCATION_SERVICE);
//        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        if (ok) {//开了定位服务
//            if (ContextCompat.checkSelfPermission(NewMap.this, Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                Log.e("BRG","没有权限");
//                // 没有权限，申请权限。
//                // 申请授权。
//                ActivityCompat.requestPermissions(NewMap.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
////                        Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
//
//            } else {
//
//            }
//        } else {
//            Log.e("BRG","系统检测到未开启GPS定位服务");
//            Toast.makeText(NewMap.this, "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent();
//            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivityForResult(intent, 1315);
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case LOCATION_CODE: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // 权限被用户同意。
//
//                } else {
//                    // 权限被用户拒绝了。
//                    Toast.makeText(NewMap.this, "定位权限被禁止，相关地图功能无法使用！",Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }
//    }
    private void requestPosition() {
        if (PermissionsUtil.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)&&PermissionsUtil.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //是否有位置权限
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {

                    Toast.makeText(NewMap.this, "已获取位置权限", Toast.LENGTH_LONG).show();
                   // onCreate(null);
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                    Toast.makeText(NewMap.this, "未获取位置权限", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            }, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION);

        }
    }
}
