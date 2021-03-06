package com.telit.zhkt_three.Activity.InteractiveScreen;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Activity.HomeWork.ExtraInfoBean;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.CircleImageView;
import com.telit.zhkt_three.CustomView.QuestionView.SubjectiveToDoView;
import com.telit.zhkt_three.Fragment.Dialog.CameraAlbumPopupFragment;
import com.telit.zhkt_three.Fragment.Dialog.CollectDisplayDialog;
import com.telit.zhkt_three.Fragment.Dialog.FileReceiveDialog;
import com.telit.zhkt_three.Fragment.Dialog.PraiseAndCriticismDialog;
import com.telit.zhkt_three.Fragment.Dialog.RandomNameDialog;
import com.telit.zhkt_three.Fragment.Dialog.ReceiveFilesDialog;
import com.telit.zhkt_three.Fragment.Dialog.ScreenShotImgDialog;
import com.telit.zhkt_three.Fragment.Dialog.TipsDialog;
import com.telit.zhkt_three.Fragment.Interactive.AskQueestionFragment;
import com.telit.zhkt_three.Fragment.Interactive.FreeSelectDiscussGroupFragment;
import com.telit.zhkt_three.Fragment.Interactive.GroupDiscussFragment;
import com.telit.zhkt_three.Fragment.Interactive.LockFragment;
import com.telit.zhkt_three.Fragment.Interactive.NewWhiteBoardFragment;
import com.telit.zhkt_three.Fragment.Interactive.PlayingRtspFragment;
import com.telit.zhkt_three.Fragment.Interactive.QuestionFragment;
import com.telit.zhkt_three.Fragment.Interactive.QuestionOnlyPicFragment;
import com.telit.zhkt_three.Fragment.Interactive.ResponderFragment;
import com.telit.zhkt_three.Fragment.Interactive.TeacherShotFragment;
import com.telit.zhkt_three.Fragment.Interactive.VoteFragment;
import com.telit.zhkt_three.Fragment.Interactive.WebViewFragment;
import com.telit.zhkt_three.Fragment.Interactive.WhitBoardPushFragment;
import com.telit.zhkt_three.JavaBean.StudentInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.RtspDisplayPush.DisplayService;
import com.telit.zhkt_three.ScreenLive.PusherContract;
import com.telit.zhkt_three.Service.ScreenRecordService;
import com.telit.zhkt_three.Service.ScreenShotService;
import com.telit.zhkt_three.Service.SockUserServer;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.FileLogUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UriTool;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ZBVPermission;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.telit.zhkt_three.customNetty.MsgUtils;
import com.telit.zhkt_three.customNetty.SimpleClientListener;
import com.telit.zhkt_three.customNetty.SimpleClientNetty;
import com.telit.zhkt_three.dialoge.CustomDialog;
import com.telit.zhkt_three.floatingview.PopWindows;
import com.telit.zhkt_three.greendao.StudentInfoDao;
import com.telit.zhkt_three.receiver.NotificationBroadcastReceiver;
import com.zbv.basemodel.LingChuangUtils;
import com.zbv.meeting.util.SharedPreferenceUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * ?????????????????????????????????????????????????????????
 * <p>
 * ?????????????????????---?????????
 * ???????????????????????????????????????
 * ??????????????????
 * ???????????????????????????????????????????????????
 * <p>
 * mediaprojection?????????????????????handler
 * <p>
 * ???????????????CommandWord + " " + UUID + " "+jsonStr+"\r\n"
 * <p>
 * ??????????????????????????????
 * ???????????????JoinClass???
 * ?????? ???LockScreen ???
 * ?????? ???UnlockScreen???
 * ?????? ???ShutdownSdtDevice???
 * ???????????? ???Acknowledgement???
 * ?????? ???FirstAnswer???
 * ???????????? ???ShareScreenshot???
 * ?????? ???Praise???
 * ?????? ???Criticism???
 * ???????????? ???Screenbroadcast???
 * ?????????????????? ???StopScreenbroadcast???
 * ???????????? ???ScreenCast???
 * ?????????????????? ???StopScreenCast???
 * ???????????? ???StartVote???
 * ???????????? ???EndVote???
 * ?????????????????? ???StartDiscuss???
 * ?????????????????? ???EndDiscuss???
 * ?????????????????? ???Discuss???
 * PPT?????? ???PPTCommand???
 * <p>
 * ?????????????????????????????????????????????????????????????????????:???????????????????????????????????????????????????????????????????????????????????????????????????
 * todo ??????Activity???????????????????????????????????????????????????????????????????????????????????????????????????
 */
