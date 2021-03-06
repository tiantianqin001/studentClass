package com.telit.zhkt_three.Activity.AutonomousLearning;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.AutoLearning.PullOperationAdapter;
import com.telit.zhkt_three.Adapter.AutoLearning.RVAutoLearningAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.CustomHeadLayout;
import com.telit.zhkt_three.CustomView.ToUsePullView;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionGrade;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionKnowledge;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionParam;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionSection;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionSubject;
import com.telit.zhkt_three.JavaBean.Gson.KnowledgeParamBean;
import com.telit.zhkt_three.JavaBean.Gson.KnowledgeSectionBean;
import com.telit.zhkt_three.JavaBean.Gson.ResourceConditionBean;
import com.telit.zhkt_three.JavaBean.Gson.ResourceInfoBean;
import com.telit.zhkt_three.JavaBean.Resource.FillResource;
import com.telit.zhkt_three.JavaBean.Resource.LocalResourceRecord;
import com.telit.zhkt_three.JavaBean.Resource.ResourceBean;
import com.telit.zhkt_three.JavaBean.Resource.ResourceCondition;
import com.telit.zhkt_three.JavaBean.StudentInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.telit.zhkt_three.greendao.LocalResourceRecordDao;
import com.telit.zhkt_three.greendao.StudentInfoDao;
import com.zbv.meeting.util.SharedPreferenceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * ??????????????????????????????????????????????????????????????????
 * <p>
 * ???????????????Fragment??????????????????
 * <p>
 * ?????????????????????????????????/storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files/AutoLearningResources/
 * <p>
 * todo ???????????????ViewPager; ??????????????????????????????????????????
 * <p>
 */
public class AutoLearningActivity extends BaseActivity implements View.OnClickListener, ToUsePullView.SpinnerClickInterface {
    private Unbinder unbinder;

    @BindView(R.id.learning_headLayout)
    CustomHeadLayout customHeadLayout;
    @BindView(R.id.learning_micro)
    LinearLayout layout_micro;
    @BindView(R.id.learning_audio)
    LinearLayout layout_audio;
    @BindView(R.id.learning_picture)
    LinearLayout layout_picture;
    @BindView(R.id.learning_book)
    LinearLayout layout_book;
    @BindView(R.id.learning_item_bank)
    LinearLayout layout_item_bank;
    @BindView(R.id.learning_xRecycler)
    XRecyclerView xRecyclerView;

    //????????????
    @BindView(R.id.learning_pull_all)
    LinearLayout layout_pull_all;
    @BindView(R.id.learning_pull_layout)
    LinearLayout layout_pull;
    @BindView(R.id.learning_pull_subject_layout)
    RelativeLayout subject_layout;
    @BindView(R.id.learning_pull_section_layout)
    RelativeLayout section_layout;
    @BindView(R.id.learning_pull_select_layout)
    RelativeLayout select_layout;
    @BindView(R.id.learning_pull_grade_layout)
    RelativeLayout grade_layout;
    @BindView(R.id.learning_pull_press_layout)
    RelativeLayout press_layout;
    @BindView(R.id.learning_pull_subject)
    ToUsePullView subject_view;
    @BindView(R.id.learning_pull_section)
    ToUsePullView section_view;
    @BindView(R.id.learning_pull_select)
    ToUsePullView select_view;
    @BindView(R.id.learning_pull_grade)
    ToUsePullView grade_view;
    @BindView(R.id.learning_pull_press)
    ToUsePullView press_view;


    //???????????????????????????
    @BindView(R.id.leak_resource)
    ImageView leak_resource;
    @BindView(R.id.leak_net_layout)
    LinearLayout leak_net_layout;
    @BindView(R.id.link_network)
    TextView link_network;

    //-------------------------------------------????????????????????????,??????????????????
    @BindView(R.id.auto_learning_pull_tag)
    FrameLayout auto_learning_pull_tag;
    @BindView(R.id.auto_learning_pull_icon)
    ImageView auto_learning_pull_icon;

    //????????????checkbox to delete
    @BindView(R.id.pull_linear_red)
    LinearLayout pull_linear_red;
    @BindView(R.id.pull_tv_edit)
    TextView pull_tv_edit;
    @BindView(R.id.pull_cb_all)
    CheckBox pull_cb_all;
    @BindView(R.id.pull_recycler)
    RecyclerView pull_recycler;
    @BindView(R.id.pull_tv_del)
    TextView pull_tv_del;

    //todo ???????????????????????????????????????

    //??????
    private Animation FromRightToLeftAnimation;
    private Animation FromLeftToRightAnimation;

    private List<LocalResourceRecord> pullOperationBeans;

    private PullOperationAdapter pullOperationAdapter;

    //-------------------------------------------????????????????????????,??????????????????

    //??????????????????
    private CircleProgressDialogFragment circleProgressDialogFragment;

    private RVAutoLearningAdapter rvAutoLearningAdapter;
    private List<FillResource> fillResourceList;

    //?????????key???????????????
    //??????
    private Map<String, String> sectionMap;
    //??????
    private Map<String, String> subjectMap;
    //??????
    private Map<String, String> gradeMap;
    //?????????
    private Map<String, String> pressMap;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operate_Section_Success = 2;
    private static final int Operate_Subject_Grade_Success = 3;
    private static final int Operate_Resource_Condition_Success = 4;
    private static final int Operate_Resource_Success = 5;

    private static boolean isShow=false;

    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(AutoLearningActivity.this, "??????????????????", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if (xRecyclerView != null) {
                            xRecyclerView.refreshComplete();
                            xRecyclerView.loadMoreComplete();
                        }
                    }

