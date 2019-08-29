package com.casualweather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.casualweather.android.R;
import com.casualweather.android.db.City;
import com.casualweather.android.db.County;
import com.casualweather.android.db.Province;
import com.casualweather.android.util.HttpUtil;
import com.casualweather.android.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    private ProgressDialog progressDialog;//弹窗进度条
    private TextView titleText;
    private ImageView showFlag;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter adapter;//字符串适配器
    private List<String> dataList=new ArrayList<>();//数据列表

    private List<Province>provinceList;//省级列表
    private List<City>cityList;//市级列表
    private List<County>countyList;//县级列表

    //所选择的对象
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    //当前所选等级
    private int currentLevel;

    @Nullable
    @Override//创建视图
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);//获取choose_area布局
        titleText=(TextView)view.findViewById(R.id.title_text);
        showFlag=(ImageView)view.findViewById(R.id.show_flag);
        backButton=(Button)view.findViewById(R.id.back_button);
        listView=(ListView)view.findViewById(R.id.list_view);

        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);//将数据传入适配器
        listView.setAdapter(adapter);//列表显示适配器数据

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();//查询城市
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounty();//查询县城
                }else if(currentLevel==LEVEL_COUNTY){
                    String weatherId=countyList.get(position).getWeatherId();

                    if (getActivity()instanceof MainActivity){//判断该碎片是否在主活动中,在就正常执行
                        Intent intent=new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity()instanceof WeatherActivity){//不在就关闭侧滑菜单
                        WeatherActivity activity=(WeatherActivity)getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }

        });
        queryProvinces();
    }

    //查询并显示省份信息
    private void queryProvinces(){
        titleText.setText("中国");
        showFlag.setImageResource(R.drawable.flag2);
        backButton.setVisibility(View.GONE);//设置按钮不可见
        provinceList=LitePal.findAll(Province.class);//从数据库中查找数据
        if(provinceList.size()>0){//如果数据库中有省级数据则将数据存入dataList
            dataList.clear();
            for (Province province:provinceList){

                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();//适配器通知 数据发生变化
            listView.setSelection(0);//初始化选择项为0
            currentLevel=LEVEL_PROVINCE;//当前等级为省级

        }else {//如果数据库中没有省级数据，则从服务器上面查询
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    //查询并显示城市信息
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        showFlag.setImageResource(R.drawable.county);
        backButton.setVisibility(View.VISIBLE);
        cityList=LitePal.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else {
            int provinceCode=selectedProvince.getProvinceCode();//查询城市需要加上所属省份ID
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }

    //查询并显示县城信息
    private void queryCounty(){
        titleText.setText(selectedCity.getCityName());
        showFlag.setImageResource(R.drawable.county);
        backButton.setVisibility(View.VISIBLE);
        countyList=LitePal.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else {
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }

    //从服务器上查询数据
    private void queryFromServer(String address,final String type){
        showProgressDialog();//显示加载进度条
        HttpUtil.sendOkHttpRequest(address, new Callback() {

            @Override//服务器响应
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();//获取服务器返回的信息
                boolean result=false;
                if ("province".equals(type)){
                    result= Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result=Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }
            }

            @Override//服务器未响应
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败了，请再试一次吧！", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        });
    }


    //显示进度对话框
    private void showProgressDialog(){
        if (progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());//获取相应的活动，实例化进度条
            progressDialog.setTitle("加载中");
            progressDialog.setMessage("请稍等一下...");
        }
        progressDialog.show();
    }
    //关闭进度对话框
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

}
