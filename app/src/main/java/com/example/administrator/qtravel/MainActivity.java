package com.example.administrator.qtravel;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.example.administrator.qtravel.adapter.MainRecyclerViewAdapter;
import com.example.administrator.qtravel.databinding.ActivityMainBinding;
import com.example.administrator.qtravel.databinding.NavHeaderBinding;
import com.example.administrator.qtravel.global.MyApplication;;
import com.example.administrator.qtravel.loader.GlideImageLoader;
import com.example.administrator.qtravel.model.Main;
import com.example.administrator.qtravel.model.MainRecyclerView;
import com.example.administrator.qtravel.service.LocationService;
import com.example.administrator.qtravel.ui.LocationActivity;
import com.example.administrator.qtravel.ui.nav.LoginActivity;
import com.example.administrator.qtravel.ui.WebViewActivity;
import com.example.administrator.qtravel.ui.nav.NavAboutActivity;
import com.example.administrator.qtravel.ui.nav.NavHomePageActivity;
import com.example.administrator.qtravel.ui.search.FrendSearchActivity;
import com.example.administrator.qtravel.ui.search.PictureSearchActivity;
import com.example.administrator.qtravel.ui.search.VenueSearchActivity;
import com.example.administrator.qtravel.viewModel.ViewModelMain;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_ALBUM = 2;

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private Banner banner;
    //头像
    private ImageView imageView;
    //当前时间
    private double mExitTime;
    //头像图片文件
    private File mImageFile;
    //裁剪头像传的参数
    public static final String IMAGE_UNSPECIFIED = "image/*";

    private LocationClient mLocationClient;
    MainRecyclerViewAdapter mainRecyclerViewAdapter;

    public static boolean isLogin = false;
    public static String local;

    private List<String> pictures = new ArrayList<>();
    private List<MainRecyclerView> messages = new ArrayList<>();

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0X1112:
                    //把新的图片地址加载到Banner
                    banner.update(pictures);
                    //下拉刷新控件隐藏
                    binding.include.swipeRefresh.setRefreshing(false);
                    if (pictures.size()<2){
                        initBanner();
                    }
                    break;
                case 0X1113:
                    mainRecyclerViewAdapter.notifyDataSetChanged();
                    closeProgressBar();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ViewModelMain viewModelMain = new ViewModelMain(this, binding);
        init();
        initNavView();
        //获取权限和位置
        getPersimmilns();
        initBanner();
        initRecyclerView();
    }

    public void init(){
        Toolbar toolbar = binding.include.toolbar;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //将own图标放在xxhdpi分辨率的文件下 大小正好
        actionBar.setHomeAsUpIndicator(R.drawable.own);
        drawerLayout = binding.drawerLayout;
        //加载动画
        showProgressBar();
        //隐藏状态栏
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //给自定义view设置高度 不然低版本状态栏会为空
        if(Build.VERSION.SDK_INT <= 19){
            double statusBarHeight = Math.ceil(25 * MyApplication.getContext().getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams s = (LinearLayout.LayoutParams)binding.include.customView.getLayoutParams();
            s.height = (int)statusBarHeight;
            binding.include.customView.setLayoutParams(s);
            binding.include.customView.setBackgroundColor(getResources().getColor(R.color.background));
        }
        //给头像加监听
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.nav_header, binding.navView);
        imageView = view.findViewById(R.id.icon_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLogin) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("选择照片")
                            .setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        selectCamera();
                                    } else {
                                        selectAlbum();
                                    }
                                }
                            })
                            .create()
                            .show();
                }
            }
        });
        if (isLogin)
            imageView.setImageResource(R.drawable.touxiang);
        //recyclerview初始化
        RecyclerView recyclerView = binding.include.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MainRecyclerView mainRecyclerView = new MainRecyclerView("kjl", "k", "kj", "kldfj");
        messages.add(mainRecyclerView);
        mainRecyclerViewAdapter = new MainRecyclerViewAdapter(messages);
        recyclerView.setAdapter(mainRecyclerViewAdapter);
        //将recyclerview第一个和第二个item设置为banner和按钮行
        View header = LayoutInflater.from(this).inflate(R.layout.activity_main_banner, recyclerView, false);
        mainRecyclerViewAdapter.setmHeaderView(header);
        View button = LayoutInflater.from(this).inflate(R.layout.activity_main_button, recyclerView, false);
        mainRecyclerViewAdapter.setmButtonView(button);
        //启动banner
        banner = (Banner)header;
        banner.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MyApplication.H / 4));
        List<Integer> list1 = new ArrayList<>();
        list1.add(null);
        banner.setImages(list1)
                .setImageLoader(new GlideImageLoader())
                .setBannerAnimation(Transformer.DepthPage)
                .start();
        //下拉更新操作
        binding.include.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.include.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initBanner();
            }
        });
    }

    public void initNavView(){
        binding.navView.setCheckedItem(R.id.nav_own);
        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_own:
                        Intent intent = new Intent(MyApplication.getContext(), NavHomePageActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_about:
                        Intent intent2 = new Intent(MyApplication.getContext(), NavAboutActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.nav_feedback:
                        Intent intent4 = new Intent(MyApplication.getContext(), WebViewActivity.class);
                        intent4.putExtra("Url", "https://github.com/autumnLei/QTravel/issues/new");
                        intent4.putExtra("Title", "QTravel反馈页面");
                        startActivity(intent4);
                        break;
                    case R.id.nav_quit:
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.linear01:
                Intent intent1 = new Intent(this, VenueSearchActivity.class);
                intent1.putExtra("local", local);
                Log.d("789", "Login: " + local);
                startActivity(intent1);
                break;
            case R.id.linear02:
                Intent intent2 = new Intent(this, PictureSearchActivity.class);
                startActivity(intent2);
                break;
            case R.id.linear03:
                Intent intent3 = new Intent(this, FrendSearchActivity.class);
                startActivity(intent3);
                break;
            case R.id.linear04:
                Toast.makeText(this, "暂未开放", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
               drawerLayout.openDrawer(GravityCompat.START);
               break;
            case R.id.search:
                Intent intent2 = new Intent(this, WebViewActivity.class);
                intent2.putExtra("Url", "https://www.baidu.com/");
                intent2.putExtra("Title", "百度首页");
                startActivity(intent2);
                break;
            case R.id.local:
                Intent intent = new Intent(this, LocationActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //1.点击返回键条件成立
        if(keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            //2.点击的时间差如果大于2000，则提示用户点击两次退出
            if(System.currentTimeMillis() - mExitTime > 2000) {
                //3.保存当前时间
                mExitTime  = System.currentTimeMillis();
                //4.提示
                Toast.makeText(this, "两次点击退出应用", Toast.LENGTH_SHORT).show();
            } else {
                //5.点击的时间差小于2000，退出。
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 根据是否登录设置头像
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);//must store the new intent unless getIntent() will return the old one
        //判断是否登录或注册 现在是singletask模式 哎 不能放oncreate里了
        Intent intent1 = getIntent();
        String login = intent1.getStringExtra("login");
        if (login!=null) {
            Log.d("login", "onNewIntent: "+"已登录"+login);
            imageView.setImageResource(R.drawable.touxiang);
            isLogin = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CAMERA:
                imageView.setImageURI(Uri.fromFile(mImageFile));
                Log.d("image", "onActivityResult: "+mImageFile);
                break;

            case REQUEST_ALBUM:
                createImageFile();
                if (!mImageFile.exists()) {
                    return;
                }

                Uri uri = data.getData();
                if (uri != null) {
                    imageView.setImageURI(uri);
                }
                break;
        }
    }

    /**
     * 根据定位获取天堂网站的图片地址 *
     * @return
     */
    public void initBanner() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                pictures.clear();
                String shortLocal = null;
                if (local != null) {
                    shortLocal = local.substring(0, local.length() - 1);
                }
                try {
                    Document doc_add = Jsoup.connect("http://www.ivsky.com/search.php?q=" + shortLocal).get();
                    Element els_temp2 = doc_add.select("ul.pli").first();
                    Elements els_add = els_temp2.select("li");
                    for (int i = 0; i < 6; i++) {
                        Element el_add = els_add.get(i).select("a").first();
                        Document doc_detail = Jsoup.connect("http://www.ivsky.com/" + el_add.attr("href")).get();
                        Element el_detail = doc_detail.select("div.left").first();
                        pictures.add(el_detail.select("img").first().attr("src"));
                        Log.d("55555", "run: "+pictures.get(i).toString());
                   }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    mHandler.sendEmptyMessageDelayed(0X1112, 0);
                }
            }
        }.start();
    }

    public void initRecyclerView() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String shortLocal = null;
                if (local != null) {
                    shortLocal = local.substring(0, local.length() - 1);
                }else{
                    shortLocal = "张家界";
                }
                Log.d("56", "run: " + shortLocal);
                try {
                    messages.clear();
                    Document doc = Jsoup.connect("https://www.douban.com/search?cat=1015&q=" + shortLocal).get();
                    Log.d("56", "run: 连接豆瓣正常");
                    Elements els = doc.select("div.content");
                    for (int i = 0; i < els.size(); i++) {
                        MainRecyclerView message = new MainRecyclerView();
                        Element el = els.get(i);
//                        Log.d(TAG, "run: " + el.select("a").attr("href"));
                        message.setTitle(el.select("a").first().text());
//                        Log.d(TAG, "run: "+el.select("a").text());
                        message.setText(el.select("p").text());
                        message.setContent(el.select("a").attr("href"));
                        messages.add(message);
                    }
                    Log.d("56", "run: " + "douban nomal");
                    //                给每个item添加图片
                    Document doc_pic = Jsoup.connect("http://www.ivsky.com/search.php?q=" + shortLocal + "&PageNo=2").get();
                    Element els_temp = doc_pic.select("ul.pli").first();
                    Elements els_pic = els_temp.select("li");
                    for (int i = 0; i < els_pic.size(); i++) {
                        if (i > messages.size()){
                            break;
                        }
                        Element el_pic = els_pic.get(i).select("img").first();
                        messages.get(i).setIamge(el_pic.attr("src"));
                    }
                } catch (IOException e) {
                    Log.d("56", "run: " + e.toString());
                }
                finally {
                    mHandler.sendEmptyMessageDelayed(0X1113, 0);
                }
            }
        }.start();
    }

    /**
     * 显示进度条
     */
    private void showProgressBar(){
        if (binding.progressBar.getVisibility() == View.GONE){
            binding.progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    /**
     * 关闭进度条
     */
    private void closeProgressBar(){
        if(binding.progressBar.getVisibility() == View.VISIBLE){
            binding.progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void createImageFile() {
        mImageFile = new File(getExternalCacheDir(), "touxiang.jpg");
        try {
            mImageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "创建文件对象失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectCamera() {
        createImageFile();
        if (!mImageFile.exists()) {
            return;
        }
        Uri imageuri ;
        if (Build.VERSION.SDK_INT >= 24){
            imageuri = FileProvider.getUriForFile(MainActivity.this,
                    "com.example.cameraalbum.fileprovider", mImageFile);
        }else{
            imageuri = Uri.fromFile(mImageFile);
        }
        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    private void selectAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(albumIntent, REQUEST_ALBUM);
    }

    private void getPersimmilns() {
        ArrayList<String> permissionList = new ArrayList<>();
        // 定位精确位置
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        // 读写权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        // 相机
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
        if (local == null) {
            requestLocation();
        }
    }

    /**
     * 获取位置的两个个函数
     */
    private void requestLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationlistener());
        //告诉百度需要获取所在城市
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public class MyLocationlistener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            local = bdLocation.getCity();
        }
    }
}