public class InteractiveActivity extends BaseActivity implements View.OnClickListener,
        SimpleClientListener, SurfaceHolder.Callback, PusherContract.View {
    private static final String TAG = "InteractiveActivity";
    private Unbinder unbinder;
    @BindView(R.id.board_avatar)
    CircleImageView avatar;
    @BindView(R.id.board_name)
    TextView board_name;
    @BindView(R.id.board_clazz)
    TextView board_clazz;
    @BindView(R.id.board_status)
    TextView board_status;
    @BindView(R.id.board_wifi)
    ImageView board_wifi;
    @BindView(R.id.board_more)
    ImageView board_more;
    @BindView(R.id.board_rl_head)
    RelativeLayout board_rl_head;

    //tv_address_ip
    @BindView(R.id.tv_address_ip)
    TextView tv_address_ip;
    //tv_wifi_name
    @BindView(R.id.tv_wifi_name)
    TextView tv_wifi_name;

    private static final int Media_Projection_Shot_RequestCode = 11;
    private static final int Media_Projection_Record_RequestCode = 12;

    static public Intent mResultIntent;
    static public int mResultCode;

    private MyHandler mHandler;

    //?????????????????????Map
    private Map<String, String> receiveMsgMap;
    private List<String> receiveMsgKeyList;


    //----------------------NEW??????-----------------------------
    private SharedPreferences sp_last_msg;
    private File videoFile_jieping;
    private RandomNameDialog randomNameDialog;
    private LockFragment lockFragment;
    private CustomDialog customDialog;
    private QuestionOnlyPicFragment questionOnlyPicFragment;
    private onCellNettyListener listener;
    private String sp_msg;
    private NotificationManager notificationManager;
    private String receiveHead;
    private PopWindows popWindows;
    private ImageView iv_san_suo;

    // HEAD_END_CLASS ??????????????????,????????????????????????????????????????????????

    private  class MyHandler extends Handler {
        private WeakReference<InteractiveActivity> activity;

        public MyHandler(InteractiveActivity testActivity) {
            super(Looper.getMainLooper());
            this.activity = new WeakReference<InteractiveActivity>(testActivity);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
//            QZXTools.logE("handler thread=" + Thread.currentThread() + ";msg=" + msg, null);
            super.handleMessage(msg);
            InteractiveActivity context = activity.get();
            if (null == context) {
                return;
            }
            switch (msg.what) {
                case Constant.OnLine:
                    if (context.board_status != null) {
                        context.board_status.setText("??????");
                        //???????????????????????????????????????????????????
                        Log.i(TAG, "sendJoinClass: ");
                        context.sendJoinClass();
                    }
                    break;
                case Constant.OffLine:
                    //????????????????????????????????????handler?????????????????????????????????
                    SimpleClientNetty.getInstance().setReconnected(true);
                    boolean netAvail = QZXTools.isNetworkAvailable();
                    if (!netAvail) {
                        if (context.board_status != null) {
                            context.board_status.setText("??????");
                        }
//                        QZXTools.popToast(context, "?????????????????????????????????????????????", false);
                    } else {
//                        QZXTools.popToast(context, (String) msg.obj, false);
                        if (context.board_status != null) {
                            context.board_status.setText("??????");
                            //????????????wifi ?????????
                          //  robotWifi();
                        }
                    }
                    break;
                case Constant.ReceiveMessage:
                    if (context.isDestroy) {
                        return;
                    }
                    //??????LinkedMap?????????key???uuid,value???msg
                    if (context.receiveMsgKeyList == null) {
                        context.receiveMsgKeyList = new LinkedList<>();
                    }

                    if (context.receiveMsgMap == null) {
                        context.receiveMsgMap = new LinkedHashMap<>();
                    }

                    String stringData = (String) msg.obj;
                    //????????????????????????
                    if (TextUtils.isEmpty(stringData)) return;
                    String[] splitString = stringData.trim().split(MsgUtils.SEPARATOR);
                    String head = splitString[0];
                    receiveHead=head;
                    //????????????????????????????????????
                    if (TextUtils.isEmpty(head) || head.equals(MsgUtils.HEAD_HEART) || splitString.length<=1) return;
                    String seqId = splitString[1];
                    String body = stringData.substring(head.length() + seqId.length() + 1).trim();

                    QZXTools.logD("receiveMsg ===> stringData=" + stringData.trim()
                            + ";head=" + head + ";seqId=" + seqId + ";body=" + body, null);

                    if (!TextUtils.isEmpty(head)) {

                        //?????????????????????????????????=?????????======================================================???
                        if (!head.equals(MsgUtils.HEAD_HEART) && !head.equals(MsgUtils.HEAD_ACKNOWLEDGE)) {
                            if (context.fileLogUtils != null) {
                                context.fileLogUtils.saveLogs("received msg ===> " + stringData);
                            }

                            /**
                             * ???????????????????????????SP,?????????????????????????????????????????????????????????????????????
                             *  ??????????????? ?????????????????? ?????????????????? ???????????????????????????????????????????????????
                             * */
                            //context.sp_last_msg.edit().putString("Last_Msg", stringData).commit();
                            //??????????????????????????????
                            QZXTools.logD("jieshouInfo              " + stringData.trim() +
                                    ";head=" + head + ";seqId=" + seqId + ";body=" + body, null);

                            //??????????????????????????? ?????????????????????????????????????????????????????? ,???????????????,?????????????????????
                            // ?????????????????????????????????  ????????????
                            //????????????????????? ????????????
                            if (!head.equals(MsgUtils.HEAD_LOCK) && !head.equals(MsgUtils.HEAD_UNLOCK)) {
                                if (head.equals(MsgUtils.HEAD_BROADCAST) ||  head.equals(MsgUtils.HEAD_START_VOTE)
                                        || head.equals(MsgUtils.HEAD_FREE_DISCUSS)
                                        || head.equals(MsgUtils.HEAD_SHARE_SHOT)) {
                                    context.sp_last_msg.edit().putString("Last_Msg", stringData).commit();

                                } else {
                                    context.sp_last_msg.edit().clear().commit();
                                }

                            }
                            //??????????????? ???????????????
                            LingChuangUtils.getInstance().startBack(MyApplication.getInstance());
                        }
                        //??????????????????????????????????????????????????????????????????????????????
                        if (MsgUtils.HEAD_HEART.equals(head)) {
                            QZXTools.logD("---?????????????????????????????????---" + stringData);
                        } else if (MsgUtils.HEAD_JOIN_CLASS_SUCCESS.equals(head)) {
                            //??????????????????
                            context.popToastInfo("??????????????????...");
                        } else if (MsgUtils.HEAD_RECONNECT_SUCCESS.equals(head)) {
                            //????????????
                            context.popToastInfo("???????????????...");
                        } else if (MsgUtils.HEAD_PRAISE.equals(head)) {
                            //????????????
                            context.receivePraiseOrCriticism(true, body);
                        } else if (MsgUtils.HEAD_CRITICISM.equals(head)) {
                            //????????????
                            context.receivePraiseOrCriticism(false, "");
                        } else if (MsgUtils.HEAD_START_CLASS.equals(head)) {
                            //????????????
                            context.popToastInfo("???????????????...");
                            context.enterWhiteBoard(MsgUtils.HEAD_START_CLASS);
                        } else if (MsgUtils.HEAD_END_CLASS.equals(head)) {
                            //????????????
                            context.popToastInfo("???????????????...");
                        } else if (MsgUtils.HEAD_LOCK.equals(head)) {
                            //??????
                            context.enterLock();
                            BuriedPointUtils.buriedPoint("2012","","","","");
                        } else if (MsgUtils.HEAD_UNLOCK.equals(head)) {
                            //?????????????????????????????????????????????
                            context.enterWhiteBoard(MsgUtils.HEAD_UNLOCK);
                            BuriedPointUtils.buriedPoint("2013","","","","");
                        } else if (MsgUtils.HEAD_FIRST_ANSWER.equals(head)) {
                            //????????????
                            context.startResponder();
                            //???????????????
                            BuriedPointUtils.buriedPoint("2006","","","","");
                        } else if (MsgUtils.HEAD_SUCCESS_ANSWER.equals(head)) {
                            //SuccessAnswer ca153fb835ca4a0b899dc180b12e696a ?????????18
                            context.showResponder(body);
                        } else if (MsgUtils.HEAD_END_ANSWER.equals(head)) {
                            //????????????
                            context.enterWhiteBoard(MsgUtils.HEAD_END_ANSWER);
                        } else if (MsgUtils.HEAD_RANDOM_NAME.equals(head)) {
                            //SuccessRoleCall e32f20d6cd4d409b975668aadb9df405 ?????????48
                            //????????????
                            context.randomName(body);
                        } else if (MsgUtils.HEAD_FILERECIEVE.equals(head)) {
                            //????????????
                            context.judgeFileReceivePermission(body);
                        } else if (MsgUtils.HEAD_START_VOTE.equals(head)) {
                            //????????????
                            context.startVote(body);
                            //????????????
                            BuriedPointUtils.buriedPoint("2008","","","","");
                        } else if (MsgUtils.HEAD_END_VOTE.equals(head)) {
                            //????????????
                            // context.popToastInfo("????????????");
                            context.endVote();
                        } else if (MsgUtils.HEAD_START_PRACTICE.equals(head)) {
                            //??????????????????
                            context.startPractice(body);
                        } else if (MsgUtils.HEAD_END_PRACTICE.equals(head)) {
                            //??????????????????
                            context.endPractice();
                        } else if (MsgUtils.HEAD_START_DISCUSS.equals(head)) {
                            //??????????????????
                            context.startDiscuss(body);
                        } else if (MsgUtils.HEAD_END_DISCUSS.equals(head)) {
                            //??????????????????
                            context.endDiscuss();
                        } else if (MsgUtils.HEAD_FREE_DISCUSS.equals(head)) {
                            //?????????????????????
                            context.toFreeDiscuss(body);
                            //????????????
                            BuriedPointUtils.buriedPoint("2007","","","","");
                        } else if (MsgUtils.HEAD_DISCUSS.equals(head)) {
                            //????????????????????????
                            EventBus.getDefault().post(body.trim(), Constant.Discuss_Message);
                        } else if (MsgUtils.HEAD_ACKNOWLEDGE.equals(head)) {
                            //????????????????????????,?????????????????????
                            if (!SimpleClientNetty.getInstance().getConcurrentLinkedQueue().isEmpty()) {
                                SimpleClientNetty.getInstance().getConcurrentLinkedQueue().remove();
                            }
                        } else if (MsgUtils.HEAD_PPT_COMMAND.equals(head)) {
                            //ppt??????
//                            QZXTools.logE("ppt command body=" + body, null);
                            String[] splitStrs = body.split(" ");
                            QZXTools.logE("qin1223......"+splitStrs[0]+"...."+splitString[1],null);
                            if (splitStrs.length<=1){
                                QZXTools.popToast(context,"?????????ppt?????????",true);
                                return;
                            }
                            //??????ppt
                            context.enterPPTCommand(splitStrs[0], splitStrs[1]);
                        } else if (MsgUtils.HEAD_BROADCAST.equals(head)) {
                            //????????????
                            context.startBroadcast(body);
                            BuriedPointUtils.buriedPoint("2011","","","","");
                        } else if (MsgUtils.HEAD_STOP_BROADCAST.equals(head)) {
                            //????????????
                            context.stopBroadcast();
                            //context.enterWhiteBoard();
                        } else if (MsgUtils.HEAD_SCREEN_CAST.equals(head)) {
                            //????????????
                            context.startStudnetCast(body);
                        } else if (MsgUtils.HEAD_STOP_SCREEN_CAST.equals(head)) {
                            //????????????
                            context.stopStudnetCast();
                        } else if (MsgUtils.HEAD_SHUTDOWN.equals(head)) {
                            //??????
                            context.receiveShutdown();
                        } else if (MsgUtils.HEAD_SHARE_SHOT.equals(head)) {
                            //????????????????????????Url??????
                            context.showTeacherShot(body, false);
                            BuriedPointUtils.buriedPoint("2010","","","","");
                        } else if (MsgUtils.HEAD_FOCUS_SHARE.equals(head)) {
                            context.showTeacherShot(body, true);
                            //????????????
                        } else if (MsgUtils.HEAD_START_ANSWERE.equals(head)) {
                            Log.i("qin0509", "handleMessage: ");
                            context.answerQuestion(body);
                            //????????????????????????????????????
                            //????????????
                            BuriedPointUtils.buriedPoint("2005","","","","");
                        }else if (MsgUtils.HEAD_TUI_LIU_TEACHER.equals(head)){
                           context.tuiLliuTeacher();
                        }else if (MsgUtils.HEAD_WHILD_BOARDPUSH.equals(head)){
                            //???????????????
                            context.WhiteboardPush(body);
                        }else if (MsgUtils.HEAD_EndQuestion.equals(head)){
                            //???????????????????????????????????????????????????????????????
                           // EventBus.getDefault().post("questEnd",Constant.Homework_Commit_end);
                            //????????????
                            enterWhiteBoard("");
                        }else if (MsgUtils.HEAD_StudentPadScreenCast.equals(head)){
                            //??????????????????????????????????????????????????????

                            if (!DisplayService.Companion.isStreaming()) {
                                startActivityForResult(DisplayService.Companion.sendIntent(), REQUEST_CODE_STREAM_RTSP);
                                BuriedPointUtils.buriedPoint("2014","","","","");

                            }

                        }else if (MsgUtils.HEAD_StopStudentScreenCast.equals(head)){
                               //?????????????????????
                            stopService(new Intent(InteractiveActivity.this, DisplayService.class));
                            if (popWindows!=null)popWindows.cancel();popWindows=null;
                        }

                        //??????????????????????????????,?????????????????????????????????,?????????????????????????????????
                        if (!head.equals(MsgUtils.HEAD_HEART) && !head.equals(MsgUtils.HEAD_ACKNOWLEDGE)) {
                            SimpleClientNetty.getInstance().sendMsgToServer
                                    (MsgUtils.HEAD_ACKNOWLEDGE, MsgUtils.createAcknowledge(seqId));
                        }
                    }
                    break;
            }
        }
    }

    /**
     * ?????????????????????
     */
    private Handler timeHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case TIMES_SEND:
                    if (times%2==0){
                        iv_san_suo.setBackgroundColor(getResources().getColor(R.color.transGray));
                    }else {
                        iv_san_suo.setBackgroundColor(getResources().getColor(R.color.colorDarkBlue));
                    }
                    break;
            }
        }
    };
    private static final int TIMES_SEND = 0X100;
    private Timer timer;
    int times=0;
    private void showReadTime() {
        if (timer == null) {

            timer = new Timer();
        }
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // System.out.println("????????????????????????");
                times++;
                //??????????????????
                timeHandler.sendEmptyMessage(TIMES_SEND);

            }
        }, 100, 100);
        /*????????????????????????5s??????????????????2s???????????????????????????*/
    }

    private PusherContract.Presenter presenter;
    static public SurfaceView mSurfaceView;
    //todo  ????????????????????????   CapScreenService
    private void tuiLliuTeacher() {
        //??????????????????????????????????????????
        mSurfaceView.getHolder().addCallback(this);
        if (presenter != null) {
            presenter.onStartPush(this, 0,
                    new Intent(),
                   -1,
                    mSurfaceView);
        }
    }

    private  void robotWifi() {
        WifiManager mWifiManager=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiManager.setWifiEnabled(false);
        mWifiManager.setWifiEnabled(true);
    }
    private MediaProjectionManager projectionManager;
    private FileLogUtils fileLogUtils;
    private boolean isFileLog = false;
    private Handler WifiHandler=new Handler();
    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            tv_wifi_name.setText(getWIFIName(InteractiveActivity.this));
            WifiHandler.postDelayed(this, 5000);

            //??????IP
            String ownIP = QZXTools.getIPAddress();
            tv_address_ip.setText("????????????"+ownIP);
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //QZXTools.logE("??????IP = " + ownIP, null);
        //QZXTools.popCommonToast(this, "??????Ip = " + ownIP, false);

        setContentView(R.layout.interactive_board);
        //????????????????????????
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        unbinder = ButterKnife.bind(this);

        sp_last_msg = getSharedPreferences("sp_last_msg", MODE_PRIVATE);
        //??????wifi ?????????
        WifiHandler.postDelayed(runnable, 200);

        EventBus.getDefault().register(this);

        //??????????????????????????????????????????????????????android:keepScreenOn="true"
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        //??????????????????
//        Intent serverIntent = new Intent(this, SimpleSocketLinkServerTwo.class);
//        serverIntent.setAction(Constant.SOCKET_CONNECT_ACTION);
//        serverIntent.setPackage(getPackageName());
//        //????????????o,api26???8.0???
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            startForegroundService(serverIntent);
//            SimpleSocketLinkServerTwo.enqueueWork(this, serverIntent);
//        } else {
//            QZXTools.logE("Thread=" + Thread.currentThread().getName(), null);
//            startService(serverIntent);
//        }


