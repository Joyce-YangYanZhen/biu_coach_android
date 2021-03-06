package com.noplugins.keepfit.coachplatform.activity.mine

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lwjfork.code.CodeEditText
import com.noplugins.keepfit.coachplatform.R
import com.noplugins.keepfit.coachplatform.activity.info.VerificationPhoneActivity
import com.noplugins.keepfit.coachplatform.adapter.CardAdapter
import com.noplugins.keepfit.coachplatform.base.BaseActivity
import com.noplugins.keepfit.coachplatform.bean.BankCardBean
import com.noplugins.keepfit.coachplatform.global.AppConstants
import com.noplugins.keepfit.coachplatform.global.clickWithTrigger
import com.noplugins.keepfit.coachplatform.util.HideDataUtil
import com.noplugins.keepfit.coachplatform.util.SpUtils
import com.noplugins.keepfit.coachplatform.util.net.Network
import com.noplugins.keepfit.coachplatform.util.net.entity.Bean
import com.noplugins.keepfit.coachplatform.util.net.progress.ProgressSubscriber
import com.noplugins.keepfit.coachplatform.util.net.progress.SubscriberOnNextListener
import com.noplugins.keepfit.coachplatform.util.ui.pop.CommonPopupWindow
import com.noplugins.keepfit.coachplatform.util.ui.toast.SuperCustomToast
import kotlinx.android.synthetic.main.activity_withdraw.*
import java.util.HashMap

class WithdrawActivity : BaseActivity() {
    var cardNumber = ""
    var selectCard = -1
    var finalCanWithdraw = 0.0
    val list:MutableList<BankCardBean> = ArrayList()
    override fun initBundle(parms: Bundle?) {
        if (parms!=null){
            finalCanWithdraw = parms.getDouble("finalCanWithdraw")
        }
    }

    override fun initView() {
        setContentView(R.layout.activity_withdraw)
        tv_name.text = "持卡人  "+SpUtils.getString(this, AppConstants.NAME)
        //超过1000元可以提现
        val ss = SpannableString("超过500元可以提现")//定义hint的值
        val ass = AbsoluteSizeSpan(15, true)//设置字体大小 true表示单位是sp
        ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        et_withdraw_money.hint = SpannedString(ss)
        tv_withdraw_ok.isClickable = false
    }

