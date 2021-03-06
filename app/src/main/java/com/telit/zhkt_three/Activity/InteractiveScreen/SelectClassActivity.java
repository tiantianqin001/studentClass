package com.telit.zhkt_three.Activity.InteractiveScreen;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.interactive.RVSelectClazzAdapter;
import com.telit.zhkt_three.CustomView.CircleImageView;
import com.telit.zhkt_three.CustomView.RippleBackground;
import com.telit.zhkt_three.JavaBean.InterActive.ServerIpInfo;
import com.telit.zhkt_three.JavaBean.StudentInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Service.SockUserServer;
import com.telit.zhkt_three.Utils.ApkListInfoUtils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.StudentInfoDao;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * ??????SSDP?????????Location??????http:172.16.5.158:7777/???????????????
 */
public class SelectClassActivity extends BaseActivity {

    private Unbinder unbinder;

    @BindView(R.id.board_wifi)
    ImageView board_wifi;

    @BindView(R.id.clazz_recycler)
    RecyclerView recyclerView;

    //-----------?????????
    @BindView(R.id.leak_resource_layout)
    LinearLayout leak_resource_layout;

    @BindView(R.id.head_name)
    TextView home_nickname;
    @BindView(R.id.head_clazz)
    TextView home_clazz;

    @BindView(R.id.tv_address_ip)
    TextView tv_address_ip;

    @BindView(R.id.tv_wifi_name)
    TextView tv_wifi_name;
    @BindView(R.id.head_avatar)
    CircleImageView home_avatar;

    //????????????      tv_wifi_shouse  tv_wifi_name1
    @BindView(R.id.tv_wifi_shouse)
    TextView tv_wifi_shouse;

    @BindView(R.id.tv_wifi_name1)
    TextView tv_wifi_name1;

    @BindView(R.id.leak_resource)
    ImageView leak_resource;
    private RVSelectClazzAdapter selectClazzAdapter;
    public static final String TAG = "SelectClassActivity";
    private static boolean isShow = false;
    private static final int Operator_Success_Two = 5;
    private static final int Operator_Err = 4;
    private static boolean is_join_Multicast = true;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String message = intent.getExtras().getString("message");
            Log.i(TAG, "onReceive: "+message);

