package com.telit.zhkt_three.Fragment.Dialog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.MediaTools.audio.play.PlayMP3;
import com.telit.zhkt_three.MediaTools.audio.record.MediaRecordVoice;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.MediaTools.video.VideoPlayerActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ZBVPermission;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.telit.zhkt_three.customNetty.MsgUtils;
import com.telit.zhkt_three.customNetty.SimpleClientNetty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: qzx
 * Date: 2019/12/16 17:09
 * <p>
 * ??????????????????????????????????????????
 * ????????????????????????????????????????????????????????????????????????
 * <p>
 * todo ???????????????????????????ImageView?????????????????????AnimationDrawable???
 * todo mp3lame??????so?????????jni
 * todo ????????????
 * todo ????????????
 */
public class DiscussConclusionFragment extends DialogFragment implements View.OnClickListener, ZBVPermission.PermPassResult {

    private Unbinder unbinder;

    @BindView(R.id.conclusion_tv_title)
    ImageView conclusion_tv_title;
    @BindView(R.id.conclusion_submit)
    ImageView conclusion_submit;

    @BindView(R.id.conclusion_words_relative)
    RelativeLayout conclusion_words_relative;
    //??????????????????
    @BindView(R.id.conclusion_text)
    TextView conclusion_text;
    @BindView(R.id.conclusion_words_del)
    ImageView conclusion_words_del;

    @BindView(R.id.conclusion_audio_relative)
    RelativeLayout conclusion_audio_relative;
    //??????????????????
    @BindView(R.id.conclusion_audio_layout)
    RelativeLayout conclusion_audio_layout;
    @BindView(R.id.conclusion_audio_img)
    ImageView conclusion_audio_img;
    @BindView(R.id.conclusion_audio_time)
    TextView conclusion_audio_time;
    @BindView(R.id.conclusion_audio_del)
    ImageView conclusion_audio_del;

    @BindView(R.id.conclusion_camera_relative)
    RelativeLayout conclusion_camera_relative;
    @BindView(R.id.conclusion_camera_title)
    TextView conclusion_camera_title;
    //??????????????????
    @BindView(R.id.conclusion_img_video_thumbnail)
    ImageView conclusion_img_video_thumbnail;
    @BindView(R.id.conclusion_video_or_img_del)
    ImageView conclusion_video_or_img_del;
    @BindView(R.id.conclusion_video_sign)
    ImageView conclusion_video_sign;

    @BindView(R.id.conclusion_words)
    LinearLayout conclusion_words;
    @BindView(R.id.conclusion_audio)
    LinearLayout conclusion_audio;
    @BindView(R.id.conclusion_camera)
    LinearLayout conclusion_camera;

    private static final String[] needPermissions = {Manifest.permission.RECORD_AUDIO};

