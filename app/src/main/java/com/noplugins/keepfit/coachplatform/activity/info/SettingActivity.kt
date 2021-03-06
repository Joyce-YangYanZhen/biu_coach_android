package com.noplugins.keepfit.coachplatform.activity.info

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.noplugins.keepfit.coachplatform.R
import com.noplugins.keepfit.coachplatform.activity.LoginActivity
import com.noplugins.keepfit.coachplatform.activity.mine.InstructorTypeActivity
import com.noplugins.keepfit.coachplatform.base.BaseActivity
import com.noplugins.keepfit.coachplatform.global.AppConstants
import com.noplugins.keepfit.coachplatform.global.clickWithTrigger
import com.noplugins.keepfit.coachplatform.util.ActivityCollectorUtil
import com.noplugins.keepfit.coachplatform.util.SpUtils
import com.noplugins.keepfit.coachplatform.util.ui.pop.CommonPopupWindow
import kotlinx.android.synthetic.main.activity_setting.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class SettingActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {
    override fun initBundle(parms: Bundle?) {

    }

    override fun initView() {
        setContentView(R.layout.activity_setting)
    }

    override fun doBusiness(mContext: Context?) {
        back_btn.clickWithTrigger(1000) {
            finish()
        }
        rl_mine_info.clickWithTrigger(1000) {
            val intent = Intent(this,InformationActivity::class.java)
            startActivity(intent)

        }
        rl_account.clickWithTrigger(1000) {
            val intent = Intent(this,AccountSecurityActivity::class.java)
            startActivity(intent)

        }
        rl_about.clickWithTrigger(1000) {
            val intent = Intent(this,AboutActivity::class.java)
            startActivity(intent)

        }
//        rl_risk.clickWithTrigger(1000) {
//
//        }
        rl_xieyi.clickWithTrigger(1000) {

            val intent = Intent(this,XieYiActivity::class.java)
            startActivity(intent)
        }
        rl_quit.clickWithTrigger(1000) {
            toQuit(rl_quit)
//            toPhone()
        }
        rl_teacher_manager.clickWithTrigger {
            val intent = Intent(this, InstructorTypeActivity::class.java)
            startActivity(intent)
        }
        rl_call.setOnClickListener {
            call_pop()
        }

    }


    private fun toQuit(view1: View) {
        val popupWindow = CommonPopupWindow.Builder(this)
            .setView(R.layout.dialog_to_quit)
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

        cancel.setOnClickListener {
            popupWindow.dismiss()
        }
        sure.setOnClickListener {
            popupWindow.dismiss()
            val intent = Intent(this, LoginActivity::class.java)
            //退出
            SpUtils.putString(applicationContext, AppConstants.TOKEN,"")
            SpUtils.putString(applicationContext,AppConstants.PHONE,"")
            SpUtils.putString(applicationContext,AppConstants.USER_NAME,"")

            startActivity(intent)
            ActivityCollectorUtil.finishAllActivity()
        }
    }


    companion object {

        private const val PERMISSION_STORAGE_CODE = 10001
        private const val PERMISSION_STORAGE_MSG = "需要电话权限才能联系客服哦"
    }

    val PERMISSION_STORAGE = arrayOf(Manifest.permission.CALL_PHONE)

    private fun call_pop() {
        val popupWindow = CommonPopupWindow.Builder(this)
            .setView(R.layout.call_pop)
            .setBackGroundLevel(0.5f)//0.5f
            .setAnimationStyle(R.style.main_menu_animstyle)
            .setWidthAndHeight(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)
            .setOutSideTouchable(true).create()
        popupWindow.showAsDropDown(rl_call)

        /**设置逻辑 */
        val view = popupWindow.contentView
        val cancel_layout = view.findViewById<LinearLayout>(R.id.cancel_layout)
        val sure_layout = view.findViewById<LinearLayout>(R.id.sure_layout)
        cancel_layout.setOnClickListener { popupWindow.dismiss() }
        sure_layout.setOnClickListener {
            initSimple()
            popupWindow.dismiss()
        }


    }

    //@AfterPermissionGranted(PERMISSION_STORAGE_CODE)
    private fun initSimple() {
        if (hasStoragePermission()) {
            //有权限
            callPhone("4006836895")
        } else {
            //申请权限
            EasyPermissions.requestPermissions(this, PERMISSION_STORAGE_MSG, PERMISSION_STORAGE_CODE, *PERMISSION_STORAGE)
        }
    }

    @SuppressLint("MissingPermission")
    fun callPhone(phoneNum: String) {
        val intent1 = Intent(Intent.ACTION_CALL)
        val data = Uri.parse("tel:$phoneNum")
        intent1.data = data
        this!!.startActivity(intent1)
    }

    fun hasPermissions( vararg permissions: String): Boolean {
        return EasyPermissions.hasPermissions(this, *permissions)
    }

    fun hasStoragePermission(): Boolean {
        return hasPermissions( *PERMISSION_STORAGE)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                .setTitle("提醒")
                .setRationale("需要电话权限才能联系客服哦")
                .build()
                .show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }
}