//        //todo ??????????????????????????????
//        SimpleClientNetty.getInstance().init(UrlUtils.SocketIp, UrlUtils.SocketPort).connectAsync();
        // ?????????????????????
         stopService(new Intent(InteractiveActivity.this, DisplayService.class));

        board_wifi.setOnClickListener(this);
        board_more.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }
        setonCellNettyListener(SimpleClientNetty.getInstance());


        //??????????????? ????????????????????????
        DisplayService.Companion.init(this);

        popWindows = new PopWindows(getApplication());
        popWindows.setView(R.layout.popwindoeview)
                .setGravity(Gravity.LEFT | Gravity.TOP)
                .setYOffset(100)
                .show();
        iv_san_suo = (ImageView) popWindows.findViewById(R.id.iv_san_suo);
        showReadTime();

    }
    private ScheduledExecutorService messageExecutorService;
    @Override
    protected void onStart() {
        super.onStart();
        //??????????????????????????????
        if (isSubjective){
            return;
        }

        //??????????????????????????????
        StudentInfoDao studentInfoDao = MyApplication.getInstance().getDaoSession().getStudentInfoDao();
        StudentInfo studentInfo = studentInfoDao.queryBuilder().where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).unique();
        if (studentInfo != null) {
            board_name.setText(studentInfo.getStudentName());
            if (studentInfo.getClassName() != null) {
                if (studentInfo.getGradeName() != null) {
                    board_clazz.setText(studentInfo.getGradeName().concat(studentInfo.getClassName()));
                } else {
                    board_clazz.setText(studentInfo.getClassName());
                }
            }
            if (studentInfo.getPhoto() == null) {
                avatar.setImageResource(R.mipmap.icon_user);
            } else {
                Glide.with(this).load(studentInfo.getPhoto()).
                        placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(avatar);
            }
        }

        //??????log??????
      //  ZBVPermission.getInstance().setPermPassResult(this);

        if (!ZBVPermission.getInstance().hadPermissions(this, WriteReadPermissions)) {
            isFileLog = true;
            ZBVPermission.getInstance().requestPermissions(this, WriteReadPermissions);
        } else {
            fileLogUtils = FileLogUtils.getInstance();
        }

        if (messageExecutorService == null) {
            messageExecutorService = Executors.newSingleThreadScheduledExecutor();

        }
        messageExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                //Handler
                mHandler = new MyHandler(InteractiveActivity.this);

                //??????????????????
                SimpleClientNetty.getInstance().setSimpleClientListener(InteractiveActivity.this);

                String path = QZXTools.getExternalStorageForFiles(InteractiveActivity.this, null) + "/config.txt";
                Properties properties = QZXTools.getConfigProperties(path);
                String socketIp = properties.getProperty("socketIp");
                String socketPort = properties.getProperty("SocketPort");
                if (TextUtils.isEmpty(socketIp)){
                    socketIp=UrlUtils.SocketIp;
                }
                if (TextUtils.isEmpty(socketPort)){
                    socketPort=UrlUtils.SocketPort+"";
                }
                int port=Integer.valueOf(socketPort);

                //???????????????ip?????????
                SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("socketIp",socketIp);
                SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setInt("port",port);
                SimpleClientNetty.getInstance().init(UrlUtils.SocketIp,port);
                // SimpleClientNetty.getInstance().reConnect();
                //????????????
                enterWhiteBoard("");

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        //??????????????????????????????
        if (isSubjective){
            return;
        }


        if (mResultCode == 0 && mResultIntent == null) {
            checkCapScreenPermission();
        }
        Intent intent=new Intent(InteractiveActivity.this, SockUserServer.class);
        stopService(intent);
    }
    private void checkCapScreenPermission() {
        //TODO

    }
    private boolean isDestroy = false;
    /**
     * ????????????????????????
     */
    @Override
    protected void onDestroy() {
        isDestroy = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
        if (messageExecutorService!=null){
            messageExecutorService.shutdown();
            messageExecutorService=null;
        }

        EventBus.getDefault().unregister(this);

        //????????????,??????????????????????????????
        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_OUT_OF_CLASS, MsgUtils.outOfClass());

        if (SimpleClientNetty.getInstance().getSimpleClientHandler() != null) {
//            SimpleClientNetty.getInstance().getSimpleClientHandler().setDestroy(true);
            //??????????????????
            SimpleClientNetty.getInstance().getSimpleClientHandler().setAutoClosed(true);
        }

        //??????netty
        if (listener!=null){
            listener.stopNetty(true);
        }

        //??????sp
        sp_last_msg.edit().clear().commit();

