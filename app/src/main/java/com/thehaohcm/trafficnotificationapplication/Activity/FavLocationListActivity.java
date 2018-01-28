package com.thehaohcm.trafficnotificationapplication.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.thehaohcm.trafficnotificationapplication.Adapter.FavoriteLocationAdapter;
import com.thehaohcm.trafficnotificationapplication.Interface.Retrofit.FavoriteLocationRepository;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.FavoriteLocationResult;
import com.thehaohcm.trafficnotificationapplication.R;
import com.thehaohcm.trafficnotificationapplication.controller.URLConnection;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by thehaohcm on 9/22/17.
 */

public class FavLocationListActivity extends AppCompatActivity {

    private ListView lvFavLocation;
    private FavoriteLocationAdapter adapter;
    private String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favlocation_list);

        this.phoneNumber=getIntent().getStringExtra("phoneNumber");

        initRetrofit();

        //get List
        lvFavLocation=(ListView) findViewById(R.id.lvFavLocation);
        lvFavLocation.setOnItemClickListener(onItemClick);
    }

    private AdapterView.OnItemClickListener onItemClick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FavLocationListActivity.this);
            builder.setTitle("Lựa chọn");
            builder.setItems(new CharSequence[]
                            {"Đến vị trí","Chỉnh sửa"},
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intent=new Intent();
                                    intent.putExtra("Lat",adapter.getItem(position).getLatitude());
                                    intent.putExtra("Lng",adapter.getItem(position).getLongitude());
                                    setResult(Activity.RESULT_OK,intent);
                                    finish();
                                    break;
                                case 1:
                                    Intent detail=new Intent(FavLocationListActivity.this, FavLocationDetailActivity.class);
                                    Bundle bundle=new Bundle();
                                    bundle.putSerializable("FAVLOCATION",adapter.getItem(position));
                                    detail.putExtras(bundle);
                                    startActivityForResult(detail,1000);
                                    break;
                            }
                        }
                    });
            builder.create().show();
        }
    };

    private void initRetrofit(){
        Retrofit.Builder builder=new Retrofit.Builder().baseUrl(URLConnection.getURLConnection()).addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=builder.build();

        FavoriteLocationRepository client=retrofit.create(FavoriteLocationRepository.class);

        Call<FavoriteLocationResult> call=client.getListFavoriteLocationResultByUser(phoneNumber);

        call.enqueue(new Callback<FavoriteLocationResult>() {
            @Override
            public void onResponse(Call<FavoriteLocationResult> call, Response<FavoriteLocationResult> response) {
                FavoriteLocationResult repos=response.body();

                adapter=new FavoriteLocationAdapter(FavLocationListActivity.this,0,(ArrayList)repos.getFavoriteLocations(),getLayoutInflater());
                lvFavLocation.setAdapter(adapter);
//                Log.i("reposfdasd",repos.toString());
//                Toast.makeText(MainActivity.this, "abc "+repos.getName(), Toast.LENGTH_SHORT).show();
//                listView.setAdapter(new GitHubRepoAdapter(MainActivity.this,repos));
            }

            @Override
            public void onFailure(Call<FavoriteLocationResult> call, Throwable t) {
                Toast.makeText(FavLocationListActivity.this, "Không thể tải về danh sách Địa điểm ưa thích", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                initRetrofit();
            }
        }
    }
}
