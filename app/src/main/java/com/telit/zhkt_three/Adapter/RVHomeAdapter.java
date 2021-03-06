package com.telit.zhkt_three.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.telit.zhkt_three.Activity.AfterHomeWork.AfterHomeWorkActivity;
import com.telit.zhkt_three.Activity.AutonomousLearning.AutoLearningActivity;
import com.telit.zhkt_three.Activity.ClassRecord.NewClassRecordActivity;
import com.telit.zhkt_three.Activity.MicroClass.MicroClassCenterActivity;
import com.telit.zhkt_three.Activity.MistakesCollection.MistakesCollectionActivity;
import com.telit.zhkt_three.Activity.PersonalSpace.PersonalSpaceActivity;
import com.telit.zhkt_three.Activity.PreView.PreViewActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/13 16:44
 */
public class RVHomeAdapter extends RecyclerView.Adapter<RVHomeAdapter.RVHomeViewHolder> {

    private static final int EMPTY_VIEW = 1;
    private Activity mContext;
    private List<Integer> datas;

    public RVHomeAdapter(Activity context, List<Integer> list) {
        mContext = context;
        datas = list;
    }
    @NonNull
    @Override
    public RVHomeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RVHomeViewHolder(LayoutInflater.from(mContext).inflate(R.layout.rv_home_item, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(@NonNull RVHomeViewHolder rvHomeViewHolder, int i) {
        rvHomeViewHolder.imageView.setTag(datas.get(i));
        rvHomeViewHolder.imageView.setImageResource(datas.get(i));
    }
    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class RVHomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;

        public RVHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.hudong_name);

//            QZXTools.logE("layoutPosition=" + getLayoutPosition(), null);//-1

            imageView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (QZXTools.canClick()) {
                switch ((int) v.getTag()) {
                    case R.mipmap.preview:
                        //????????????home?????????
                        lingChang();
                        mContext.startActivity(new Intent(mContext, PreViewActivity.class));
                        //mContext.overridePendingTransition(R.anim.right_to_left_show,R.anim.right_to_left_show);
                        //??????????????????
                        BuriedPointUtils.buriedPoint("2027","","","","");
                        break;
                    case R.mipmap.homework_after:
                        //????????????home?????????
                        lingChang();
                        mContext.startActivity(new Intent(mContext, AfterHomeWorkActivity.class));
                        //??????????????????
                        BuriedPointUtils.buriedPoint("2015","","","","");
                        break;
                    case R.mipmap.cuotiji:
                        //????????????home?????????
                        lingChang();
                        mContext.startActivity(new Intent(mContext, MistakesCollectionActivity.class));
                        //???????????????
                        BuriedPointUtils.buriedPoint("2019","","","","");
                        break;
                    case R.mipmap.auto_learning:
                        //????????????home?????????
                        lingChang();
                        mContext.startActivity(new Intent(mContext, AutoLearningActivity.class));
                        //??????????????????
                        BuriedPointUtils.buriedPoint("2031","","","","");
                        break;
                    case R.mipmap.personal_space:
                        //????????????home?????????
                        lingChang();
                        mContext.startActivity(new Intent(mContext, PersonalSpaceActivity.class));

                        //??????????????????
                        BuriedPointUtils.buriedPoint("2035","","","","");
                        break;
                    case R.mipmap.class_record:
                        //????????????home?????????
                        lingChang();
                        mContext.startActivity(new Intent(mContext, NewClassRecordActivity.class));
                        //??????????????????
                        BuriedPointUtils.buriedPoint("2029","","","","");
                        break;
                    case R.mipmap.zhizhu:
                        //????????????home?????????
                        lingChang();
                        mContext.startActivity(new Intent(mContext, com.telit.zhkt_three.Activity.UnityResource
                                .ForUnityResourceActivity.class));
                        break;
                    case R.mipmap.micro_class:
                        //????????????home?????????
                        lingChang();
                        mContext.startActivity(new Intent(mContext, MicroClassCenterActivity.class));
                        //??????????????????
                        BuriedPointUtils.buriedPoint("2023","","","","");
                        break;
                    case R.mipmap.expected:
                        QZXTools.popCommonToast(mContext, "????????????", false);
                      //  mContext.startActivity(new Intent(mContext, TestActivity.class));

                        break;
                }

            }
        }
    }
    private void lingChang() {
        Intent intent = new Intent("com.linspirer.edu.homeaction");
        intent.setPackage("com.android.launcher3");
        mContext.sendBroadcast(intent);
        // Toast.makeText(mContext,"?????????com.linspirer.edu.homeaction??????",Toast.LENGTH_LONG).show();
    }
}