//        //??????????????????
//        Intent serverIntent = new Intent(this, SimpleSocketLinkServerTwo.class);
//        stopService(serverIntent);

        mHandler = null;
        WifiHandler.removeCallbacks(runnable);
        WifiHandler=null;
        QZXTools.setmToastNull();

        if (customDialog!=null){
            customDialog.dismiss();
        }

        //?????????????????????
        String joinClassStudent = SharedPreferenceUtil.getInstance(MyApplication.getInstance()).getString("joinClassStudent");
        BuriedPointUtils.buriedPoint("2004","","","",joinClassStudent);

        if (popWindows!=null)popWindows.cancel();popWindows=null;

        if (timer!=null){
            timer.cancel();
            timer=null;
        }
        //????????????
        Intent intent=new Intent(this,DisplayService.class);
        stopService(intent);
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        //??????????????????????????????
        if (popIsRecordScreen) {
            //????????????---Service
            Intent intent = new Intent(this, ScreenRecordService.class);
            stopService(intent);
        }
        //??????tips
        TipsDialog tipsDialog = new TipsDialog();
        tipsDialog.setTipsStyle("????????????????????????????\n????????????????????????????????????????????????????????????????????????",
                "??????", "??????", -1);
        tipsDialog.setBackNoMiss();
        tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
            @Override
            public void cancle() {
                tipsDialog.dismissAllowingStateLoss();
            }

            @Override
            public void confirm() {
                tipsDialog.dismissAllowingStateLoss();
               // InteractiveActivity.super.onBackPressed();
                InteractiveActivity.this.finish();
            }
        });
        tipsDialog.show(getSupportFragmentManager(), TipsDialog.class.getSimpleName());
    }

    //???????????????????????????
    private boolean isShotGrant = false;

    //???????????????????????????
    private boolean isRecordScreen = false;
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * ??????????????????:
     */
    private boolean popIsRecordScreen = false;

    /**
     * screen shot???screen record????????????????????????????????????
     */
    @Override
    public void onClick(View v) {
        if (QZXTools.canClick()) {
            switch (v.getId()) {
                case R.id.board_wifi:
                    QZXTools.enterWifiSetting(this);

                    //??????shot??????
//                    String url = "http://test.download.cycore.cn/edc/openapi/avatar_default_teacher_200_m_2.png";
//
//                    showTeacherShot(url, false);

                    break;
                case R.id.board_more:
                    if (scheduledExecutorService == null) {
                        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                    }
                    popMorePan(v);
                    break;
                case R.id.new_board_shot:
                    morePop.dismiss();

                    isShotGrant = true;

                  //  ZBVPermission.getInstance().setPermPassResult(this);

                    if (!ZBVPermission.getInstance().hadPermissions(this, WriteReadPermissions)) {
                        ZBVPermission.getInstance().requestPermissions(this, WriteReadPermissions);
                    } else {
                        //????????????SD
                        QZXTools.logD("?????????????????????SDCard");
                        screenSnap();
                    }
                    break;
                case R.id.new_board_record:
                    //????????????
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        QZXTools.popToast(InteractiveActivity.this, "Android5.0??????????????????????????????", false);
                        return;
                    }
                    if (popIsRecordScreen) {
                        //????????????---Service
                        Intent intent = new Intent(this, ScreenRecordService.class);
                        stopService(intent);
                        popIsRecordScreen = false;
                        morePop.dismiss();
                    } else {

//                    if (recordScreenOperator != null && recordScreenOperator.isScreenRecord()) {
//                        recordScreenOperator.stopScreenRecord();
//                        morePop.dismiss();
//                    } else {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            QZXTools.popToast(this, "Android5.0?????????????????????", false);
                            return;
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                //Android6.0+????????????????????????
                                isRecordScreen = true;
                               // ZBVPermission.getInstance().setPermPassResult(this);
                                if (!ZBVPermission.getInstance().hadPermissions(this, RecordPermissions)) {
                                    ZBVPermission.getInstance().requestPermissions(this, RecordPermissions);
                                } else {
                                    QZXTools.logD("?????????????????????");
                                    recordScreen();
                                }
                            } else {
                                //5.0 5.1???????????????
                                recordScreen();
                            }
                        }
                    }
                    break;
                case R.id.new_board_file_receive:
                    morePop.dismiss();
                    ReceiveFilesDialog receiveFilesDialog = new ReceiveFilesDialog();
                    receiveFilesDialog.show(getSupportFragmentManager(), ReceiveFilesDialog.class.getSimpleName());
                    break;
                case R.id.new_board_collect_practice:
                    morePop.dismiss();
                    //??????????????????????????????
                    CollectDisplayDialog collectDisplayDialog = new CollectDisplayDialog();
                    collectDisplayDialog.show(getSupportFragmentManager(), CollectDisplayDialog.class.getSimpleName());
                    break;
            }
        }

    }

    @Subscriber(tag = Constant.Free_Theme_Over, mode = ThreadMode.MAIN)
    public void endFreeSelect(String msg) {
        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_FREE_JOIN_GROUP,
                MsgUtils.selectedGroup(msg));

        //????????????
        getoWhitBoardFragmeng();
    }
    /**
     * ?????????????????????????????????????????????
     */
    @Subscriber(tag = Constant.Show_Conclusion, mode = ThreadMode.MAIN)
    public void showConclusion(String nothing) {
        //todo ???????????????
        enterWhiteBoard("");
    }

    @Subscriber(tag = Constant.Screen_Record_file, mode = ThreadMode.MAIN)
    public void getScreenRecordFile(String filePath) {
        recordPath = filePath;
    }

    private String recordPath;

    /**
     * ======================> Js??????
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Subscriber(tag = Constant.Show_Js_Record, mode = ThreadMode.MAIN)
    public void recordScreen(JsRecordScreenBean jsRecordScreenBean) {
        int command = jsRecordScreenBean.getCommand();
        String json = jsRecordScreenBean.getJson();

        if (command == 1) {
            //????????????
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                QZXTools.popToast(this, "Android5.0?????????????????????", false);
                return;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Android6.0+????????????????????????
                    isRecordScreen = true;
                  //  ZBVPermission.getInstance().setPermPassResult(this);
                    if (!ZBVPermission.getInstance().hadPermissions(this, RecordPermissions)) {
                        ZBVPermission.getInstance().requestPermissions(this, RecordPermissions);
                    } else {
                        QZXTools.logD("?????????????????????");
                        recordScreen();
                    }
                } else {
                    //5.0 5.1???????????????
                    recordScreen();
                }
            }
        } else if (command == 0) {
            if (TextUtils.isEmpty(recordPath)) {
                QZXTools.logE("???????????????????????????????????????", null);
                return;
            }

            //????????????
            Intent intent = new Intent(this, ScreenRecordService.class);
            stopService(intent);
            popIsRecordScreen = false;

            //????????????????????????
            String url = UrlUtils.BaseUrl + UrlUtils.CommitJsPractice;

            Gson gson = new Gson();
            InteractionAnswerVO interactionAnswerVO = gson.fromJson(json, InteractionAnswerVO.class);

            interactionAnswerVO.setClassId(UserUtils.getClassId());
            interactionAnswerVO.setClassName(UserUtils.getClassName());
            interactionAnswerVO.setStudentName(UserUtils.getStudentName());

            String resultJson = gson.toJson(interactionAnswerVO);

            QZXTools.logE("resultJson=" + resultJson + ";name=" + UserUtils.getStudentName(), null);

            Map<String, String> map = new HashMap<>();
            map.put("interactionAnswer", resultJson);

            File file = new File(recordPath);

            OkHttp3_0Utils.getInstance().asyncPostSingleOkHttp(url, "file", map, file, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    QZXTools.logE("onFailure", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    QZXTools.logE("asyncPostSingleOkHttp str=" + response.body().string(), null);
                }
            });

        } else if (command == 2) {
            //??????????????????
            //????????????---Service
            Intent intent = new Intent(this, ScreenRecordService.class);
            stopService(intent);
            popIsRecordScreen = false;
        }
    }


    /**
     * ??????????????????????????????
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void recordScreen() {
        Intent captureIntent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, Media_Projection_Record_RequestCode);
        isRecordScreen = false;
    }

    /**
     * ?????????????????????????????????????????????
     * ???????????????
     * todo ???????????????MediaProjection??????????????????22??? ?????????????????????????????????????????????????????????
     */
    private void screenSnap() {

        QZXTools.logE("screenSnap date start =" + new Date().getTime(), null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //??????MediaProjection??????
            shotMediaProjection();//6s
        } else {
            //???????????????????????????
            shotDrawView();//431ms
        }

        QZXTools.logE("screenSnap date end =" + new Date().getTime(), null);

        //??????
        isShotGrant = false;
    }

    /**
     * ??????MediaProjection??????
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void shotMediaProjection() {
        startActivityForResult(projectionManager.createScreenCaptureIntent(), Media_Projection_Shot_RequestCode);
    }

    private String shotFilePath;

    /**
     * ????????????????????????View????????????????????????
     * ??????????????????SurfaceView?????????
     * ?????????????????????????????????
     */
    private void shotDrawView() {
        View view = getWindow().getDecorView();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        shotFilePath = QZXTools.getExternalStorageForFiles(this, null) + "/zhkt_shot";
        File dirFile = new File(shotFilePath);
        if (!dirFile.exists()) {
            boolean dirSuccess = dirFile.mkdir();
            if (!dirSuccess) {
                QZXTools.popToast(this, "?????????????????????????????????", false);
                return;
            }
        }

        FileOutputStream fos = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateStr = simpleDateFormat.format(new Date());
            String fileName = "shotImg_" + dateStr + ".png";
            File shotFile = new File(shotFilePath, fileName);
            fos = new FileOutputStream(shotFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            QZXTools.savePictureToSystemDCIM(InteractiveActivity.this, shotFile, "");

            //????????????
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = null;
            if (Build.VERSION.SDK_INT >= 26) {
                channel = new NotificationChannel("zhkt", "screen_shot", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder;
            if (channel != null && Build.VERSION.SDK_INT >= 26) {
                builder = new NotificationCompat.Builder(this);
            } else {
                builder = new NotificationCompat.Builder(this);
            }


            Intent intentClick = new Intent(this, NotificationBroadcastReceiver.class);
            intentClick.setAction(NotificationBroadcastReceiver.ACTION_CLICK);
            intentClick.putExtra(NotificationBroadcastReceiver.TYPE, 7);
            intentClick.putExtra("shot_path", shotFile.getAbsolutePath());
            PendingIntent pendingIntentClick = PendingIntent.getBroadcast(this, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);
            Intent intentCancel = new Intent(this, NotificationBroadcastReceiver.class);
            intentCancel.setAction(NotificationBroadcastReceiver.ACTION_CANCEL);
            intentCancel.putExtra(NotificationBroadcastReceiver.TYPE, 7);
            PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, 0, intentCancel, PendingIntent.FLAG_ONE_SHOT);

            //??????????????????????????????
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("??????");
            builder.setContentText("???????????????????????????");
            builder.setWhen(System.currentTimeMillis());
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            builder.setContentIntent(pendingIntentClick);
            builder.setDeleteIntent(pendingIntentCancel);

            Notification notification = builder.build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;//????????????????????????
            notificationManager.notify(7, notification);

            QZXTools.popToast(InteractiveActivity.this, "????????????", false);
            ScreenShotImgDialog screenShotImgDialog = new ScreenShotImgDialog();
            screenShotImgDialog.setImgFilePath(shotFile.getAbsolutePath());
            screenShotImgDialog.show(getSupportFragmentManager(), ScreenShotImgDialog.class.getSimpleName());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }


    private PopupWindow morePop;

    /**
     * ??????????????????????????????
     */
    private void popMorePan(View v) {
        if (morePop != null) {
            morePop.dismiss();
        }
        View moreView = LayoutInflater.from(this).inflate(R.layout.pop_white_board_more_vertical_layout, null);
        int moreWidth = getResources().getDimensionPixelSize(R.dimen.y126);
        int moreHeight = getResources().getDimensionPixelSize(R.dimen.x466);
        morePop = new PopupWindow(moreView, moreWidth, moreHeight);

        morePop.setBackgroundDrawable(new ColorDrawable());
        morePop.setOutsideTouchable(true);

        LinearLayout new_board_shot = moreView.findViewById(R.id.new_board_shot);
        LinearLayout new_board_record = moreView.findViewById(R.id.new_board_record);
        LinearLayout new_board_file_receive = moreView.findViewById(R.id.new_board_file_receive);
        LinearLayout new_board_collect_practice = moreView.findViewById(R.id.new_board_collect_practice);

        new_board_shot.setOnClickListener(this);
        new_board_record.setOnClickListener(this);
        new_board_file_receive.setOnClickListener(this);
        new_board_collect_practice.setOnClickListener(this);

        //??????????????????????????????????????????
        int offsetHeight = getResources().getDimensionPixelSize(R.dimen.x35);
        morePop.showAsDropDown(v, 0, offsetHeight);
    }

    //-------------------------????????????------------------------------
    @Override
    public void onLine() {
        if (mHandler == null) {
            return;
        }

        Message message = mHandler.obtainMessage();
        message.what = Constant.OnLine;
        message.obj = "??????";
        mHandler.sendMessage(message);
    }

    @Override
    public void offLine() {
        if (mHandler == null) {
            return;
        }

        Message message = mHandler.obtainMessage();
        message.what = Constant.OffLine;
        message.obj = "??????";
        mHandler.sendMessage(message);
    }

    @Override
    public void receiveData(String msgInfo) {
        if (mHandler == null) {
            return;
        }
        Log.i("qin002", "receiveData: "+msgInfo);
        Message message = mHandler.obtainMessage();
        message.what = Constant.ReceiveMessage;
        message.obj = msgInfo;
        mHandler.sendMessage(message);
    }



    /**
     * ??????????????????,?????????????????????????????????????????????????????????????????????????????????
     *
     * @param isFocus ?????????????????????
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showTeacherShot(String body, boolean isFocus) {
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }

//        Intent intent_img = new Intent(this, ImageLookActivity.class);
//        ArrayList<String> imgFilePathList = new ArrayList<>();
//        imgFilePathList.add(body);
//        intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
//        intent_img.putExtra("curImgIndex", 0);
//        startActivity(intent_img);

        TeacherShotFragment teacherShotFragment = new TeacherShotFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url_img", body);
        teacherShotFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, teacherShotFragment);
        fragmentTransaction.commitAllowingStateLoss();

        sendShotRecordToServer(body, isFocus);
    }


    //?????????????????????
    private void WhiteboardPush(String body) {
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }
        Log.i(TAG, "WhiteboardPush001: "+body);
        WhitBoardPushFragment whitBoardPushFragment = new WhitBoardPushFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url_img", body);
        whitBoardPushFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, whitBoardPushFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }
    /**
     * ????????????????????????
     * ???????????????????????????????????????????????????
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void sendShotRecordToServer(String fileUrl, boolean isFocus) {
        String url = UrlUtils.BaseUrl + UrlUtils.ToServerShotShare;
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("userId", UserUtils.getUserId());
        paraMap.put("classId", UserUtils.getClassId());
        if (isFocus) {
            paraMap.put("type", "19");
        } else {
            paraMap.put("type", "18");
        }
        paraMap.put("fileUrl", fileUrl);
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.logE("sendShotRecordToServer onFailure", null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                QZXTools.logE("result = " + response.body().string(), null);
            }
        });
    }

    /**
     * ?????????????????????
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void receiveShutdown() {
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.setMessage("---?????????????????????---").setCancelable(true)
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog != null && alertDialog.isShowing())
                    alertDialog.dismiss();
            }
        }, 1000);

        LingChuangUtils.getInstance().closeDevice(MyApplication.getInstance());





    }

    /**
     * ????????????
     */
    private void startBroadcast(String body) {
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }
        if (TextUtils.isEmpty(body)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog alertDialog = builder.setMessage("---???????????????????????????---").setCancelable(true)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (alertDialog != null && alertDialog.isShowing())
                        alertDialog.dismiss();
                }
            }, 1000);

        } else {
            PlayingRtspFragment playingRtspFragment = new PlayingRtspFragment();
            Bundle bundle = new Bundle();
//        bundle.putString("rtsp_url", "rtsp://172.16.5.158/1/");

            // texture_view.setUp("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov", null);
            //  texture_view.setUp("rtmp://202.69.69.180:443/webcast/bshdlive-pc", null);
          bundle.putString("rtsp_url", body.trim());
         //bundle.putString("rtsp_url", "rtmp://192.168.3.15/live/tiantainqin");
         //bundle.putString("rtsp_url", "rtmp://202.69.69.180:443/webcast/bshdlive-pc");
         //   bundle.putString("rtsp_url", "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov");
            playingRtspFragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.board_frame, playingRtspFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }
    /**
     * ??????????????????
     */
    private void stopBroadcast() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.setMessage("---????????????---").setCancelable(true)
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog != null && alertDialog.isShowing())
                    alertDialog.dismiss();
            }
        }, 1000);

        //????????????
        //????????????
        getoWhitBoardFragmeng();
    }

    private final int REQUEST_CODE_STREAM_RTSP = 199; //random num
    private String rtsp_url;

    /**
     * ????????????
     */
    private void startStudnetCast(String body) {
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }
        if (TextUtils.isEmpty(body)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog alertDialog = builder.setMessage("---?????????????????????---").setCancelable(true)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (alertDialog != null && alertDialog.isShowing())
                        alertDialog.dismiss();
                }
            }, 1000);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!DisplayService.Companion.isStreaming()) {
                    rtsp_url = body.trim();
                    startActivityForResult(DisplayService.Companion.sendIntent(), REQUEST_CODE_STREAM_RTSP);
                } else {
                    stopService(new Intent(this, DisplayService.class));
                }
            } else {
                QZXTools.popToast(getApplicationContext(), "????????????5.0,???????????????", false);
            }
        }
    }

    /**
     * ????????????
     */
    private void stopStudnetCast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!DisplayService.Companion.isStreaming()) {
                stopService(new Intent(this, DisplayService.class));
            }
        }
    }
    /**
     * ????????????
     */
    private boolean isunClock=true;
    private void enterLock() {
        if (customDialog!=null && customDialog.isShowing()){
           return;
        }
        customDialog = new CustomDialog(this);
        customDialog.show();

        Log.i("qin0513", "enterLock: ");
        //????????????????????????
        //?????????????????????
        setDialogeCallListener();
        //??????back???
        LingChuangUtils.getInstance().stopBack(MyApplication.getInstance());

    }
    private long tem1=0;
    private void setDialogeCallListener() {
        customDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if(i==KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount()==0) {
                    //todo ????????????????????????2???
                    Calendar calendar = Calendar.getInstance();
                    long timeInMillis = calendar.getTimeInMillis();
                    if (tem1!=0 && timeInMillis-tem1<700){
                        isunClock=false;
                    }else {
                        isunClock=true;
                    }
                    tem1=timeInMillis;

                    backClock(isunClock);
                    return true;



                }
                return false;
            }
        });
    }

    private void backClock(boolean isunClock) {
        if (isunClock){
            Log.i(TAG, "onKey: ");
            //??????tips
            TipsDialog tipsDialog = new TipsDialog();
            tipsDialog.setTipsStyle("???????????????????\n????????????????????????????????????????????????????????????????????????",
                    "??????", "??????", -1);
            //   tipsDialog.setBackNoMiss();
            tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
                @Override
                public void cancle() {
                    tipsDialog.dismissAllowingStateLoss();
                    setDialogeCallListener();
                }

                @Override
                public void confirm() {
                    tipsDialog.dismissAllowingStateLoss();
                    customDialog.dismiss();
                    InteractiveActivity.super.onBackPressed();
                }
            });


            tipsDialog.show(getSupportFragmentManager(), TipsDialog.class.getSimpleName());
        }



    }

    /**
     * ??????????????????
     * @param type
     */
    private void enterWhiteBoard(String type) {
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }

        //????????????????????????????????????

        /**
         * ??????sp????????????????????????????????????sp??????
         * */
        sp_msg = sp_last_msg.getString("Last_Msg", null);

        QZXTools.logE("qin123"+ sp_msg,null);
        //??????sp??????
        if (!TextUtils.isEmpty(sp_msg)) {
            Message message = mHandler.obtainMessage();
            message.what = Constant.ReceiveMessage;
            message.obj = sp_msg;
            mHandler.sendMessage(message);
        } else {
            if (TextUtils.isEmpty(type)){
                //????????????
                getoWhitBoardFragmeng();
                return;
            }
            //???????????????????????????
            if (!TextUtils.isEmpty(type)&& type.equals(MsgUtils.HEAD_END_VOTE)){
                //????????????
                getoWhitBoardFragmeng();
                return;
            }
            //?????????????????? ????????????
            if (!TextUtils.isEmpty(type)&& type.equals(MsgUtils.HEAD_START_CLASS)){
                //????????????
                getoWhitBoardFragmeng();
                return;
            }
            //?????????????????????  HEAD_UNLOCK
            if (!TextUtils.isEmpty(type)&& type.equals(MsgUtils.HEAD_UNLOCK)){
                //????????????
                getoWhitBoardFragmeng();
                //???????????????
                LingChuangUtils.getInstance().startBack(MyApplication.getInstance());
                return;
            }
            //?????????????????????????????????
            if (!TextUtils.isEmpty(type)&& type.equals(MsgUtils.HEAD_END_ANSWER)){
                //????????????
                getoWhitBoardFragmeng();
                return;
            }
        }


    }

    private void getoWhitBoardFragmeng() {
        NewWhiteBoardFragment whiteBoardFragment = new NewWhiteBoardFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, whiteBoardFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * ????????????
     */
    private void popToastInfo(String msg) {
        // QZXTools.popToast(this, msg, false);
    }

    /**
     * ??????????????????????????????
     */
    private void sendJoinClass() {
       // boolean isReconnected = sp_last_msg.getBoolean("isReconnected", false);


        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_JOINCLASS,
                MsgUtils.joinClass( SimpleClientNetty.getInstance().isReconnected()));
        //??????????????????sp
       // sp_last_msg.edit().putBoolean("isReconnected", true).commit();
    }
    /**
     * ????????????????????????
     */
    private void receivePraiseOrCriticism(boolean isPraised, String stName) {
        PraiseAndCriticismDialog dialog = new PraiseAndCriticismDialog();
        dialog.setDialogType(isPraised, stName.trim());
        dialog.show(getSupportFragmentManager(), PraiseAndCriticismDialog.class.getSimpleName());
    }

    private ResponderFragment responderFragment;

    /**
     * ??????????????????
     */
    private void startResponder() {
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }
        responderFragment = new ResponderFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, responderFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * ??????????????????
     */
    private void showResponder(String name) {
        if (responderFragment != null) {
            responderFragment.showResponseResult(name.trim());
        }
    }

    /**
     * ????????????
     */
    private void randomName(String name) {
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }
        randomNameDialog = new RandomNameDialog();
        randomNameDialog.setName(name.trim());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, randomNameDialog);
        fragmentTransaction.commitAllowingStateLoss();


    }


    //????????????????????????sdcard??????
    private static final String[] WriteReadPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //??????????????????
    private static final String[] RecordPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    /**
     * ??????????????????
     */
    private void fileReceive(String body) {

        enterWhiteBoard("");

        FileReceiveDialog fileReceiveDialog = new FileReceiveDialog();
        fileReceiveDialog.setFileBodyString(false, null, body);
        fileReceiveDialog.show(getSupportFragmentManager(), FileReceiveDialog.class.getSimpleName());
    }

    private String fileReceiveBody = null;

    private void judgeFileReceivePermission(String body) {
        //ZBVPermission.getInstance().setPermPassResult(this);

        if (!ZBVPermission.getInstance().hadPermissions(this, WriteReadPermissions)) {
            fileReceiveBody = body;
            ZBVPermission.getInstance().requestPermissions(this, WriteReadPermissions);
        } else {
            //??????????????????
            QZXTools.logD("?????????????????????SDCard");
            fileReceive(body);
        }
    }

    private VoteFragment voteFragment;

    /**
     * ????????????
     */
    private void startVote(String body) {
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }
        voteFragment = new VoteFragment();
        //?????????body?????????????????????
        voteFragment.setVoteId(body.trim());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, voteFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * ????????????
     */
    private void endVote() {
        //??????????????????
      /*  if (voteFragment != null && voteFragment.getVoteDialog() != null && voteFragment.getVoteDialog().isVisible()) {
            voteFragment.getVoteDialog().commitVote();
        }*/
        QZXTools.popToast(this,"???????????????",true);
        EventBus.getDefault().post("close_discuss", Constant.Close_Discuss_Img);

        //??????????????????  ????????????
        enterWhiteBoard(MsgUtils.HEAD_END_VOTE);
    }

    private QuestionFragment questionFragment;

    /**
     * ??????????????????
     */
    private void startPractice(String body) {
        //????????????
        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.PAPER_RECIEVE + "",
                MsgUtils.createPracticeStatus(MsgUtils.PAPER_RECIEVE));

        questionFragment = new QuestionFragment();
        //?????????body?????????????????????
        questionFragment.setPracticeId(body.trim());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, questionFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * ??????????????????
     */
    private void endPractice() {
        //?????????
        if (questionFragment != null)
            questionFragment.commitAnswer();
        //?????????????????????
        enterWhiteBoard("");
    }

    private GroupDiscussFragment groupDiscussFragment;

    /**
     * ??????????????????
     * ???????????????????????????????????????
     * ?????????????????????????????????????????????
     */
    private void startDiscuss(String body) {
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }
        groupDiscussFragment = new GroupDiscussFragment();
        //?????????body?????????????????????
        groupDiscussFragment.setDiscussId(body.trim());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, groupDiscussFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * ????????????
     */
    private void toFreeDiscuss(String disucssId) {
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }
        FreeSelectDiscussGroupFragment freeSelectDiscussGroupFragment = new FreeSelectDiscussGroupFragment();
        freeSelectDiscussGroupFragment.setDiscussId(disucssId);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, freeSelectDiscussGroupFragment);
        fragmentTransaction.commitAllowingStateLoss();

        //Dialog??????
