package xyz.jayadev.marvel;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.Timestamp;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gLayout;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    static View.OnClickListener myOnClickListener;
    private static final int PERMISSION_REQUEST_CODE = 1;
    int count;
    boolean running = false;
    int offset = 0;
    int ref = 0;
    boolean search = false;
    boolean searchViewOn = false;

    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> images = new ArrayList<String>();
    ArrayList<Integer> ids = new ArrayList<Integer>();

    Context mContext;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        myOnClickListener = new MyOnClickListener(this);
        gLayout = new GridLayoutManager(MainActivity.this, 2);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gLayout);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);
        if(!checkPermission())
            requestPermission();
    }

    RecyclerView.OnScrollListener mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView,
                                         int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = gLayout.getChildCount();
            int totalItemCount = gLayout.getItemCount();
            int firstVisibleItemPosition = gLayout.findFirstVisibleItemPosition();
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= 10 && totalItemCount == adapter.getItemCount()) {
//                Log.d("Alpha", "conditions matched on scroll total:" + totalItemCount + ", get items" + adapter.getItemCount());
                if (totalItemCount > ref) {
                   if(!search) {
                       ref = totalItemCount;
//                       Log.d("Alpha", "Not running" + totalItemCount + ", get items" + adapter.getItemCount());
//                       Log.d("Alpha", "offset:" + offset);
                       offset = totalItemCount;
                       apicall(offset, null);
                   }
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setQueryHint("Iron man");
        }
        if (searchView != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
//                    Log.i("onQueryTextChange", newText);
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
//                    Log.i("onQueryTextSubmit", query);
                    searchViewOn = true;
                    apicall(0, query);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            pos(v);
        }

        private void pos(View v) {
            int selectedItemPosition = recyclerView.getChildPosition(v);
//            Log.d("alpha", selectedItemPosition + "");
        }
    }

    public void apicall(int offst, String nameStart) {

        running = true;
        if (Utils.isAvailable()) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            int time = (int) (System.currentTimeMillis());
            Timestamp tsTemp = new Timestamp(time);
            String ts = tsTemp.toString();
            params.put("apikey", Config.PUBLIC_KEY);
            params.put("ts", "1");
            params.put("hash", "a887e1e1659d059357b71bf9e9361e67");
            params.put("limit", 10);
            if (nameStart != null) {
                params.put("nameStartsWith", nameStart);
                search = true;
            }
            params.put("offset", offst);
            client.get(Config.URL_server, params, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
//                   mD =  new MaterialDialog.Builder(mContext)
//                            .title(R.string.progress_dialog)
//                            .content(R.string.please_wait)
//                            .progress(true, 0)
//                            .show();
                    Log.d("Alpha:apicall", "started.." + search);

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    String resp = new String(response);

                    try {
                        JSONObject jo = new JSONObject(resp);
                        JSONObject data = jo.getJSONObject("data");
                        JSONArray results = data.getJSONArray("results");
                        count = results.length();
                        if (search) {
                            Log.d("alpha", "search true");
                            names.clear();
                            ids.clear();
                            images.clear();
                        }
                        for (int i = 0; i < count; i++) {
                            JSONObject jsn = results.getJSONObject(i);
                            JSONObject img = jsn.getJSONObject("thumbnail");
                            names.add(jsn.getString("name"));
                            ids.add(jsn.getInt("id"));
                            images.add(img.getString("path") + ".jpg");
                        }
//                        for (int i = 0; i < names.size(); i++) {
//                            Log.d("Alpha:apicall", "started:" + names.get(i));
//                        }

                    } catch (Exception e) {
//                        Log.d("Alpha:apicall", "catched");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
//                    Log.d("Alpha:apicall", statusCode + "" + errorResponse);
//                    mD.dismiss();
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Unable to connect to server.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }

                @Override
                public void onFinish() {
                    // Completed the request (either success or failure)
//                    mD.dismiss();
                    int size = ids.size();
//                    Log.d("Alpha:apicall", "done" + ids.size());
                    String[] _images = images.toArray(new String[size]);
                    String[] _names = names.toArray(new String[size]);
                    Integer[] _ids = ids.toArray(new Integer[size]);

                    data = new ArrayList<DataModel>();
                    for (int i = 0; i < size; i++) {
                        data.add(new DataModel(
                                _names[i],
                                _ids[i],
                                _images[i]
                        ));

                    }
                    adapter = new CustomAdapter(data, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.scrollToPosition(offset);
//                    Log.d("Alpha", "count:" + adapter.getItemCount());
                    search = false;
                    running = false;
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    // Progress notification
//                    Log.d("alpha", "shitting");
                }
            });

        } else {
//            Log.d("Alpha:else", "elsecase");
            Realm realm = Realm.getInstance(this);
            RealmQuery<DataModel> query = realm.where(DataModel.class);
            RealmResults<DataModel> result = query.findAll();
//            Log.d("Alpha:realm", result.size() + "");
            data = new ArrayList<DataModel>();
            for (int i = 0; i < result.size(); i++) {
                DataModel u = result.get(i);
                data.add(new DataModel(
                        u.getName(),
                        u.getId(),
                        u.getImage()
                ));
//                Log.d("Alpha", "realm" + u.getId()+u.getName()+u.getImage());
            }
            adapter = new CustomAdapterCache(data, MainActivity.this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {
        if (searchViewOn) {
            apicall(0, null);
            searchViewOn = false;
        } else {
            super.onBackPressed();
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            apicall(0,null);
            return true;

        } else {

            return false;

        }
    }

    private void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            Toast.makeText(mContext,"Storage Permission in required for this app",Toast.LENGTH_SHORT).show();
        } else {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    Toast.makeText(mContext,"granted",Toast.LENGTH_SHORT).show();
                    apicall(0,null);

                } else {
                    requestPermission();
                    Toast.makeText(mContext,"permission denied",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
