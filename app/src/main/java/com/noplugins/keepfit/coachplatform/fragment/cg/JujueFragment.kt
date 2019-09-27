package com.noplugins.keepfit.coachplatform.fragment.cg

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.noplugins.keepfit.coachplatform.R
import com.noplugins.keepfit.coachplatform.activity.manager.ChaungguanDetailActivity
import com.noplugins.keepfit.coachplatform.adapter.ShoukeCgAdapter
import com.noplugins.keepfit.coachplatform.base.BaseFragment
import com.noplugins.keepfit.coachplatform.bean.manager.CgListBean
import com.noplugins.keepfit.coachplatform.util.net.Network
import com.noplugins.keepfit.coachplatform.util.net.entity.Bean
import com.noplugins.keepfit.coachplatform.util.net.progress.ProgressSubscriber
import com.noplugins.keepfit.coachplatform.util.net.progress.SubscriberOnNextListener
import kotlinx.android.synthetic.main.fragment_manager_teacher_1.*
import java.util.HashMap

class JujueFragment : BaseFragment()  {
    companion object {
        fun newInstance(title: String): JujueFragment {
            val fragment = JujueFragment()
            val args = Bundle()
            args.putString("home_fragment_title", title)
            fragment.arguments = args
            return fragment
        }
    }
    var  datas:MutableList<CgListBean.AreaListBean> = ArrayList()
    lateinit var adapterManager : ShoukeCgAdapter
    var newView: View? = null
    var page = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (newView == null) {
            newView = inflater.inflate(R.layout.fragment_manager_teacher_1, container, false)
        }
        return newView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initAdapter()
//        requestData()
    }

    override fun onFragmentVisibleChange(isVisible: Boolean) {
        super.onFragmentVisibleChange(isVisible)
        if (isVisible){
//            requestData()
        }
    }

    private fun initAdapter(){
        rv_list.layoutManager = LinearLayoutManager(context)
        adapterManager = ShoukeCgAdapter(datas)
        val view = LayoutInflater.from(context).inflate(R.layout.enpty_view, rv_list, false)
        adapterManager.emptyView = view
        rv_list.adapter = adapterManager

        adapterManager.setOnItemChildClickListener { adapter, view, position ->
            when(view.id){
                R.id.rl_jump -> {
                    //跳转到详情页 需要携带状态
                    val toInfo = Intent(activity, ChaungguanDetailActivity::class.java)
                    val bundle = Bundle()
                    bundle.putInt("type",1)
                    bundle.putString("cgNum",datas[position].areaNum)
                    toInfo.putExtras(bundle)
                    startActivity(toInfo)
                }
            }
        }
        refresh_layout.setOnRefreshListener {
            //下拉刷新
            refresh_layout.finishRefresh(2000/*,false*/)
        }
        refresh_layout.setOnLoadMoreListener {
            //上拉加载
            refresh_layout.finishLoadMore(2000/*,false*/)
        }

    }

    private fun requestData(){
        val params = HashMap<String, Any>()
//        params["teacherNum"] = SpUtils.getString(activity,AppConstants.USER_NAME)
        params["teacherNum"] = "GEN23456"
        params["courseType"] = 1
        params["type"] = 1
        val subscription = Network.getInstance("场馆列表", activity)
            .bindingAreaList(
                params,
                ProgressSubscriber("场馆列表", object : SubscriberOnNextListener<Bean<CgListBean>> {
                    override fun onNext(result: Bean<CgListBean>) {
//                        setting(result.data.areaList)
                        if (page == 1){
                            datas.clear()
                            datas.addAll(result.data.areaList)
                        } else{
                            datas.addAll(result.data.areaList)
                        }
                        adapterManager.notifyDataSetChanged()
                    }

                    override fun onError(error: String) {

                    }
                }, activity, false)
            )

    }
}