    private String discussId;
    //????????????????????????
    private int discussGroupId;
    private String groupIndex;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //????????????????????????????????????
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_discuss_conclusion_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);

        getDialog().setCanceledOnTouchOutside(false);

        EventBus.getDefault().register(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            discussId = bundle.getString("discussId");
            discussGroupId = bundle.getInt("discussGroupId");
            groupIndex = bundle.getString("groupIndex");
        }

        //??????????????????
        conclusion_words_relative.setVisibility(View.GONE);
        conclusion_audio_relative.setVisibility(View.GONE);
        conclusion_camera_relative.setVisibility(View.GONE);

        //????????????
        conclusion_audio_layout.setOnClickListener(this);
        conclusion_img_video_thumbnail.setOnClickListener(this);

        //????????????
        conclusion_words_del.setOnClickListener(this);
        conclusion_audio_del.setOnClickListener(this);
        conclusion_video_or_img_del.setOnClickListener(this);

        //????????????
        conclusion_words.setOnClickListener(this);
        conclusion_camera.setOnClickListener(this);
        conclusion_submit.setOnClickListener(this);

        conclusion_audio.setOnTouchListener(new View.OnTouchListener() {
            boolean isCancelRecord = false;
            long startTime = 0;
            float startY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //??????????????????
                        ZBVPermission.getInstance().setPermPassResult(DiscussConclusionFragment.this);
                        if (!ZBVPermission.getInstance().hadPermissions(getActivity(), needPermissions)) {
                            ZBVPermission.getInstance().requestPermissions(getActivity(), needPermissions);
                            //??????????????????????????????
                            isCancelRecord = true;
                        } else {
                            if (conclusion_audio_relative.getVisibility() == View.VISIBLE) {
                                QZXTools.popCommonToast(getContext(), "?????????????????????????????????????????????????????????",
                                        false);
                                return false;
                            }
                            if (conclusion_camera_relative.getVisibility() == View.VISIBLE) {
                                QZXTools.popCommonToast(getContext(), "?????????????????????????????????????????????",
                                        false);
                                return false;
                            }
                            //????????????View,????????????
                            startTime = System.currentTimeMillis();
                            startY = event.getRawY();
                            boolean needRequest = requestOverlaysPermission();
                            if (needRequest) {
                                isCancelRecord = true;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float endY = event.getRawY();
                        QZXTools.logE("startY=" + startY + ";endY=" + endY, null);
                        if (startY - endY > 100) {
                            isCancelRecord = true;
                            updateRecordTips("????????????????????????");
                        } else if (startY - endY <= 0) {
                            isCancelRecord = false;
                            updateRecordTips("tips???????????????3??????,????????????");
                        }
                        //????????????
                        updateRecordPic(MediaRecordVoice.getInstance().byVolumnToLevel(14));
                        break;
                    case MotionEvent.ACTION_UP:
                        //?????????????????????????????????/????????????
                        long endTime = System.currentTimeMillis();
                        if (endTime - startTime < 1000) {
                            isCancelRecord = true;
                        }
                        if (isCancelRecord) {
                            removeRecordView();
                        } else {
                            removeRecordView();
                            //????????????????????????
                            audioFilePath = MediaRecordVoice.getInstance().getSaveRecordPath();
                            //????????????
                            conclusion_audio_relative.setVisibility(View.VISIBLE);
                            long time_s = endTime - startTime;
                            //????????????????????????
                            String shownTime = handlerAudioTime(time_s);
                            conclusion_audio_time.setText(shownTime);
                        }
                        //????????????
                        MediaRecordVoice.getInstance().stop();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        removeRecordView();
                        //????????????
                        MediaRecordVoice.getInstance().stop();
                        break;
                }
                return true;
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        ZBVPermission.getInstance().recyclerAll();
        EventBus.getDefault().unregister(this);
    }

    /**
     * ???????????????????????????????????????1???2??? 1???2???
     * ?????????3??????
     *
     * @param time ??????
     */
    private String handlerAudioTime(long time) {
        long second = time / 1000;
        if (second < 60) {
            //???
            return second + "???";
        } else {
            long minute = second / 60;
            long over = second % 60;
            return minute + "???" + over + "???";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.conclusion_words:
                if (conclusion_words_relative.getVisibility() == View.VISIBLE) {
                    QZXTools.popCommonToast(getContext(), "?????????????????????????????????????????????????????????", false);
                    return;
                }
                DiscussWordsRecordDialog wordsRecordDialog = new DiscussWordsRecordDialog();
                wordsRecordDialog.show(getChildFragmentManager(), DiscussWordsRecordDialog.class.getSimpleName());
                break;
//            case R.id.conclusion_audio:
//                break;
            case R.id.conclusion_camera:
                if (conclusion_camera_relative.getVisibility() == View.VISIBLE) {
                    QZXTools.popCommonToast(getContext(), "?????????????????????????????????????????????????????????", false);
                    return;
                }

                if (conclusion_audio_relative.getVisibility() == View.VISIBLE) {
                    QZXTools.popCommonToast(getContext(), "?????????????????????????????????????????????",
                            false);
                    return;
                }

                CameraAlbumPopupFragment cameraAlbumPopupFragment = new CameraAlbumPopupFragment();
                cameraAlbumPopupFragment.showSetting(true, true, false);
                cameraAlbumPopupFragment.show(getChildFragmentManager(), CameraAlbumPopupFragment.class.getSimpleName());
                break;
            case R.id.conclusion_submit:

                CircleProgressDialogFragment circleProgressDialogFragment = new CircleProgressDialogFragment();
                circleProgressDialogFragment.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

                String url = UrlUtils.BaseUrl + UrlUtils.DiscussConclusion;
                File file = null;
                Map<String, String> paraMap = new HashMap<String, String>();
                paraMap.put("discussGroupId", discussGroupId+"");
                paraMap.put("userId", UserUtils.getUserId());

                //???????????????????????????:??????????????? ???????????? > ???????????? > ????????????
                if (imgFilePath != null) {
                    file = new File(imgFilePath);
                } else if (videoFilePath != null) {
                    file = new File(videoFilePath);
                } else if (audioFilePath != null) {
                    file = new File(audioFilePath);
                }

                if (!TextUtils.isEmpty(conclusion_text.getText().toString().trim())) {
                    paraMap.put("groupConclusion", conclusion_text.getText().toString().trim());
                } else {
                    if (file == null) {
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                        }
                        QZXTools.popToast(getContext(), "??????????????????????????????", false);
                        return;
                    }
                }

                OkHttp3_0Utils.getInstance().asyncPostSingleOkHttp(url, "file", paraMap, file, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                QZXTools.popToast(getContext(), "??????????????????", false);
                                if (circleProgressDialogFragment != null)
                                    circleProgressDialogFragment.dismissAllowingStateLoss();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
//                        QZXTools.logE("conclusion response result=" + response.body().string(), null);
                        String result = response.body().string();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Gson gson = new Gson();
                                Map<String, Object> resultMap = gson.fromJson(result, new TypeToken<Map<String, Object>>() {
                                }.getType());

                                String msg = (String) resultMap.get("msg");

                                QZXTools.popToast(getContext(), msg, false);

                                if (circleProgressDialogFragment != null)
                                    circleProgressDialogFragment.dismissAllowingStateLoss();

                                //??????????????????
                                dismiss();

                                //???????????????????????????????????????????????????id
                                SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_DISCUSS_COMMIT_CONCLUSION,
                                        MsgUtils.feedbackConclusionEnd(discussGroupId + "", groupIndex));

                                //?????????????????????????????????
                                EventBus.getDefault().post("", Constant.Show_Conclusion);
                            }
                        });
                    }
                });

                break;
            case R.id.conclusion_words_del:
                conclusion_words_relative.setVisibility(View.GONE);
                break;
            case R.id.conclusion_audio_del:
                conclusion_audio_relative.setVisibility(View.GONE);
                //?????????????????????
                QZXTools.deleteFileOrDirectory(audioFilePath);
                audioFilePath = null;
                break;
            case R.id.conclusion_video_or_img_del:
                conclusion_camera_relative.setVisibility(View.GONE);
                //?????????????????????????????????
                QZXTools.deleteFileOrDirectory(imgFilePath);
                QZXTools.deleteFileOrDirectory(videoFilePath);
                imgFilePath = null;
                videoFilePath = null;
                break;
            case R.id.conclusion_audio_layout:
                //????????????
                AnimationDrawable animationDrawable = (AnimationDrawable) conclusion_audio_img.getDrawable();
                animationDrawable.start();
                PlayMP3.getInstance().playMusic(audioFilePath, false);
                break;
            case R.id.conclusion_img_video_thumbnail:
                if (isPic) {
                    //???????????????????????????
                    Intent intent = new Intent(getContext(), ImageLookActivity.class);
                    ArrayList<String> imgFilePathList = new ArrayList<>();
                    imgFilePathList.add(imgFilePath);
                    intent.putStringArrayListExtra("imgResources", imgFilePathList);
                    intent.putExtra("curImgIndex", 0);
                    getContext().startActivity(intent);
                } else {
                    //???????????????????????????
                    Intent intent_video = new Intent(getContext(), VideoPlayerActivity.class);
                    intent_video.putExtra("VideoFilePath", videoFilePath);
                    intent_video.putExtra("VideoTitle", "Discuss_Conclusion_MP4");
                    getContext().startActivity(intent_video);
                }
                break;
        }
    }

    /**
     * ????????????????????????
     * ?????????ThumbnailUtils???????????????????????????????????????????????????ThumbnailUtils????????????????????????????????????
     * ?????????????????????????????????????????????MICRO_KIND?????????????????????MICRO_KIND??????kind?????????????????????????????????
     *
     * @param videoPath ???????????????
     * @param width     ????????????????????????????????????
     * @param height    ???????????????????????????????????????
     * @param kind      ??????MediaStore.Images.Thumbnails???????????????MINI_KIND???MICRO_KIND???
     *                  ?????????MINI_KIND: 512 x 384???MICRO_KIND: 96 x 96
     * @return ??????????????????????????????
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                           int kind) {
        Bitmap bitmap = null;
        // ????????????????????????
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QZXTools.logE("DiscussConclusionFragment requestCode=" + requestCode + ";resultCode=" + resultCode, null);
        if (requestCode == OverlaysPermissionCode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getActivity())) {
                    Toast.makeText(getActivity(), "????????????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private static final int OverlaysPermissionCode = 0x107;

    /**
     * ????????????????????????WindowManager
     * ?????????SYSTEM_ALERT_WINDOW
     * ????????? ?????????????????????????????????????????????????????????
     * <p>
     * ???????????????view????????????????????????
     */
    private boolean requestOverlaysPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getActivity())) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                //??????Fragment??????onActivityResult
                startActivityForResult(intent, OverlaysPermissionCode);
                return true;
            }
        }

        //???????????????
        showRecordView();

        return false;
    }

    private WindowManager windowManager;

    private View view;

    private ImageView record_img;

    private TextView record_tips;

    private void showRecordView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_discuss_conclusion_audio, null);

        record_img = view.findViewById(R.id.conclusion_record_img);
        record_tips = view.findViewById(R.id.conclusion_record_tips);

        windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = getResources().getDimensionPixelSize(R.dimen.x400);
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.x400);
        layoutParams.gravity = Gravity.CENTER;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        windowManager.addView(view, layoutParams);

        MediaRecordVoice.getInstance().initRecord().start();
    }

    private void removeRecordView() {
        if (windowManager != null && view != null) {
            windowManager.removeView(view);
            record_img = null;
            record_tips = null;
            view = null;
        }
    }

    private void updateRecordTips(String tips) {
        if (record_tips != null) {
            record_tips.setText(tips);
        }
    }

    private void updateRecordPic(int level) {
        if (record_img == null) {
            return;
        }
        switch (level) {
            case 0:
                record_img.setImageResource(R.drawable.ease_record_animate_01);
                break;
            case 1:
                record_img.setImageResource(R.drawable.ease_record_animate_02);
                break;
            case 2:
                record_img.setImageResource(R.drawable.ease_record_animate_03);
                break;
            case 3:
                record_img.setImageResource(R.drawable.ease_record_animate_04);
                break;
            case 4:
                record_img.setImageResource(R.drawable.ease_record_animate_05);
                break;
            case 5:
                record_img.setImageResource(R.drawable.ease_record_animate_06);
                break;
            case 6:
                record_img.setImageResource(R.drawable.ease_record_animate_07);
                break;
            case 7:
                record_img.setImageResource(R.drawable.ease_record_animate_08);
                break;
            case 8:
                record_img.setImageResource(R.drawable.ease_record_animate_09);
                break;
            case 9:
                record_img.setImageResource(R.drawable.ease_record_animate_10);
                break;
            case 10:
                record_img.setImageResource(R.drawable.ease_record_animate_11);
                break;
            case 11:
                record_img.setImageResource(R.drawable.ease_record_animate_12);
                break;
            case 12:
                record_img.setImageResource(R.drawable.ease_record_animate_13);
                break;
            case 13:
                record_img.setImageResource(R.drawable.ease_record_animate_14);
                break;
        }
    }

    //----------------------??????????????????
    private boolean isPic = true;

    private String audioFilePath;
    private String videoFilePath;
    private String imgFilePath;

    @Subscriber(tag = Constant.Group_Conclusion_Pic, mode = ThreadMode.MAIN)
    public void showPic(String filePath) {
        imgFilePath = filePath;
        isPic = true;
        conclusion_camera_relative.setVisibility(View.VISIBLE);
        conclusion_video_sign.setVisibility(View.GONE);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        conclusion_img_video_thumbnail.setImageBitmap(bitmap);
    }

    @Subscriber(tag = Constant.Group_Conclusion_Video, mode = ThreadMode.MAIN)
    public void showVideo(String filePath) {
        videoFilePath = filePath;
        isPic = false;
        conclusion_camera_relative.setVisibility(View.VISIBLE);
        conclusion_video_sign.setVisibility(View.VISIBLE);
        Bitmap thumbnail = getVideoThumbnail(videoFilePath, getResources().getDimensionPixelSize(R.dimen.x700),
                getResources().getDimensionPixelSize(R.dimen.x400), MediaStore.Images.Thumbnails.MICRO_KIND);
        conclusion_img_video_thumbnail.setImageBitmap(thumbnail);
    }

    @Subscriber(tag = Constant.Group_Conclusion_Words, mode = ThreadMode.MAIN)
    public void showWords(String words) {
        if (TextUtils.isEmpty(words)) {
            return;
        }
        conclusion_words_relative.setVisibility(View.VISIBLE);
        conclusion_text.setText(words);
    }

    @Subscriber(tag = Constant.Play_Audio_Completed, mode = ThreadMode.MAIN)
    public void audioCompleted(String sign) {
        AnimationDrawable animationDrawable = (AnimationDrawable) conclusion_audio_img.getDrawable();
        animationDrawable.stop();
        //??????????????????
        animationDrawable.selectDrawable(0);
    }

    @Override
    public void grantPermission() {
        Toast.makeText(getActivity(), "???????????????????????????????????????", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void denyPermission() {
        QZXTools.logD("???????????????");
        Toast.makeText(getActivity(), "??????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
    }
}