//        SelectGroupFragment selectGroupFragment = new SelectGroupFragment();
//        selectGroupFragment.setDiscussId(disucssId);
//        selectGroupFragment.show(getSupportFragmentManager(), SelectGroupFragment.class.getSimpleName());
    }

    /**
     * ??????????????????????????????????????????Dialog,???????????????????????????
     */
    private void endDiscuss() {
        //??????????????????????????????
        if (groupDiscussFragment != null && groupDiscussFragment.isVisible()) {
            groupDiscussFragment.showConclusionView();
        }
        //?????????ImageActivity??????????????????
        EventBus.getDefault().post("close_discuss", Constant.Close_Discuss_Img);
    }

    /**
     * ??????PPT????????????
     */
    private void enterPPTCommand(String type, String id) {
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }
        WebViewFragment webViewFragment = new WebViewFragment();
        webViewFragment.setFlag(Integer.parseInt(type));
        webViewFragment.setInteractId(id);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, webViewFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * ????????????
     * <p>
     * StartAnswer 2b32707b834b4e1fb473cddba67deab6 4ec51ba42642496caf9d1ab400df1a9c;head=StartAnswer;body=4ec51ba42642496caf9d1ab400df1a9c
     * <p>
     * http://172.16.5.160:8090/wisdomclass/interface/homework/handDetail?homeworkid=c872c974cbea4074983cf921c759c27c&status=0
     */
    private long tem=0;
    private void answerQuestion(String body) {
        Log.i("qin0509", "handleMessage:2222222222 "+body);
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }
        //???????????????????????????
        Calendar calendar = Calendar.getInstance();
        long timeInMillis = calendar.getTimeInMillis();
        if (tem!=0 && timeInMillis-tem<700){
            return;
        }else {
            Log.i("qin0509", "handleMessage:11111111 ");
            questionOnlyPicFragment = new QuestionOnlyPicFragment();
            questionOnlyPicFragment.setPracticeId(body.trim());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.board_frame, questionOnlyPicFragment);
            Bundle bundle = new Bundle();
            bundle.putString("homeworkId", body);
            questionOnlyPicFragment.setArguments(bundle);

            fragmentTransaction.commitAllowingStateLoss();
        }
        tem=timeInMillis;
    }
    //-------------------------????????????------------------------------

    //-------------------------??????----------------------

    private Uri outputUri;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_STREAM_RTSP:
                    if (data != null && (requestCode == REQUEST_CODE_STREAM_RTSP
                            && resultCode == Activity.RESULT_OK)) {
                     //   initNotification();
                        DisplayService.Companion.setData(resultCode, data);
                        StudentInfo studentInfo = MyApplication.getInstance().getDaoSession().getStudentInfoDao().
                                queryBuilder().where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                        String path = QZXTools.getExternalStorageForFiles(MyApplication.getInstance(), null) + "/config.txt";
                        Properties properties = QZXTools.getConfigProperties(path);
                        String socketIp = properties.getProperty("socketIp");
                        Intent intent = new Intent(this, DisplayService.class);
                        Constant.RtmpUrl=Constant.RtmpUrl+socketIp+"/live/"+studentInfo.getUserId();
                        intent.putExtra("endpoint", Constant.RtmpUrl);
                        startService(intent);
                    } else {
                        Toast.makeText(this, "No permissions available", Toast.LENGTH_SHORT).show();
                        //button.setText(R.string.start_button);
                    }
                    isSubjective=true;

                    break;
                case ZBVPermission.ACTIVITY_REQUEST_CODE:
                    //????????????????????????????????????????????????????????????????????????  public static final int ACTIVITY_REQUEST_CODE = 0x9;??????
                    ZBVPermission.getInstance().onActivityResult(requestCode, resultCode, data);
                    isSubjective=true;
                    break;
                case SubjectiveToDoView.CODE_SYS_CAMERA:
                    //data???null,???????????????????????????????????????????????????
                    EventBus.getDefault().post("CAMERA_CALLBACK", Constant.Subjective_Camera_Callback);
                    isSubjective=true;
                    break;
                case GroupDiscussFragment.CODE_SYS_CAMERA:
                    //data???null
                    outputUri = createCropUri();

                    QZXTools.logE("cameraUri:" + GroupDiscussFragment.cameraUri, null);