    override fun onResume() {
        super.onResume()
        requestCardList()
    }
    override fun doBusiness(mContext: Context?) {
        tv_now_money.text = "当前可提现余额$finalCanWithdraw"
        back_btn.clickWithTrigger {
            finish()
        }
        rl_money_details.clickWithTrigger {
            currentFocus?.let {
                val mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                mInputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                et_withdraw_money.clearFocus()
            }
            Handler().postDelayed({ toSelectCard(rl_money_details) }, 200)

        }
        iv_delete_edit.clickWithTrigger {
            et_withdraw_money.setText("")
        }
        tv_all.clickWithTrigger {
            et_withdraw_money.setText("$finalCanWithdraw")
        }

        tv_withdraw_ok.clickWithTrigger(2000) {
            if (et_withdraw_money.text.toString().isEmpty()){
                SuperCustomToast.getInstance(applicationContext)
                    .show("提现金额不能小于500")
                return@clickWithTrigger
            }
            //提现操作
            if (et_withdraw_money.text.toString().toDouble() < 500){
                SuperCustomToast.getInstance(applicationContext)
                    .show("提现金额不能小于500")
                return@clickWithTrigger
            }
            toInputPwd(tv_withdraw_ok)
        }
        et_withdraw_money.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
             }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()){
                    iv_delete_edit.visibility = View.VISIBLE
                    if (s[0].toString() != "."){
                        if (s.toString().toDouble() > finalCanWithdraw){
                            tv_tips.visibility = View.VISIBLE
                            tv_withdraw_ok.setBackgroundResource(R.drawable.btn_no_click)
                            tv_withdraw_ok.isClickable = false
                        }
                        else {
                            tv_tips.visibility = View.GONE
                            tv_withdraw_ok.setBackgroundResource(R.drawable.btn_click)
                            tv_withdraw_ok.isClickable = true
                        }

                    }
                } else {
                    iv_delete_edit.visibility = View.GONE
                    tv_tips.visibility = View.GONE
                    tv_withdraw_ok.setBackgroundResource(R.drawable.btn_click)
                    tv_withdraw_ok.isClickable = true
                }


            }


        })

        ll_content.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                currentFocus?.let {

                    val mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    mInputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                    et_withdraw_money.clearFocus()
                    return false

                }
                return false
            }

        })
    }

    private fun requestCardList(){
        val params = HashMap<String, Any>()
        params["teacherNum"] = SpUtils.getString(applicationContext,AppConstants.USER_NAME)
//        params["teacherNum"] = "CUS19091292977313"
        subscription = Network.getInstance("银行卡列表", this)
            .bankList(
                params,
                ProgressSubscriber("银行卡列表", object : SubscriberOnNextListener<Bean<List<BankCardBean>>> {
                    override fun onNext(result: Bean<List<BankCardBean>>) {
//                        setting(result.data.areaList)
                        list.clear()
                        list.addAll(result.data)
                    }

                    override fun onError(error: String) {

                    }
                }, this, false)
            )
    }
    private fun toSelectCard(view1: View) {
        val popupWindow = CommonPopupWindow.Builder(this)
            .setView(R.layout.selext_card_pop)
            .setBackGroundLevel(0.5f)//0.5f
            .setAnimationStyle(R.style.main_menu_animstyle)
            .setWidthAndHeight(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            .setOutSideTouchable(true).create()
        popupWindow.showAsDropDown(view1)

        /**设置逻辑 */
        val view = popupWindow.contentView
        val addCard = view.findViewById<LinearLayout>(R.id.ll_add_card)
        val rvCard = view.findViewById<RecyclerView>(R.id.rv_dialog_card)
        val layoutManager = LinearLayoutManager(this)
        rvCard.layoutManager = layoutManager
        val cardAdapter = CardAdapter(list)
        rvCard.adapter = cardAdapter
        if (selectCard >-1){

//            val itemView = (rvCard.layoutManager as LinearLayoutManager).findViewByPosition(selectCard)
//            val chabox = itemView!!.findViewById<CheckBox>(R.id.cb_select)
//            chabox.isChecked = true

        }
        cardAdapter.setOnItemChildClickListener { adapter, itemView, position ->
            if (itemView.id == R.id.ll_item){
                val checkBox = itemView.findViewById<CheckBox>(R.id.cb_select)
                checkBox.isChecked = true
            } else if (itemView.id == R.id.cb_select) {
                (  itemView as CheckBox ).isChecked = true
            }
            cardNumber = list[position].bankCardNum
            tv_card_number.text  = HideDataUtil.hideCardNo(list[position].bankCardNum)
            tv_bank_name.text = list[position].bankCardName
            tv_card_type.text = "储蓄卡"
            rl_select_card.visibility = View.GONE
            ll_card.visibility = View.VISIBLE
            selectCard = position
            popupWindow.dismiss()


        }
        addCard.setOnClickListener {
            popupWindow.dismiss()
            val intent = Intent(this,AddCardActivity::class.java)
            startActivity(intent)
            //添加银行卡
        }
    }


    private fun toInputPwd(view1: TextView) {
        val popupWindow = CommonPopupWindow.Builder(this)
            .setView(R.layout.dialog_to_input_pwd)
            .setBackGroundLevel(0.5f)//0.5f
            .setAnimationStyle(R.style.main_menu_animstyle)
            .setWidthAndHeight(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            .setOutSideTouchable(true).create()
        popupWindow.showAsDropDown(view1)

        /**设置逻辑 */
        val view = popupWindow.contentView
        val cancel = view.findViewById<LinearLayout>(R.id.cancel_layout)
        val sure = view.findViewById<LinearLayout>(R.id.sure_layout)
        val etPwd = view.findViewById<CodeEditText>(R.id.et_password)
        val tvWjPwd = view.findViewById<TextView>(R.id.tv_wj_pwd)
        val tv_money = view.findViewById<TextView>(R.id.tv_money)
        tv_money.text = "¥${et_withdraw_money.text.toString().trim()}"

        cancel.setOnClickListener {
            popupWindow.dismiss()
        }
        tvWjPwd.clickWithTrigger(1000) {
            val intent = Intent(this, VerificationPhoneActivity::class.java)
            startActivity(intent)
        }
        sure.setOnClickListener {
            if (etPwd.text.toString().length < 6){
                //请输入6位数密码
                return@setOnClickListener
            }
            Log.d("etPwd",etPwd.text.toString())
            request(etPwd.text.toString())
            popupWindow.dismiss()
        }
    }


    private fun request(pwd:String){
        //withdrawDeposit
        val params = HashMap<String, Any>()
        params["teacherNum"] = SpUtils.getString(this, AppConstants.USER_NAME)
        params["money"] = et_withdraw_money.text.toString().trim()
        params["paypassword"] = pwd
        val subscription = Network.getInstance("提现", this)
            .withdrawDeposit(
                params,
                ProgressSubscriber("提现", object : SubscriberOnNextListener<Bean<Any>> {
                    override fun onNext(result: Bean<Any>) {
                        toComplete()
                    }

                    override fun onError(error: String) {
                        SuperCustomToast.getInstance(applicationContext)
                            .show(error)
                    }
                }, this, false)
            )
    }
    private fun toComplete(){
        val intent = Intent(this, WithdrawCompleteActivity::class.java)
        startActivity(intent)
        this.finish()

    }
}