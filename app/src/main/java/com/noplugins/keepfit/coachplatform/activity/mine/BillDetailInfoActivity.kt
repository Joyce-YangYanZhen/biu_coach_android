package com.noplugins.keepfit.coachplatform.activity.mine

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.noplugins.keepfit.coachplatform.R
import com.noplugins.keepfit.coachplatform.base.BaseActivity
import com.noplugins.keepfit.coachplatform.bean.BalanceListBean
import com.noplugins.keepfit.coachplatform.global.clickWithTrigger
import com.noplugins.keepfit.coachplatform.util.net.Network
import com.noplugins.keepfit.coachplatform.util.net.entity.Bean
import com.noplugins.keepfit.coachplatform.util.net.progress.ProgressSubscriber
import com.noplugins.keepfit.coachplatform.util.net.progress.SubscriberOnNextListener
import kotlinx.android.synthetic.main.activity_bill_detail_info.*
import java.util.HashMap

class BillDetailInfoActivity : BaseActivity() {
    private var walletDetailNum = ""
    override fun initBundle(parms: Bundle?) {
        if (parms!=null){
            walletDetailNum = parms.getString("walletDetailNum").toString()
            requestData()
        }
    }

    override fun initView() {
        setContentView(R.layout.activity_bill_detail_info)
    }

    override fun doBusiness(mContext: Context?) {
        //
        back_btn.clickWithTrigger {
            finish()
        }
    }

    private fun setting(bean:BalanceListBean.ListBean){
        if(bean.type == 1){
            lv_1.visibility = View.VISIBLE
            lv_2.visibility = View.GONE
            tv_zcje.text ="¥${bean.finalmoney}"
            tv_sqsj.text= bean.createDate
            if (null != bean.cardName){
                tv_txyh.text= bean.cardName
            }
            tv_operate.text = "提现-银行"
            tv_dqzt.text= statusToString(bean.status)
            tv_zclb.text = typeToString(bean.type)
        } else {
            lv_1.visibility = View.GONE
            lv_2.visibility = View.VISIBLE
            tv_srje.text = "¥${bean.money}"
            tv_kcmc.text = bean.courseName
            tv_cgmc.text = bean.areaName
            tv_sksj.text = bean.createDate
            if (bean.type == 3){
                tv_operate.text = "收入-私教服务"
            } else {
                tv_operate.text = "收入-团课服务"
            }
            tv_srlb.text = typeToString(bean.type)
        }


        Glide.with(this)
            .load(typeToDrawble(bean.type))
            .into(iv_logo)

        tv_money.text = "-${bean.finalmoney}"
    }

    private fun requestData() {
        val params = HashMap<String, Any>()
//        params["teacherNum"] = SpUtils.getString(activity, AppConstants.USER_NAME)
        params["walletDetailNum"] = walletDetailNum
        val subscription = Network.getInstance("我的钱包", this)
            .myBalanceListDetail(
                params,
                ProgressSubscriber("我的钱包", object : SubscriberOnNextListener<Bean<BalanceListBean.ListBean>> {
                    override fun onNext(result: Bean<BalanceListBean.ListBean>) {
                        setting(result.data)

                    }

                    override fun onError(error: String) {


                    }
                }, this, false)
            )
    }

    private fun typeToDrawble(type: Int): Int {
        return when (type) {
            1 -> R.drawable.yinlian
            3 -> R.drawable.sijiao_logo
            4 -> R.drawable.team_logo
            else -> R.drawable.yinlian
        }
    }

    private fun typeToString(type: Int): String {

        return when (type) {
            1 -> "银行卡提现"
            3 -> "私教服务"
            4 -> "团课服务"
            else -> "未知错误"
        }
    }

    private fun statusToString(type: Int): String {
        return when (type) {
            1 -> "成功"
            2 -> "失败"
            3 -> "处理中"
            else -> "未知错误"
        }
    }

}