//                    GroupDiscussFragment.cropPhoto(this, GroupDiscussFragment.cameraUri, outputUri);
                    GroupDiscussFragment.cropPhotoTwo(this, GroupDiscussFragment.cameraUri, outputUri);
                    isSubjective=true;
                    break;
                case GroupDiscussFragment.CODE_SYS_ALBUM:
                    if (data == null) {
                        return;
                    }
                    outputUri = createCropUri();
                    GroupDiscussFragment.cameraUri = data.getData();

//                    GroupDiscussFragment.cropPhoto(this, data.getData(), outputUri);
                    GroupDiscussFragment.cropPhotoTwo(this, data.getData(), outputUri);
                    isSubjective=true;
                    break;
                case GroupDiscussFragment.CODE_SYS_CROP:
                    String filePath = QZXTools.getRealFilePath(this, outputUri);
                    QZXTools.logE("filePath " + filePath + ";outputUri=" + outputUri, null);

                    if (data.getData() != null) {
                        QZXTools.logE("data " + data.getData(), null);
                        filePath = QZXTools.getExternalStorageForFiles(this, Environment.DIRECTORY_PICTURES)
                                + data.getData().toString().substring(data.getData().toString().lastIndexOf("/"));
                    }
                    QZXTools.logE("end filePath " + filePath, null);
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                    if (bitmap == null) {
                        QZXTools.logE("bitmap==null first", null);
                        bitmap = BitmapFactory.decodeFile(filePath);
                        if (bitmap == null) {
                            //?????????????????????????????????Android7.1.2???????????????????????????
                            filePath = QZXTools.getExternalStorageForFiles(this, Environment.DIRECTORY_PICTURES)
                                    + GroupDiscussFragment.cameraUri.toString()
                                    .substring(GroupDiscussFragment.cameraUri.toString().lastIndexOf("/"));
                            bitmap = BitmapFactory.decodeFile(filePath);

                            QZXTools.logE("bitmap==null " + GroupDiscussFragment.cameraUri + ";filePath=" + filePath, null);
                        }
                    }

                    compressQuality(filePath, bitmap);

                    EventBus.getDefault().post(filePath, Constant.Discuss_Send_Pic);