            if (!TextUtils.isEmpty(message)) {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String className = jsonObject.optString("className");
                    String ip = jsonObject.optString("address");
                    String port = jsonObject.optString("port");
                    String teacherId = jsonObject.optString("teacherId");
                    // String serviceName = jsonObject.optString("serviceName");
                    ServerIpInfo serverIpInfo = new ServerIpInfo();
                    serverIpInfo.setClassName(className);
                    serverIpInfo.setDevicePort(port);
                    serverIpInfo.setDeviceIp(ip);
                    serverIpInfo.setClassName(className);
                    serverIpInfo.setTeacherId(teacherId);
                    //???????????????????????????  ??????????????????
                    synchronized (SelectClassActivity.this) {
                        ServerIpInfos1.put(ip, serverIpInfo);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    //????????????????????????
    ConcurrentHashMap<String, ServerIpInfo> currentServerInfos = new ConcurrentHashMap<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Operator_Success_Two:
                    if (isShow) {
                        valueServerIpInfos.clear();
                        ConcurrentHashMap<String, ServerIpInfo> currentServerInfos = (ConcurrentHashMap<String, ServerIpInfo>) msg.obj;
                        if (currentServerInfos == null) {
                            if (leak_resource_layout != null && recyclerView != null) {
                                Log.i(TAG, "run: wwwwwServerIp??????????????????ccccccc");
                                leak_resource_layout.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                leak_resource.setVisibility(View.VISIBLE);
                            }
                            return;
                        }
                        Log.i(TAG, "handleMessage123: " + Operator_Success_Two);
                        //??????map
                        for (Map.Entry<String, ServerIpInfo> entry : currentServerInfos.entrySet()) {
                            valueServerIpInfos.add(entry.getValue());
                        }


                        if (valueServerIpInfos.size() <= 0) {
                            if (leak_resource_layout != null && recyclerView != null) {

                                leak_resource_layout.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                leak_resource.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (leak_resource_layout != null) {
                                leak_resource_layout.setVisibility(View.GONE);
                                leak_resource.setVisibility(View.GONE);
                            }
                            if (recyclerView != null) {
                                recyclerView.setVisibility(View.VISIBLE);
                                selectClazzAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    break;
                case Operator_Err:
                    if (leak_resource_layout != null) {
                        leak_resource_layout.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };
    private volatile ConcurrentHashMap<String, ServerIpInfo> ServerIpInfos1 = new ConcurrentHashMap<>();
    private volatile ConcurrentHashMap<String, ServerIpInfo> ServerIpInfos2 = new ConcurrentHashMap<>();

    private CopyOnWriteArrayList<ServerIpInfo> valueServerIpInfos = new CopyOnWriteArrayList<>();

    //private static final String Multicast_IP = "224.5.6.7";
    private static final String Multicast_IP = "239.5.6.7";
    private static final int Multicast_Port = 37656;
    private ExecutorService executorService;
    private MulticastSocket multicastSocket;
    private MulticastLock multicastLock;
    private Timer timerTask;
    private RippleBackground rippleBackground;
    private CountDownLatch endLatch;
    private static final int REQUEST_OVERLAY = 4444;
    private DatagramPacket packet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_class);
        //??????IP
        isShow = true;
        is_join_Multicast = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //?????????????????????
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY);
            } else {

            }
        }

        //  QZXTools.popCommonToast(this, "??????Ip = " + ownIP, false);

        //????????????????????????
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: 222222222222222");
        unbinder = ButterKnife.bind(this);
        String wifiName = getWIFIName(this);
        String ownIP = QZXTools.getIPAddress();
        QZXTools.logE("??????IP = " + ownIP, null);
        //??????????????????
        if (!TextUtils.isEmpty(wifiName)) {
            tv_wifi_name.setText(wifiName);
            tv_wifi_name1.setText(wifiName);
        }
        tv_address_ip.setText("???????????? " + ownIP);

        tv_wifi_shouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QZXTools.enterWifiSetting(SelectClassActivity.this);
            }
        });
        StudentInfoDao studentInfoDao = MyApplication.getInstance().getDaoSession().getStudentInfoDao();
        StudentInfo studentInfo = studentInfoDao.queryBuilder().where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).unique();
        if (studentInfo != null) {
            home_nickname.setText(studentInfo.getStudentName());
            if (studentInfo.getClassName() != null) {
                if (studentInfo.getGradeName() != null) {
                    home_clazz.setText(studentInfo.getGradeName().concat(studentInfo.getClassName()));
                } else {
                    home_clazz.setText(studentInfo.getClassName());
                }
            }
            if (studentInfo.getPhoto() == null) {
                home_avatar.setImageResource(R.mipmap.icon_user);
            } else {
                Glide.with(this).load(studentInfo.getPhoto()).
                        placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(home_avatar);
            }
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (selectClazzAdapter == null) {
            selectClazzAdapter = new RVSelectClazzAdapter(SelectClassActivity.this, valueServerIpInfos);
        }
        recyclerView.setAdapter(selectClazzAdapter);
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
        if (leak_resource_layout != null) {
            leak_resource_layout.setVisibility(View.GONE);
            leak_resource.setVisibility(View.GONE);
        }
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        is_join_Multicast = true;
        isShow = true;
        //????????????????????????
        ServerIpInfos2.clear();
        //????????????
        IntentFilter filter = new IntentFilter("com.gdp2852.demo.service.broadcast");
        registerReceiver(receiver, filter);
        //????????????
        Intent service = new Intent(this, SockUserServer.class);
        startService(service);

     /*   try {
            //???????????????
            multicastSocket = createMulticastGroupAndJoin(Multicast_IP, Multicast_Port);
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            multicastLock = wifiManager.createMulticastLock(SelectClassActivity.class.getSimpleName());
            multicastLock.acquire();
            multicastSocket.setNetworkInterface(NetworkInterface.getByName("wlan0"));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //?????????????????????  ??????6???????????????????????????
     /*   executorService = ApkListInfoUtils.getInstance().onStart();
        executorService.execute(new Runnable() {
            @Override
            public synchronized void run() {
                while (is_join_Multicast) {
                    try {
                        getDatashows();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });*/
        //5s???????????????????????? ??????????????????    ????????????5s ???????????????????????????????????????
        timerTask = new Timer();
        timerTask.schedule(new TimerTask() {
            @Override
            public void run() {
                //???????????????
                recycleInfoView();
            }
        }, 2000, 15000);

        if (!TextUtils.isEmpty(wifiName)) {
            if (tv_wifi_name != null && tv_wifi_name1 != null) {

                tv_wifi_name.setText(wifiName);
                tv_wifi_name1.setText(wifiName);
            }
        }
        if (!TextUtils.isEmpty(ownIP)) {
            if (tv_address_ip != null) {

                tv_address_ip.setText("???????????? " + ownIP);
            }
        }
    }

    private synchronized void recycleInfoView() {


        currentServerInfos.clear();
        currentServerInfos.putAll(ServerIpInfos1);
    /*    Iterator<Map.Entry<String, ServerIpInfo>> entryIterator = ServerIpInfos1.entrySet().iterator();
        while (entryIterator.hasNext()){
            entryIterator.remove();
        }*/
        synchronized (SelectClassActivity.class) {
            ServerIpInfos1.clear();
        }

        if (!isMapEaquse(currentServerInfos, ServerIpInfos2) && currentServerInfos.size() >= 1) {
            if (mHandler != null) {
                Message message = Message.obtain();
                message.obj = currentServerInfos;
                message.what = Operator_Success_Two;
                mHandler.sendMessage(message);
            }
        } else {
            //???????????????????????????????????????
            if (mHandler != null && currentServerInfos.size() == 0) {
                Message message = Message.obtain();
                message.obj = null;
                message.what = Operator_Success_Two;
                mHandler.sendMessage(message);
            }
        }
        //??????????????????
        ServerIpInfos2.clear();
        ServerIpInfos2.putAll(currentServerInfos);
    }

    //????????????
    @Override
    protected void onPause() {
        super.onPause();
        is_join_Multicast = false;
        isShow = false;

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if (rippleBackground != null) {
            rippleBackground.stopRippleAnimation();
            rippleBackground = null;
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        //???????????????
    }

    private synchronized void getDatashows() {
        //????????????????????????????????????????????????

        String message = recieveData(multicastSocket, Multicast_IP);//??????????????????????????????
        if (!TextUtils.isEmpty(message)) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                String className = jsonObject.optString("className");
                String ip = jsonObject.optString("address");
                String port = jsonObject.optString("port");
                String teacherId = jsonObject.optString("teacherId");
                // String serviceName = jsonObject.optString("serviceName");
                ServerIpInfo serverIpInfo = new ServerIpInfo();
                serverIpInfo.setClassName(className);
                serverIpInfo.setDevicePort(port);
                serverIpInfo.setDeviceIp(ip);
                serverIpInfo.setClassName(className);
                serverIpInfo.setTeacherId(teacherId);
                //???????????????????????????  ??????????????????
                synchronized (SelectClassActivity.this) {
                    ServerIpInfos1.put(ip, serverIpInfo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public MulticastSocket createMulticastGroupAndJoin(String groupurl, int port) // ?????????????????????????????????????????????
    {
        try {
            InetAddress group = InetAddress.getByName(groupurl); // ???????????????????????????239.0.0.0
            MulticastSocket socket = new MulticastSocket(port); // ?????????MulticastSocket??????????????????????????????

            //  socket.setTimeToLive(1); // ???????????????????????????????????????????????????
            socket.setSoTimeout(10000); // ????????????????????????????????????????????????
            socket.joinGroup(group); // ??????????????????
            //socket.setLoopbackMode(false);
            return socket;
        } catch (Exception e1) {
            System.out.println("Error: " + e1); // ??????????????????
            return null;
        }
    }

    public synchronized String recieveData(MulticastSocket socket, String groupurl) {
        String message = null;
        try {
            InetAddress group = InetAddress.getByName(groupurl);
            byte[] data = new byte[1024];
            packet = new DatagramPacket(data, data.length, group, Multicast_Port);
            socket.receive(packet); // ??????MulticastSocket????????????????????????????????????
            // ??????????????????????????????????????????
            message = new String(packet.getData(), 0, packet.getLength());
        } catch (Exception e1) {
            Log.i(TAG, "recieveData: " + e1);
            message = "Error: " + e1;
            if (is_join_Multicast) {
                synchronized (SelectClassActivity.class) {
                    ServerIpInfos1.clear();
                }
                ServerIpInfos2.clear();
                if (mHandler != null) {
                    Message message1 = Message.obtain();
                    message1.what = Operator_Success_Two;
                    message1.obj = null;
                    mHandler.sendMessage(message1);
                }
            }
        }
        return message;
    }

    @Override
    protected void onDestroy() {
        isShow = false;


        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        QZXTools.setmToastNull();

        synchronized (SelectClassActivity.class) {
            ServerIpInfos1.clear();
        }
        ServerIpInfos2.clear();

        if (rippleBackground != null) {
            rippleBackground.stopRippleAnimation();
            rippleBackground = null;
        }
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        isShow = false;
        if (leak_resource_layout != null) {
            leak_resource_layout.setVisibility(View.GONE);
            leak_resource.setVisibility(View.GONE);
        }
    }

    /**
     * ?????????????????????wifi??????
     *
     * @param context
     * @return
     */
    public static String getWIFIName(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID().replace("\"", "") : null;
        return wifiId;
    }

    //????????????map  ???????????????
    private static boolean isMapEaquse(ConcurrentHashMap<String, ServerIpInfo> currentServerInfos,
                                       ConcurrentHashMap<String, ServerIpInfo> serverIpInfos2) {
        if (currentServerInfos.size() != serverIpInfos2.size()) return false;
        Iterator<String> iterator = currentServerInfos.keySet().iterator();

        if (iterator.hasNext()) {
            String next = iterator.next();
            Log.i(TAG, "isMapEaquse: " + next);
            if (serverIpInfos2.containsKey(next)) {
                return true;
            }
        }
        return false;
    }

}
