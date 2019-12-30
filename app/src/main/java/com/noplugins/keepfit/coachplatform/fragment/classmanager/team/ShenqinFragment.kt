package com.noplugins.keepfit.coachplatform.fragment.classmanager.team

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.noplugins.keepfit.coachplatform.R
import com.noplugins.keepfit.coachplatform.activity.manager.TeamInfoActivity
import com.noplugins.keepfit.coachplatform.adapter.ManagerTeacherAdapter
import com.noplugins.keepfit.coachplatform.adapter.ManagerTeamClassAdapter
import com.noplugins.keepfit.coachplatform.base.BaseFragment
import com.noplugins.keepfit.coachplatform.bean.manager.ManagerBean
import com.noplugins.keepfit.coachplatform.bean.manager.ManagerTeamBean
import com.noplugins.keepfit.coachplatform.global.AppConstants
import com.noplugins.keepfit.coachplatform.global.PublicPopControl
import com.noplugins.keepfit.coachplatform.util.SpUtils
import com.noplugins.keepfit.coachplatform.util.net.Network
import com.noplugins.keepfit.coachplatform.util.net.entity.Bean
import com.noplugins.keepfit.coachplatform.util.net.progress.ProgressSubscriber
import com.noplugins.keepfit.coachplatform.util.net.progress.SubscriberOnNextListener
import com.noplugins.keepfit.coachplatform.util.ui.pop.CommonPopupWindow
import com.noplugins.keepfit.coachplatform.util.ui.toast.SuperCustomToast
import kotlinx.android.synthetic.main.fragment_manager_teacher_1.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.HashMap

class ShenqinFragment : BaseFragment() {
    companion object {
        fun newInstance(title: String): ShenqinFragment {
            val fragment = ShenqinFragment()
            val args = Bundle()
            args.putString("home_fragment_title", title)
            fragment.arguments = args
            return fragment
        }
    }

    var datas: MutableList<ManagerBean.CourseListBean> = ArrayList()
    lateinit var adapterManager: ManagerTeamClassAdapter
    var newView: View? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (newView == null) {
            newView = inflater.inflate(R.layout.fragment_manager_teacher_1, container, false)
            EventBus.getDefault().register(this)
        }
        return newView
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun upadate(messageEvent:String ) {
        if (AppConstants.TEAM_YQ_AGREE == messageEvent||AppConstants.TEAM_YQ_REFUSE == messageEvent){
            requestData()
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initAdapter()
        requestData()
    }

    override fun onFragmentVisibleChange(isVisible: Boolean) {
        super.onFragmentVisibleChange(isVisible)
        if (isVisible) {

        }
    }

    private fun initAdapter() {
        rv_list.layoutManager = LinearLayoutManager(context)
        adapterManager = ManagerTeamClassAdapter(datas)
        val view = LayoutInflater.from(context).inflate(R.layout.enpty_view, null, false)
        adapterManager.emptyView = view
        rv_list.adapter = adapterManager

        adapterManager.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.rl_jump -> {
                    //跳转到详情 需要携带状态
                    val toInfo = Intent(activity, TeamInfoActivity::class.java)
                    val bundle = Bundle()
                    bundle.putInt("type", 4)
                    bundle.putString("courseNum", datas[position].courseNum)
                    bundle.putInt("status",datas[position].status)
                    toInfo.putExtras(bundle)
                    startActivity(toInfo)
                }
                R.id.tv_yaoqin_edit -> {
                    toJujue(view as TextView, position)
                }
            }
        }
        refresh_layout.setEnableLoadMore(false)
        refresh_layout.setOnRefreshListener {
            //下拉刷新
            requestData()
            refresh_layout.finishRefresh(2000/*,false*/)
        }
        refresh_layout.setOnLoadMoreListener {
            //上拉加载
            refresh_layout.finishLoadMore(2000/*,false*/)
        }

    }

    private fun requestData() {
        val params = HashMap<String, Any>()
//        params["teacherNum"] = SpUtils.getString(activity, AppConstants.USER_NAME)
        params["teacherNum"] = SpUtils.getString(activity, AppConstants.USER_NAME)
        params["courseType"] = 1
        params["type"] = 4
        val subscription = Network.getInstance("课程管理", activity)
            .courseManager(
                params,
                ProgressSubscriber("课程管理", object : SubscriberOnNextListener<Bean<ManagerBean>> {
                    override fun onNext(result: Bean<ManagerBean>) {
                        datas.clear()
                        datas.addAll(result.data.courseList)
                        adapterManager.notifyDataSetChanged()
                    }

                    override fun onError(error: String) {
                        SuperCustomToast.getInstance(activity)
                            .show(error)

                    }
                }, activity, false)
            )

    }

    private fun toJujue(view1: TextView, position: Int) {

        PublicPopControl.alert_dialog_center(activity) { view, popup ->
            val content = view.findViewById<TextView>(R.id.pop_content)
            val title = view.findViewById<TextView>(R.id.pop_title)
            content.setText("确定取消申请?")
            title.setText("取消申请")
            view.findViewById<LinearLayout>(R.id.cancel_btn)
                .setOnClickListener {
                    popup.dismiss()
                }
            view.findViewById<LinearLayout>(R.id.sure_btn)
                .setOnClickListener {  //去申请
                    popup.dismiss()
                    agreeCourse(position)}
        }
    }

    private fun agreeCourse(position: Int) {
        val params = HashMap<String, Any>()
        params["teacherNum"] = SpUtils.getString(activity, AppConstants.USER_NAME)
//        params["teacherNum"] = "GEN23456"
        params["gymInviteNum"] = datas[position].courseNum
        val subscription = Network.getInstance("团课取消申请", activity)
            .cancelInviteByTeacher(
                params,
                ProgressSubscriber("团课取消申请", object : SubscriberOnNextListener<Bean<Any>> {
                    override fun onNext(result: Bean<Any>) {
                        //上架成功！
                       if (result.code == 0){
                           datas.removeAt(position)//删除数据源,移除集合中当前下标的数据
                           adapterManager.notifyItemRemoved(position)//刷新被删除的地方
                           adapterManager.notifyItemRangeChanged(position, adapterManager.itemCount) //刷新被删除数据，以及其后面的数据

                           EventBus.getDefault().post(AppConstants.TEAM_YQ_REFUSE)
                       }

                        SuperCustomToast.getInstance(activity)
                            .show(result.message)
                    }

                    override fun onError(error: String) {
                        SuperCustomToast.getInstance(activity)
                            .show(error)
                    }
                }, activity, false)
            )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }
}