//                    groupDiscussFragment.sendDiscussMsg(filePath, MsgUtils.TYPE_PICTURE);
                    isSubjective=true;
                    break;
                case GroupDiscussFragment.CODE_CUSTOM_CROP:
                    String newFilePath = QZXTools.getRealFilePath(this, data.getData());
                    EventBus.getDefault().post(newFilePath, Constant.Discuss_Send_Pic);
                    isSubjective=true;
                    break;
                case Media_Projection_Shot_RequestCode:
                    //????????????????????????
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        mediaProjection = projectionManager.getMediaProjection(resultCode, data);
//                        if (mediaProjection != null) {

//                        QZXTools.logE("Projection_Shot date start =" + new Date().getTime(), null);

//                            startScreenShot();

                        Intent intent = new Intent(this, ScreenShotService.class);
                        intent.putExtra("result_code", resultCode);
                        intent.putExtra("data_intent", data);
                        intent.setPackage(getPackageName());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent);
                        } else {
                            startService(intent);
                        }

//                        QZXTools.logE("Projection_Shot date end =" + new Date().getTime(), null);


//                        }
                    }
                    isSubjective=true;
                    break;
                case Media_Projection_Record_RequestCode:
                    //????????????????????????
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        popIsRecordScreen = true;

                        Intent intent = new Intent(this, ScreenRecordService.class);
                        intent.putExtra("result_code", resultCode);
                        intent.putExtra("data_intent", data);
                        intent.setPackage(getPackageName());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent);
                        } else {
                            startService(intent);
                        }
                    }
                    isSubjective=true;
                    break;
                //???????????????????????????
                case CameraAlbumPopupFragment.CODE_SYS_CAMERA:
                    QZXTools.logE("camera img data=" + data, null);
                    outputUri = createCropUri();
