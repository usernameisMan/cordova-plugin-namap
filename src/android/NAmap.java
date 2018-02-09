package com.lenlee.plugin.namap;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;  
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.lenlee.plugin.namap.PluginUtil;

/**
 * Created by Administrator on 2017-10-11.  
 */  

public class NAmap extends CordovaPlugin {

    int resId = 0,mapId = 0;
    float screenW,screenH;
    FrameLayout layout = null;
    View mapView = null;
    MapView mMapView = null;
    AMap aMap = null;
    JSONArray touchAreas = null;
    JSONObject logicWebViewPixel = null;
    @Override  
    protected void pluginInitialize() {
        super.pluginInitialize();
    }  
  
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        final Activity activity = this.cordova.getActivity();
        Log.e("MainActivity", String.valueOf(args.getJSONArray(0)));
        if (action.equals("getMap")){
            initMap(args);
            JSONObject r = new JSONObject();
            callbackContext.success(r);
        }
        return true;
    }
    public void initMap(JSONArray args) throws JSONException {
        //第一组是触摸对象
        touchAreas = args.getJSONArray(0);
        //第二组是webView的逻辑分辨率
        logicWebViewPixel  = args.getJSONObject(1);
        //获取屏幕宽高
        screenW = Resources.getSystem().getDisplayMetrics().widthPixels/logicWebViewPixel.getInt("w");
        screenH = Resources.getSystem().getDisplayMetrics().heightPixels/logicWebViewPixel.getInt("h");
        Log.e("MainActivity","屏幕宽高比:"+screenW+" "+screenH);
        //动态 获取Layout 文件
        resId=PluginUtil.getAppResource(this.cordova.getActivity(),"amap_activity_main","layout");
        mapId=PluginUtil.getAppResource(this.cordova.getActivity(),"map","id");
        layout = (FrameLayout)webView.getView().getParent();
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        NAmap.this.cordova.getActivity().runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(cordova.getContext(),"主线程运行成功",Toast.LENGTH_LONG);
                                        webView.getView().setBackgroundColor(0);
                                        layout.addView(cordova.getActivity().getLayoutInflater().inflate(resId,null));
                                        mMapView = cordova.getActivity().findViewById(mapId);
                                        mMapView.onCreate(new Bundle());
                                        aMap = mMapView.getMap();
                                        toast.show();
                                        initListeners(); //实现监听触摸事件
                                        webView.getView().bringToFront();
                                        initMapUtil();
                                    }
                                }
                        );
                    }
                }
        ).start();
    }

    public void initListeners(){
        webView.getView().setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                touchSelected("webView",event);
                //把触摸分发给地图的view
                if(judgeTouch(event)){
                    return  true;
                }else {
                    mMapView.dispatchTouchEvent(event);
                    return false;
                }
            }
        });
        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener(){
            @Override
            public void onTouch(MotionEvent event) {
                touchSelected("map",event);
            }
        });
    }

    public void touchSelected(String type,MotionEvent event){
        Log.e("MainActivity","发生触摸===="+type+"====x:"+((int)event.getRawX())/screenW+"======Y:"+((int)event.getRawY())/screenH+"=======");
    }


    public void initMapUtil(){
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
    }

    public boolean judgeTouch(MotionEvent event){
        boolean x=true,y=true;
        //转换比例
        int X,Y,X1,Y1;
        //判断坐标位置
        for (int i = 0; i <touchAreas.length() ; i++) {
            try {
                JSONObject  pdata = (JSONObject)touchAreas.get(i);
//                PluginUtil.transformNative2webViewPixel();

                 X = pdata.getInt("X");
                 Y = pdata.getInt("Y");
                 X1 = pdata.getInt("X1");
                 Y1 = pdata.getInt("Y1");
                 Log.e("MainActivity","X:"+X+" Y:"+Y+" X1:"+X1+" Y1:"+Y1);
                 x = (((int)event.getRawX())/screenW)>X && (((int)event.getRawX())/screenW)<X1;
                 y = (((int)event.getRawY())/screenH)>Y && (((int)event.getRawY())/screenH)<Y1;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("MainActivity","x:"+x+" y"+y);
        return  x&&y;
    }
}