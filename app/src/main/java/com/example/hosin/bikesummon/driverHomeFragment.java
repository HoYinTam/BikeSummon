package com.example.hosin.bikesummon;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DriverHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DriverHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverHomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private HttpUtil httpUtil=new HttpUtil();
    private static Handler handler;

    //Map
    private TextureMapView mapView=null;
    private LocationClient locationClient=null;
    boolean isFirst=true;

    private BDLocationListener locationListener= new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //Log.d("test", String.valueOf(bdLocation.getLatitude()));
            // Log.d("test", String.valueOf(bdLocation.getLongitude()));
            if(isFirst){
                LatLng latLng=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(latLng);
                mapView.getMap().setMapStatus(update);//locate where you are on the map
                isFirst=false;
            }
            MyLocationData data=new MyLocationData.Builder().accuracy(100).latitude(bdLocation.getLatitude()).longitude(bdLocation.getLongitude()).build();
            mapView.getMap().setMyLocationData(data);
            mapView.getMap().setMyLocationEnabled(true);
            mapView.getMap().setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
            try {
                final JSONObject param = new JSONObject();
                param.put("event", 0);
                param.put("type", "driver");
                param.put("ID", getActivity().getIntent().getIntExtra("userID", 3));
                param.put("latitude", bdLocation.getLatitude());
                param.put("longitude", bdLocation.getLongitude());
                Log.d("json", param.toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String result;
                            result= httpUtil.doPost("/event", param).toString();
                            if(result!=null){
                                Message message = new Message();
                                message.what = 0;
                                message.obj = result;
                                handler.sendMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private OnFragmentInteractionListener mListener;

    public DriverHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriverHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverHomeFragment newInstance(String param1, String param2) {
        DriverHomeFragment fragment = new DriverHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //Location
        locationClient=new LocationClient(getActivity().getApplicationContext());
        locationClient.registerLocationListener(locationListener);
        initLocation();
        locationClient.start();

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0){
                    try {
                        JSONObject res = new JSONObject(msg.obj.toString());
                        //TODO:update orderlist
                        Log.d("json",res.toString());
                        mListener.onGetInfo(res.getJSONArray("orderList"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                super.handleMessage(msg);
            }
        };

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    private void initLocation() {
        LocationClientOption option=new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        locationClient.setLocOption(option);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_driver_home, container, false);
        mapView=(TextureMapView) view.findViewById(R.id.driverMapView);
        mapView.getMap().setMyLocationEnabled(true);
        mapView.getMap().setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
        mapView.showScaleControl(false);
        mapView.showZoomControls(false);
        mListener.onGetMapView(mapView);
        return view;
    }



    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onGetInfo(JSONArray orders);
        void onGetMapView(TextureMapView mapView);
    }
}