                    break;
                case Error404:
                    if (isShow){
                        QZXTools.popToast(AutoLearningActivity.this, "?????????????????????", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                    }

                    break;
                case Operate_Section_Success:
                    if (isShow){
                        List<String> sectionList = new ArrayList<String>(sectionMap.keySet());
                        section_view.setDataList(sectionList);
                    }

                    break;
                case Operate_Subject_Grade_Success:
                    if (isShow){
                        countAdd++;

                        List<String> subjectList = new ArrayList<String>(subjectMap.keySet());
                        subject_view.setDataList(subjectList);

                        List<String> gradeList = new ArrayList<String>(gradeMap.keySet());
                        grade_view.setDataList(gradeList);

                        List<String> pressList = new ArrayList<String>(pressMap.keySet());
                        press_view.setDataList(pressList);
                        if (pressList != null && pressList.size() > 0) {
                            press_view.setPullContent(pressList.get(0));
                        }

                        if (countAdd == countRequest) {
                            if (circleProgressDialogFragment != null) {
                                circleProgressDialogFragment.dismissAllowingStateLoss();
                                circleProgressDialogFragment = null;
                            }
                        }

                        //???????????????????????????
                        if (xRecyclerView != null) {
                            xRecyclerView.refreshComplete();
                            xRecyclerView.loadMoreComplete();
                        }

                        if (fillResourceList.size() > 0) {
                            leak_resource.setVisibility(View.GONE);
                        } else {
                            leak_resource.setVisibility(View.VISIBLE);
                        }
                        rvAutoLearningAdapter.notifyDataSetChanged();

                        //????????????
                        xRecyclerView.setNoMore(true);
                    }

                    break;
                case Operate_Resource_Condition_Success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        List<String> xuekeList = new ArrayList<String>(subjectMap.keySet());
                        subject_view.setDataList(xuekeList);

                        List<String> nianjiList = new ArrayList<String>(gradeMap.keySet());
                        grade_view.setDataList(nianjiList);

                        List<String> chubansheList = new ArrayList<String>(pressMap.keySet());
                        press_view.setDataList(chubansheList);

                        List<String> xueqiList = new ArrayList<String>(sectionMap.keySet());
                        section_view.setDataList(xueqiList);

                        //????????????????????????????????????????????????
                        subject_view.setPullContent(xuekeList.get(0));
                        grade_view.setPullContent(nianjiList.get(0));
                        press_view.setPullContent(chubansheList.get(0));
                        section_view.setPullContent(xueqiList.get(0));

                        fetchNetworkForResourceContent(false, subjectMap.get(subject_view.getPullContent()),
                                gradeMap.get(grade_view.getPullContent()),
                                pressMap.get(press_view.getPullContent()),
                                sectionMap.get(section_view.getPullContent()));

                    }

                    break;
                case Operate_Resource_Success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if (xRecyclerView != null) {
                            xRecyclerView.refreshComplete();
                            xRecyclerView.loadMoreComplete();
                        }