//                    CameraAlbumPopupFragment.cropPhoto(this, CameraAlbumPopupFragment.cameraUri, outputUri);
                    CameraAlbumPopupFragment.cropPhotoTwo(this, CameraAlbumPopupFragment.cameraUri, outputUri);
                    isSubjective=true;
                    break;
                case CameraAlbumPopupFragment.CODE_SYS_CROP:
                    String cropPath = QZXTools.getRealFilePath(this, outputUri);
//                    QZXTools.logE("cropPath=" + cropPath + ";data=" + data.getData(), null);
                    if (data.getData() != null) {
                        cropPath = QZXTools.getExternalStorageForFiles(this, Environment.DIRECTORY_PICTURES)
                                + data.getData().toString().substring(data.getData().toString().lastIndexOf("/"));
                    }
                    Bitmap cropBm = BitmapFactory.decodeFile(cropPath);
                    if (cropBm == null) {
                        QZXTools.logE("bitmap==null first", null);
                        cropBm = BitmapFactory.decodeFile(cropPath);
                        if (cropBm == null) {
                            //?????????????????????????????????Android7.1.2???????????????????????????
                            cropPath = QZXTools.getExternalStorageForFiles(this, Environment.DIRECTORY_PICTURES)
                                    + CameraAlbumPopupFragment.cameraUri.toString()
                                    .substring(CameraAlbumPopupFragment.cameraUri.toString().lastIndexOf("/"));
                            cropBm = BitmapFactory.decodeFile(cropPath);

                            QZXTools.logE("bitmap==null " + CameraAlbumPopupFragment.cameraUri + ";filePath=" + cropPath, null);
                        }
                    }
                    compressQuality(cropPath, cropBm);
                    EventBus.getDefault().post(cropPath, Constant.Group_Conclusion_Pic);
                    isSubjective=true;
                    break;
                case CameraAlbumPopupFragment.CODE_CUSTOM_CROP:
                    String newCropPath = QZXTools.getRealFilePath(this, data.getData());
                    EventBus.getDefault().post(newCropPath, Constant.Group_Conclusion_Pic);
                    isSubjective=true;
                    break;
                case CameraAlbumPopupFragment.CODE_SYS_CAMERA_VIDEO:
                    //todo ??????????????????
                    QZXTools.logE("camera video data=" + data.getData(), null);
//content://com.telit.smartclass.desktop.fileprovider/camera_photos/Android/data/com.telit.smartclass.desktop/files/VIDEO_20191219_101652.mp4
//                    String videoPath = UriTool.getFilePathByUri(this, data.getData());
                    String videoPath = QZXTools.getRealFilePath(this, CameraAlbumPopupFragment.cameraUri);
                    String videoPath2 = UriTool.getFilePathByUri(this, CameraAlbumPopupFragment.cameraUri);
                    QZXTools.logE("camera video actual path =" + videoPath
                            + ";uri=" + CameraAlbumPopupFragment.cameraUri
                            + ";video2=" + videoPath2, null);

                    String actualPath = QZXTools.getExternalStorageForFiles(this, null)
                            + data.getData().toString().substring(data.getData().toString().lastIndexOf("/"));

                    QZXTools.logE("actualPath=" + actualPath, null);

                    EventBus.getDefault().post(actualPath, Constant.Group_Conclusion_Video);
                    isSubjective=true;
                    break;
            }
        }
    }
    private void initNotification() {
        Notification.Builder notificationBuilder =
                new Notification.Builder(this).setSmallIcon(R.drawable.notification_anim)
                        .setContentTitle("Streaming")
                        .setContentText("Display mode stream")
                        .setTicker("Stream in progress");
        notificationBuilder.setAutoCancel(true);
        if (notificationManager != null)
            notificationManager.notify(12345, notificationBuilder.build());
    }
    /**
     * ????????????Uri
     */
    private Uri createCropUri() {
        String fileDir = QZXTools.getExternalStorageForFiles(this, Environment.DIRECTORY_PICTURES);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IMG_");
        stringBuilder.append(simpleDateFormat.format(new Date()));
        stringBuilder.append(".jpg");
        File outputFile = new File(fileDir, stringBuilder.toString());

        QZXTools.logE("outputPATH=" + outputFile.getAbsolutePath(), null);

        return Uri.fromFile(outputFile);
    }

    /**
     * ????????????????????????????????????????????????
     */
    private void compressQuality(String filePath, Bitmap bitmap) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

    public interface  onCellNettyListener{
        void stopNetty(boolean closeNetty);
    }

    public void setonCellNettyListener(onCellNettyListener listener){

        this.listener = listener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    //?????? ?????????????????????
    @Override
    public void changeViewStatus(int status, String URL) {

    }

    @Override
    public void setPresenter(PusherContract.Presenter presenter) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void dismissProgress() {

    }

    @Override
    public void showTip(String message) {

    }

    @Subscriber(tag = Constant.Homework_Commit_Success, mode = ThreadMode.MAIN)
    public void SubmitQuestionSucess(String homeworkId) {
        Log.i("qin", "????????????????????????: " + homeworkId);

        //????????????????????????
        if (customDialog!=null && customDialog.isShowing()){
            customDialog.dismiss();
        }
        AskQueestionFragment  askQueestionFragment = new AskQueestionFragment();
        //?????????body?????????????????????
        askQueestionFragment.setHomeWordId(homeworkId);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, askQueestionFragment);
        fragmentTransaction.commitAllowingStateLoss();

        //??????????????????????????????  ????????????id
        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.SubmitQuestion,
                MsgUtils.SubmitQuestion());
    }
    //??????????????????????????????
    @Subscriber(tag = Constant.Homework_Commit_Success_Tijiao, mode = ThreadMode.MAIN)
    public void SubmitQuestion(String teacher_end) {
        Log.i("qin", "????????????????????????: " + teacher_end);

        //??????????????????????????????  ????????????id
        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.SubmitQuestion,
                MsgUtils.SubmitQuestion());
        if (teacher_end.equals("teacher_end")){
            Toast.makeText(this,"??????????????????????????????",Toast.LENGTH_LONG).show();
            enterWhiteBoard("");
        }else {
            Toast.makeText(this,"????????????????????????",Toast.LENGTH_LONG).show();
        }
    }
    //??????????????????????????????
private boolean isSubjective=false;
    @Subscriber(tag = Constant.Subjective_Board_Callback, mode = ThreadMode.MAIN)
    public void SubmitSubjective(ExtraInfoBean anster) {
        Log.i("qin", "???????????????????????????????????? " + anster);
        isSubjective=true;
    }
}