                        if (fillResourceList.size() > 0) {
                            leak_resource.setVisibility(View.GONE);
                        } else {
                            leak_resource.setVisibility(View.VISIBLE);
                        }
                        rvAutoLearningAdapter.notifyDataSetChanged();
                    }

                    break;
            }
        }
    };

    /**
     * ???????????????????????????????????????
     */
    private long enterLearningTime;
    private static final String TAG="AutoLearenActivity";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_learning);
        unbinder = ButterKnife.bind(this);
        isShow=true;
        //??????????????????????????????????????????????????????android:keepScreenOn="true"
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //????????????????????????
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        //?????????????????????????????????
        EventBus.getDefault().register(this);

        //????????????????????????????????????
        enterLearningTime = System.currentTimeMillis();
        //??????????????????
        MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_ONE, -1, -1, "");
        //??????????????????
        MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_TWO, -1, -1, "");

        //?????????????????????
        StudentInfo studentInfo = MyApplication.getInstance().getDaoSession().getStudentInfoDao().queryBuilder()
                .where(StudentInfoDao.Properties.StudentId.eq(UserUtils.getStudentId())).unique();
        if (studentInfo != null) {
            String clazz;
            if (studentInfo.getClassName() != null) {
                if (studentInfo.getGradeName() != null) {
                    clazz = studentInfo.getGradeName().concat(studentInfo.getClassName());
                } else {
                    clazz = studentInfo.getClassName();
                }
            } else {
                clazz = "";
            }
            customHeadLayout.setHeadInfo(studentInfo.getPhoto(), studentInfo.getStudentName(), clazz);
        }

        //???????????????????????????
        fillResourceList = new ArrayList<>();
        //???????????????
        xRecyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        //?????????????????????????????????
        xRecyclerView.getDefaultFootView().setNoMoreHint("");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        xRecyclerView.setLayoutManager(gridLayoutManager);
        xRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvAutoLearningAdapter = new RVAutoLearningAdapter(this, fillResourceList);
        xRecyclerView.setAdapter(rvAutoLearningAdapter);

        //??????????????????
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                try {
                    curPageNo = 1;
                    if (isItemBank) {
                        fetchNetworkForSubjectGrade(sectionMap.get(section_view.getPullContent()), "1",
                                subjectMap.get(subject_view.getPullContent()), pressMap.get(press_view.getPullContent()));
                    } else {
                        fetchNetworkForResourceContent(false, subjectMap.get(subject_view.getPullContent()),
                                gradeMap.get(grade_view.getPullContent()), sectionMap.get(section_view.getPullContent()),
                                pressMap.get(press_view.getPullContent()));
                    }
                }catch (Exception e){
                    e.fillInStackTrace();
                    QZXTools.popToast(MyApplication.getInstance(),"???????????????",true);
                }

            }

            @Override
            public void onLoadMore() {

                try {
                    curPageNo++;
                    if (isItemBank) {

                    } else {
                        fetchNetworkForResourceContent(true, subjectMap.get(subject_view.getPullContent()),
                                gradeMap.get(grade_view.getPullContent()), sectionMap.get(section_view.getPullContent()),
                                pressMap.get(press_view.getPullContent()));
                    }
                }catch (Exception e){
                    e.fillInStackTrace();
                    QZXTools.popToast(MyApplication.getInstance(),"???????????????",true);
                }

            }
        });

        link_network.setOnClickListener(this);
        //?????????????????????
        layout_micro.setOnClickListener(this);
        layout_audio.setOnClickListener(this);
        layout_picture.setOnClickListener(this);
        layout_book.setOnClickListener(this);
        layout_item_bank.setOnClickListener(this);

        layout_pull_all.setOnClickListener(this);
        //?????????????????????????????????????????????????????????????????????
        layout_pull.setOnClickListener(this);

        //???????????????
        layout_micro.setSelected(true);
        auto_learning_pull_tag.setVisibility(View.VISIBLE);

        //??????????????????????????????????????????????????????????????????????????????
        leak_resource.setVisibility(View.GONE);

        subject_view.setSpinnerClick(this);
        section_view.setSpinnerClick(this);
        select_view.setSpinnerClick(this);
        grade_view.setSpinnerClick(this);
        press_view.setSpinnerClick(this);

        subjectMap = new LinkedHashMap<>();
        sectionMap = new LinkedHashMap<>();
        gradeMap = new LinkedHashMap<>();
        pressMap = new LinkedHashMap<>();


        //???????????????????????????
        press_layout.setVisibility(View.GONE);

        //??????????????????????????????
        List<String> papers = new ArrayList<>();
        papers.add("?????????");
        papers.add("??????");
        select_view.setDataList(papers);

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());
        //???????????????????????????????????????????????????
        fetchNetworkForResourceCondition();

        //-----------------------------------------????????????

        //????????????
        auto_learning_pull_tag.setOnClickListener(this);

        //????????????
        FromRightToLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.right_to_left_show);
        FromLeftToRightAnimation = AnimationUtils.loadAnimation(this, R.anim.left_to_right_hide);

        LocalResourceRecordDao localResourceRecordDao = MyApplication.getInstance().getDaoSession().getLocalResourceRecordDao();
        pullOperationBeans = localResourceRecordDao.queryBuilder().list();

        pull_tv_edit.setOnClickListener(this);
        pull_tv_del.setOnClickListener(this);

        pull_cb_all.setOnClickListener(this);

        pull_recycler.setLayoutManager(new LinearLayoutManager(this));

        pullOperationAdapter = new PullOperationAdapter(this, pullOperationBeans);

        pullOperationAdapter.setCheckedInterface(new PullOperationAdapter.CheckedInterface() {
            @Override
            public void checkedStatus(boolean hasChecked, int position) {
                boolean allChoosed = true;

                pullOperationBeans.get(position).setIsChoosed(hasChecked);
                pull_tv_del.setSelected(hasChecked);

                for (LocalResourceRecord localResourceRecord : pullOperationBeans) {
                    if (!localResourceRecord.getIsChoosed()) {
                        allChoosed = false;
                    }
                }

                //??????
                pull_cb_all.setChecked(allChoosed);
            }
        });

        pull_recycler.setAdapter(pullOperationAdapter);

        if (pullOperationBeans == null || pullOperationBeans.size() <= 0) {
            auto_learning_pull_tag.setVisibility(View.GONE);
        }

    }

    /**
     * todo ???????????????Activity????????????
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        isShow=false;

        EventBus.getDefault().unregister(this);

        if (circleProgressDialogFragment != null) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }

        //??????????????????
        mHandler.removeCallbacksAndMessages(null);
        OkHttp3_0Utils.getInstance().cleanHandler();
        QZXTools.setmToastNull();
        super.onDestroy();

        //??????????????????
        long exitLearningTime = System.currentTimeMillis();
 /*       MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_FIVE
                , (exitLearningTime - enterLearningTime), -1, "");*/

        String selfLearning = SharedPreferenceUtil.getInstance(MyApplication.getInstance()).getString("SelfLearning");
        BuriedPointUtils.buriedPoint("2034","","","",selfLearning);
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     */
    @Subscriber(tag = Constant.Auto_Learning_Update, mode = ThreadMode.MAIN)
    public void updateResources(String type) {
        pullOperationBeans = null;
        LocalResourceRecordDao localResourceRecordDao = MyApplication.getInstance().getDaoSession().getLocalResourceRecordDao();
        pullOperationBeans = localResourceRecordDao.queryBuilder().list();
        if (pullOperationBeans != null && pullOperationBeans.size() > 0) {
            switch (type) {
                case "item_bank":
                    auto_learning_pull_tag.setVisibility(View.GONE);
                    break;
                default:
                    auto_learning_pull_tag.setVisibility(View.VISIBLE);
                    break;
            }
            //??????????????????????????????????????????
            pullOperationAdapter.setPullOperationBeans(pullOperationBeans);
            pullOperationAdapter.notifyDataSetChanged();
        } else {
            auto_learning_pull_tag.setVisibility(View.GONE);
        }
    }

    /**
     * ????????????????????????
     */
    private int countRequest;
    private int countAdd;

    /**
     * {
     * "success": true,
     * "errorCode": "1",
     * "msg": "?????????????????????????????????",
     * "result": [
     * {
     * "id": 1,
     * "xd": 1,
     * "xdName": "??????"
     * }
     * ],
     * "total": 0,
     * "pageNo": 0
     * }
     * <p>
     * ????????????
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fetchNetworkForSection() {
        subjectMap.clear();
        sectionMap.clear();
        gradeMap.clear();
        pressMap.clear();

        subject_view.setHintText("????????????");
        section_view.setHintText("????????????");
        select_view.setHintText("????????????");
        grade_view.setHintText("????????????");
        press_view.setHintText("?????????");

        select_layout.setVisibility(View.VISIBLE);
        press_layout.setVisibility(View.GONE);

        countAdd = 0;

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());
        String url = UrlUtils.BaseUrl + UrlUtils.QueryKnowledgeSection;

        OkHttp3_0Utils.getInstance().asyncGetOkHttp(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
                //???????????????
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String resultJson = response.body().string();
//                            QZXTools.logE("resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        KnowledgeSectionBean knowledgeSectionBean = gson.fromJson(resultJson, KnowledgeSectionBean.class);
//                    QZXTools.logE("knowledgeSectionBean=" + knowledgeSectionBean, null);

                        countRequest = knowledgeSectionBean.getResult().size();

                        for (int i = 0; i < knowledgeSectionBean.getResult().size(); i++) {
                            QuestionSection questionSection = knowledgeSectionBean.getResult().get(i);
                            sectionMap.put(questionSection.getXdName(), questionSection.getXd() + "");
                            //??????????????????????????????
                            fetchNetworkForSubjectGrade(questionSection.getXd() + "", "0", null, null);
                        }
                        mHandler.sendEmptyMessage(Operate_Section_Success);
                    }catch (Exception e){
                        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        mHandler.sendEmptyMessage(Error404);
                    }

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }
    private void fetchNetworkForSubjectGrade(String xd, String isSearchChapter, String subjectId, String pressId) {
        String url = UrlUtils.BaseUrl + UrlUtils.QueryKnowledgeSubjectGrade;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("xd", xd);
        // 0:???????????????  1????????????
        mapParams.put("isSearchChapter", isSearchChapter);
        if (!TextUtils.isEmpty(subjectId)) {
            mapParams.put("chid", subjectId);
        }

        if (!TextUtils.isEmpty(pressId)) {
            mapParams.put("press", pressId);
        }

        /**
         * post????????????????????????int????????????????????????????????????????????????????????????
         * */
        //??????????????????
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //???????????????
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    try {
                        String resultJson = response.body().string();
//                            QZXTools.logE("resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        KnowledgeParamBean knowledgeSubjectGradeBean = gson.fromJson(resultJson, KnowledgeParamBean.class);
//                    QZXTools.logE("knowledgeSubjectGradeBean=" + knowledgeSubjectGradeBean, null);

                        QuestionParam questionParam = knowledgeSubjectGradeBean.getResult();

                        //??????
                        if (questionParam.getQuestionSubjects() != null && questionParam.getQuestionSubjects().size() > 0) {
                            for (int i = 0; i < questionParam.getQuestionSubjects().size(); i++) {
                                QuestionSubject questionSubject = knowledgeSubjectGradeBean.getResult().getQuestionSubjects().get(i);
                                subjectMap.put(questionSubject.getChname(), questionSubject.getChid() + "");
                            }
                        }

                        //??????
                        if (questionParam.getQuestionGrades() != null && questionParam.getQuestionGrades().size() > 0) {
                            for (int i = 0; i < questionParam.getQuestionGrades().size(); i++) {
                                QuestionGrade questionGrade = knowledgeSubjectGradeBean.getResult().getQuestionGrades().get(i);
                                gradeMap.put(questionGrade.getGradeName(), questionGrade.getGradeId() + "");
                            }
                        }

                        //?????????
                        if (questionParam.getQuestionEdition() != null && questionParam.getQuestionEdition().size() > 0) {
                            for (int i = 0; i < questionParam.getQuestionEdition().size(); i++) {
                                QuestionKnowledge questionKnowledge = questionParam.getQuestionEdition().get(i);
                                //?????????????????????id
                                pressMap.put(questionKnowledge.getName(), questionKnowledge.getKnowledgeId() + "");
                            }
                        }

                        //????????????QuestionGradeVolum
                        if (questionParam.getQuestionGradeVolum() != null && questionParam.getQuestionGradeVolum().size() > 0) {

                            //?????????????????????
                            fillResourceList.clear();

                            for (int i = 0; i < questionParam.getQuestionGradeVolum().size(); i++) {
                                //???QuestionKnowledge?????????FillResource
                                QuestionKnowledge questionKnowledge = questionParam.getQuestionGradeVolum().get(i);

                                FillResource fillResource = new FillResource();
                                fillResource.setCover("");
                                fillResource.setGradename(questionKnowledge.getName());
                                fillResource.setTermname("");

                                displayItemBankText(questionKnowledge.getParentId(), questionKnowledge.getXd(), questionKnowledge.getChid());

                                fillResource.setPressname(pressName);
                                fillResource.setSubjectName(subjectName);

                                if (type.equals("1010")) {
                                    fillResource.setTeachingMaterial(true);
                                } else {
                                    fillResource.setTeachingMaterial(false);
                                }
                                fillResource.setTitle(TitleText);
                                fillResource.setType(type);
                                fillResource.setItemBank(true);


                                fillResource.setChid(questionKnowledge.getChid());
                                fillResource.setKnowledgeId(questionKnowledge.getKnowledgeId() + "");
                                fillResource.setXd(questionKnowledge.getXd());
                                fillResource.setId(questionKnowledge.getId()+"");

                                fillResourceList.add(fillResource);
                            }
                        }

                        mHandler.sendEmptyMessage(Operate_Subject_Grade_Success);
                    }catch (Exception e){
                        e.fillInStackTrace();
                        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        mHandler.sendEmptyMessage(Error404);
                    }

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    /**
     * {
     * "success": true,
     * "errorCode": "1",
     * "msg": "????????????",
     * "result": [
     * [
     * {
     * "id": "1010",
     * "name": "????????????",
     * "code": null
     * },
     * {
     * "id": "0",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "1",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "2",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "3",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "4",
     * "name": "?????????",
     * "code": null
     * }
     * ],
     * [
     * {
     * "id": null,
     * "name": "?????????",
     * "code": "1"
     * },
     * {
     * "id": null,
     * "name": "?????????",
     * "code": "2"
     * },
     * {
     * "id": null,
     * "name": "?????????",
     * "code": "3"
     * },
     * {
     * "id": null,
     * "name": "?????????",
     * "code": "4"
     * },
     * {
     * "id": null,
     * "name": "?????????",
     * "code": "5"
     * },
     * {
     * "id": null,
     * "name": "?????????",
     * "code": "6"
     * },
     * {
     * "id": null,
     * "name": "?????????",
     * "code": "7"
     * },
     * {
     * "id": null,
     * "name": "?????????",
     * "code": "8"
     * },
     * {
     * "id": null,
     * "name": "?????????",
     * "code": "9"
     * },
     * {
     * "id": null,
     * "name": "??????",
     * "code": "10"
     * },
     * {
     * "id": null,
     * "name": "??????",
     * "code": "11"
     * },
     * {
     * "id": null,
     * "name": "??????",
     * "code": "12"
     * }
     * ],
     * [
     * {
     * "id": "0",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "1",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "10",
     * "name": "????????????",
     * "code": null
     * },
     * {
     * "id": "11",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "12",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "13",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "14",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "15",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "16",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "17",
     * "name": "????????????",
     * "code": null
     * },
     * {
     * "id": "18",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "19",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "2",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "20",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "21",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "22",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "28",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "29",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "3",
     * "name": "???????????????",
     * "code": null
     * },
     * {
     * "id": "30",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "31",
     * "name": "???????????????",
     * "code": null
     * },
     * {
     * "id": "32",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "33",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "34",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "35",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "36",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "37",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "38",
     * "name": "??????/??????",
     * "code": null
     * },
     * {
     * "id": "39",
     * "name": "??????/??????",
     * "code": null
     * },
     * {
     * "id": "4",
     * "name": "???????????????",
     * "code": null
     * },
     * {
     * "id": "40",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "41",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "42",
     * "name": "2+1",
     * "code": null
     * },
     * {
     * "id": "5",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "6",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "7",
     * "name": "????????????",
     * "code": null
     * },
     * {
     * "id": "8",
     * "name": "??????",
     * "code": null
     * },
     * {
     * "id": "9",
     * "name": "????????????",
     * "code": null
     * }
     * ],
     * [
     * {
     * "id": "0",
     * "name": "?????????",
     * "code": null
     * },
     * {
     * "id": "1",
     * "name": "?????????",
     * "code": null
     * },
     * {
     * "id": "9",
     * "name": "??????",
     * "code": null
     * }
     * ],
     * [
     * {
     * "id": "0",
     * "name": "?????????",
     * "code": null
     * },
     * {
     * "id": "11",
     * "name": "?????????2016",
     * "code": null
     * },
     * {
     * "id": "1",
     * "name": "?????????",
     * "code": null
     * },
     * {
     * "id": "2",
     * "name": "?????????",
     * "code": null
     * },
     * {
     * "id": "40",
     * "name": "?????????????????????",
     * "code": null
     * },
     * {
     * "id": "42",
     * "name": "?????????",
     * "code": null
     * },
     * {
     * "id": "30",
     * "name": "?????????",
     * "code": null
     * },
     * {
     * "id": "87",
     * "name": "?????????????????????",
     * "code": null
     * },
     * {
     * "id": "88",
     * "name": "?????????????????????",
     * "code": null
     * },
     * {
     * "id": "90",
     * "name": "?????????????????????",
     * "code": null
     * },
     * {
     * "id": "91",
     * "name": "???????????????????????????",
     * "code": null
     * },
     * {
     * "id": "92",
     * "name": "?????????????????????",
     * "code": null
     * },
     * {
     * "id": "93",
     * "name": "?????????????????????",
     * "code": null
     * },
     * {
     * "id": "98",
     * "name": "?????????????????????",
     * "code": null
     * },
     * {
     * "id": "99",
     * "name": "??????",
     * "code": null
     * }
     * ]
     * ],
     * "total": 0,
     * "pageNo": 0
     * }
     * <p>
     * ??????????????????
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fetchNetworkForResourceCondition() {
        //??????????????????
        subjectMap.clear();
        sectionMap.clear();
        gradeMap.clear();
        pressMap.clear();

        subject_view.setHintText("????????????");
        section_view.setHintText("????????????");
        grade_view.setHintText("????????????");
        press_view.setHintText("?????????");

        select_layout.setVisibility(View.GONE);
        press_layout.setVisibility(View.VISIBLE);

        String url = UrlUtils.BaseUrl + UrlUtils.ConditionResource;

        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("schoolid", UserUtils.getStudentId());

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
                //???????????????
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resultJson = response.body().string();
//                    QZXTools.logE("resultJson=" + resultJson, null);

                    try {
                        Gson gson = new Gson();
                        ResourceConditionBean resourceConditionBean = gson.fromJson(resultJson, ResourceConditionBean.class);
                        ResourceConditionBean.ResultBean result = resourceConditionBean.getResult();

                        //????????????
                        List<ResourceConditionBean.ResultBean.PeriodBean> period = result.getPeriod();
                        for (ResourceConditionBean.ResultBean.PeriodBean periodBean : period) {
                           // gradeMap.put(periodBean.getName(),periodBean.getId());
                        }
                        //????????????
                        List<ResourceConditionBean.ResultBean.SubjectBean> subject = result.getSubject();
                        for (ResourceConditionBean.ResultBean.SubjectBean subjectBean : subject) {
                            subjectMap.put(subjectBean.getName(),subjectBean.getId());
                        }
                        //????????????
                        List<ResourceConditionBean.ResultBean.GradeBean> grade = result.getGrade();
                        for (ResourceConditionBean.ResultBean.GradeBean gradeBean : grade) {
                            gradeMap.put(gradeBean.getName(), gradeBean.getCode());
                        }

                        Log.i(TAG, "onResponse: "+gradeMap);
                        //?????????
                        List<ResourceConditionBean.ResultBean.PressBean> press = result.getPress();
                        for (ResourceConditionBean.ResultBean.PressBean pressBean : press) {
                            pressMap.put(pressBean.getName(), pressBean.getId());
                        }
                        //????????????
                        List<ResourceConditionBean.ResultBean.TermBean> term = result.getTerm();
                        for (ResourceConditionBean.ResultBean.TermBean termBean : term) {
                            sectionMap.put(termBean.getName(), termBean.getId());
                        }

                        mHandler.sendEmptyMessage(Operate_Resource_Condition_Success);
                    }catch (Exception e){
                        e.fillInStackTrace();
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        mHandler.sendEmptyMessage(Server_Error);
                    }

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    private int curPageNo = 1;
    private String type = "3";

    /**
     * ???????????????????????????
     *
     * @param isLoadingMore ??????????????????????????????????????????????????????list??????
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fetchNetworkForResourceContent(boolean isLoadingMore, String subjectId, String gradeId, String termId, String pressId) {

        //??????????????????
        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);
            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        QZXTools.logE("isLoadingMore=" + isLoadingMore + ";subjectId=" + subjectId
                + ";gradeId=" + gradeId + ";termId=" + termId + ";pressId=" + pressId + ";curPageNo="
                + curPageNo + ";type=" + type+";url="+UrlUtils.BaseUrl + UrlUtils.OldResource, null);

        if (!isLoadingMore) {
            fillResourceList.clear();
            //???????????????????????????????????????????????????Scrapped or attached views may not be recycled
            rvAutoLearningAdapter.notifyDataSetChanged();
        }

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());
        String url = UrlUtils.BaseUrl + UrlUtils.OldResource;

        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("pageNo", curPageNo + "");
        paraMap.put("pageSize", "30");
        paraMap.put("suffix", type);
        if (!TextUtils.isEmpty(subjectId)) {
            paraMap.put("subjectid", subjectId);
        }

        if (!TextUtils.isEmpty(gradeId)) {
            paraMap.put("gradeid", gradeId);
        }

        if (!TextUtils.isEmpty(termId)) {
            paraMap.put("term", termId);
        }

        if (!TextUtils.isEmpty(pressId)) {
            paraMap.put("press", pressId);
        }

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
                //???????????????
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String resultJson = response.body().string();
                        QZXTools.logE("resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        ResourceInfoBean resourceInfoBean = gson.fromJson(resultJson, ResourceInfoBean.class);
                        List<ResourceBean> resourceBeanList = resourceInfoBean.getResult();
                        for (int i = 0; i < resourceBeanList.size(); i++) {
                            FillResource fillResource = new FillResource();
                            fillResource.setId(resourceBeanList.get(i).getId());
                            fillResource.setCover(resourceBeanList.get(i).getCover());
                            fillResource.setGradename(resourceBeanList.get(i).getGradename());
                            fillResource.setPressname(resourceBeanList.get(i).getPressname());
                            if (type.equals("1010")) {
                                fillResource.setTeachingMaterial(true);
                            } else {
                                fillResource.setTeachingMaterial(false);
                            }
                            fillResource.setTermname(resourceBeanList.get(i).getTermname());
                            fillResource.setTitle(resourceBeanList.get(i).getTitle());
                            fillResource.setType(type);
                            fillResource.setItemBank(false);
                            fillResource.setSubjectName(subject_view.getPullContent());
                            fillResource.setSubjectId(subjectMap.get(subject_view.getPullContent()));

                            fillResourceList.add(fillResource);
                        }
                        mHandler.sendEmptyMessage(Operate_Resource_Success);
                    }catch (Exception e){
                        e.fillInStackTrace();
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        mHandler.sendEmptyMessage(Server_Error);
                    }

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    /**
     * ???????????????
     */
    private boolean isItemBank = false;

    /**
     * ??????????????????s
     */
    private boolean isShown = false;
    private int preValue;

    /**
     * ?????????3
     * ?????????2
     * ?????????1
     * ???????????????1010
     * <p>
     * ??????????????????????????????????????????????????????????????????????????????????????????
     * <p>
     * ??????????????????????????????????????????????????????????????????????????????
     * ???????????????????????????????????? fetchNetworkForResourceCondition()
     * ?????????fetchNetworkForSection() ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.learning_micro:
                resetViewInterface(false);

                if (pullOperationBeans != null && pullOperationBeans.size() > 0) {
                    auto_learning_pull_tag.setVisibility(View.VISIBLE);
                }

                type = "3";

                layout_micro.setSelected(true);
                layout_audio.setSelected(false);
                layout_picture.setSelected(false);
                layout_book.setSelected(false);
                layout_item_bank.setSelected(false);

                layout_pull_all.setVisibility(View.VISIBLE);

                fetchNetworkForResourceCondition();
                break;
            case R.id.learning_audio:
                resetViewInterface(false);

                if (pullOperationBeans != null && pullOperationBeans.size() > 0) {
                    auto_learning_pull_tag.setVisibility(View.VISIBLE);
                }

                type = "2";

                layout_micro.setSelected(false);
                layout_audio.setSelected(true);
                layout_picture.setSelected(false);
                layout_book.setSelected(false);
                layout_item_bank.setSelected(false);

                layout_pull_all.setVisibility(View.VISIBLE);

                fetchNetworkForResourceCondition();
                break;
            case R.id.learning_picture:
                resetViewInterface(false);

                if (pullOperationBeans != null && pullOperationBeans.size() > 0) {
                    auto_learning_pull_tag.setVisibility(View.VISIBLE);
                }

                type = "1";

                layout_micro.setSelected(false);
                layout_audio.setSelected(false);
                layout_picture.setSelected(true);
                layout_book.setSelected(false);
                layout_item_bank.setSelected(false);

                layout_pull_all.setVisibility(View.VISIBLE);

                fetchNetworkForResourceCondition();
                break;
            case R.id.learning_book:
                resetViewInterface(false);

                if (pullOperationBeans != null && pullOperationBeans.size() > 0) {
                    auto_learning_pull_tag.setVisibility(View.VISIBLE);
                }

                type = "1010";

                layout_micro.setSelected(false);
                layout_audio.setSelected(false);
                layout_picture.setSelected(false);
                layout_book.setSelected(true);
                layout_item_bank.setSelected(false);

                layout_pull_all.setVisibility(View.VISIBLE);

                fetchNetworkForResourceCondition();
                break;
            case R.id.learning_item_bank:

                if (pullOperationBeans != null && pullOperationBeans.size() > 0) {
                    auto_learning_pull_tag.setVisibility(View.GONE);
                }

                type = "item_bank";

                resetViewInterface(true);

                layout_micro.setSelected(false);
                layout_audio.setSelected(false);
                layout_picture.setSelected(false);
                layout_book.setSelected(false);
                layout_item_bank.setSelected(true);

                layout_pull_all.setVisibility(View.VISIBLE);

                /**
                 * ?????????????????????????????????????????????????????????????????????????????????????????????
                 * ??????????????????
                 * */
                fetchNetworkForSection();
                break;
            case R.id.link_network:
                QZXTools.enterWifiSetting(this);
                break;
            case R.id.learning_pull_all:
                //????????????????????????????????????????????????
                if (subject_view.pullViewPopShown() || section_view.pullViewPopShown()
                        || select_view.pullViewPopShown() || grade_view.pullViewPopShown()
                        || press_view.pullViewPopShown()) {
                    return;
                }
                layout_pull_all.setVisibility(View.GONE);
                break;
            case R.id.auto_learning_pull_tag:
                preValue = 0;
                if (isShown) {
                    //??????gone???????????????elecRes_pull_content_layout.getMeasuredWidth()
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, pull_linear_red.getMeasuredWidth());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)
                                    auto_learning_pull_tag.getLayoutParams();
                            int value = (int) animation.getAnimatedValue();
                            int offset = value - preValue;
                            layoutParams.rightMargin -= offset;
                            auto_learning_pull_tag.setLayoutParams(layoutParams);
                            preValue = value;
                        }
                    });
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            iconRotate(auto_learning_pull_icon, 180.0f, 0.0f);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            pull_linear_red.setVisibility(View.INVISIBLE);
                            isShown = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    valueAnimator.setDuration(500);
                    pull_linear_red.startAnimation(FromLeftToRightAnimation);
                    valueAnimator.start();
                } else {
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, pull_linear_red.getMeasuredWidth());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)
                                    auto_learning_pull_tag.getLayoutParams();
                            int value = (int) animation.getAnimatedValue();
                            int offset = value - preValue;
                            layoutParams.rightMargin += offset;
                            auto_learning_pull_tag.setLayoutParams(layoutParams);
                            preValue = value;
                        }
                    });
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            iconRotate(auto_learning_pull_icon, 0f, 180.0f);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isShown = true;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    valueAnimator.setDuration(500);
                    pull_linear_red.setVisibility(View.VISIBLE);
                    pull_linear_red.startAnimation(FromRightToLeftAnimation);
                    valueAnimator.start();
                }
                break;
            case R.id.pull_tv_edit:
                if (pull_tv_edit.getText().toString().trim().equals("??????")) {
                    pull_cb_all.setVisibility(View.VISIBLE);
                    //???????????????selected??????
                    pull_tv_del.setVisibility(View.VISIBLE);

                    for (LocalResourceRecord localResourceRecord : pullOperationBeans) {
                        localResourceRecord.setCanChecked(true);
                    }

                    pullOperationAdapter.notifyDataSetChanged();

                    pull_tv_edit.setText("??????");
                } else if (pull_tv_edit.getText().toString().trim().equals("??????")) {
                    pull_cb_all.setVisibility(View.GONE);
                    pull_tv_del.setVisibility(View.GONE);

                    for (LocalResourceRecord localResourceRecord : pullOperationBeans) {
                        localResourceRecord.setCanChecked(false);
                    }

                    pullOperationAdapter.notifyDataSetChanged();

                    pull_tv_edit.setText("??????");
                }
                break;
            case R.id.pull_tv_del:
                if (pull_tv_del.isSelected()) {
                    LocalResourceRecordDao localResourceRecordDao = MyApplication.getInstance().getDaoSession()
                            .getLocalResourceRecordDao();
                    List<LocalResourceRecord> delResources = new ArrayList<>();
                    //???????????????????????????????????????
                    for (LocalResourceRecord localResourceRecord : pullOperationBeans) {
                        if (localResourceRecord.getIsChoosed()) {
                            delResources.add(localResourceRecord);
                        }
                    }
                    pullOperationBeans.removeAll(delResources);
                    localResourceRecordDao.deleteInTx(delResources);
                    pullOperationAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.pull_cb_all:
                //????????????:???onCheckedChanged?????????????????????????????????????????????????????????isChecked=true,?????????????????????
                if (pull_cb_all.isChecked()) {
                    pull_cb_all.setChecked(true);

                    //????????????
                    pull_tv_del.setSelected(true);

                    for (LocalResourceRecord localResourceRecord : pullOperationBeans) {
                        localResourceRecord.setIsChoosed(true);
                    }

                    pullOperationAdapter.notifyDataSetChanged();
                } else {
                    pull_cb_all.setChecked(false);

                    //??????
                    pull_tv_del.setSelected(false);

                    for (LocalResourceRecord localResourceRecord : pullOperationBeans) {
                        localResourceRecord.setIsChoosed(false);
                    }

                    pullOperationAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void spinnerClick(View parent, String text) {
        switch (parent.getId()) {
            case R.id.learning_pull_subject:
                subject_view.setPullContent(text);
                break;
            case R.id.learning_pull_section:
                section_view.setPullContent(text);
                break;
            case R.id.learning_pull_select:
                //????????????????????????????????????????????????
                if (text.equals(getResources().getString(R.string.knowledge_point))) {
                    if (subject_view.getPullContent().equals("") || section_view.getPullContent().equals("")) {
                        QZXTools.popToast(this, "???????????????????????????", false);
                        return;
                    }
                    select_view.setPullContent(text);
                    press_layout.setVisibility(View.GONE);
                    //??????????????????????????????????????????,?????????????????????
                    //Intent intent = new Intent(this, ItemBankKnowledgeActivity.class);
                    Intent intent = new Intent(this, ItemBankKnowledgeActivity1.class);
                    //?????????????????????
                    String learning_section = sectionMap.get(section_view.getPullContent());
                    String subject = subjectMap.get(subject_view.getPullContent());
                    QZXTools.logE("learning_section=" + learning_section + ";subject=" + subject, null);
                    intent.putExtra("learning_section", learning_section);
                    intent.putExtra("subject", subject);
                    startActivity(intent);
                    return;
                } else if (text.equals(getResources().getString(R.string.teaching_material))) {
                    if (section_view.getPullContent().equals("")) {
                        QZXTools.popToast(this, "??????????????????", false);
                        return;
                    }
                    select_view.setPullContent(text);
                    press_layout.setVisibility(View.VISIBLE);
                    fetchNetworkForSubjectGrade(sectionMap.get(section_view.getPullContent()), "1",
                            subjectMap.get(subject_view.getPullContent()), pressMap.get(press_view.getPullContent()));
                    return;
                }
                break;
            case R.id.learning_pull_grade:
                grade_view.setPullContent(text);
                break;
            case R.id.learning_pull_press:
                press_view.setPullContent(text);
                break;
        }
        //????????????---??????????????????
        if (isItemBank) {
            if (subject_view.getPullContent().equals("") || section_view.getPullContent().equals("")
                    || press_view.getPullContent().equals("")) {
                return;
            }
            fetchNetworkForSubjectGrade(sectionMap.get(section_view.getPullContent()), "1",
                    subjectMap.get(subject_view.getPullContent()), pressMap.get(press_view.getPullContent()));
        } else {
            fetchNetworkForResourceContent(false, subjectMap.get(subject_view.getPullContent()),
                    gradeMap.get(grade_view.getPullContent()), sectionMap.get(section_view.getPullContent()),
                    pressMap.get(press_view.getPullContent()));
        }
    }

    /**
     * ????????????
     */
    private void resetViewInterface(Boolean isItemBank) {
        //????????????
        curPageNo = 1;
        //?????????????????????
        this.isItemBank = isItemBank;
        //????????????????????????
        fillResourceList.clear();
        rvAutoLearningAdapter.notifyDataSetChanged();
        //??????????????????
        if (isItemBank) {
            subjectMap.clear();
            sectionMap.clear();
            gradeMap.clear();
            pressMap.clear();

            subject_view.setHintText("????????????");
            section_view.setHintText("????????????");
            select_view.setHintText("????????????");
            grade_view.setHintText("????????????");
            press_view.setHintText("?????????");

            subject_view.setPullContent("");
            section_view.setPullContent("");
            select_view.setPullContent("");
            grade_view.setPullContent("");
            press_view.setPullContent("");

            select_layout.setVisibility(View.VISIBLE);
            press_layout.setVisibility(View.GONE);
        } else {
            subjectMap.clear();
            sectionMap.clear();
            gradeMap.clear();
            pressMap.clear();

            subject_view.setHintText("????????????");
            section_view.setHintText("????????????");
            grade_view.setHintText("????????????");
            press_view.setHintText("?????????");

            subject_view.setPullContent("");
            section_view.setPullContent("");
            grade_view.setPullContent("");
            press_view.setPullContent("");

            select_layout.setVisibility(View.GONE);
            press_layout.setVisibility(View.VISIBLE);
        }

        //?????????????????????????????????????????????
        leak_resource.setVisibility(View.VISIBLE);
    }

    private String pressName;
    private String subjectName;
    private String sectionName;
    private String TitleText;

    /**
     * ????????????????????????
     */
    private void displayItemBankText(String parentId, String xd, String chid) {
        //??????parentId?????????????????????
        Iterator<Map.Entry<String, String>> iterator = pressMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (entry.getValue().equals(parentId)) {
                pressName = entry.getKey();
                break;
            }
        }
        //??????xd??????????????????
        Iterator<Map.Entry<String, String>> iterator_xd = sectionMap.entrySet().iterator();
        while (iterator_xd.hasNext()) {
            Map.Entry<String, String> entry = iterator_xd.next();
            if (entry.getValue().equals(xd)) {
                sectionName = entry.getKey();
                break;
            }
        }
        //??????chid????????????
        Iterator<Map.Entry<String, String>> iterator_xk = subjectMap.entrySet().iterator();
        while (iterator_xk.hasNext()) {
            Map.Entry<String, String> entry = iterator_xk.next();
            if (entry.getValue().equals(chid)) {
                subjectName = entry.getKey();
                break;
            }
        }

        //???????????????????????????
        if (!TextUtils.isEmpty(sectionName) && !TextUtils.isEmpty(subjectName)) {
            TitleText = sectionName.concat(subjectName);
        }
    }

    /**
     * ???????????????
     */
    private void iconRotate(View view, float fromDegrees, float toDegrees) {
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        view.startAnimation(rotateAnimation);
    }
}
