package com.fphoenixcorneae.framework.web

import android.app.Activity
import android.os.Handler
import android.webkit.WebView
import com.fphoenixcorneae.ext.loggerI
import com.just.agentweb.AgentWebUIControllerImplBase

/**
 * 如果你需要修改某一个AgentWeb 内部的某一个弹窗 ，请看下面的例子
 * 注意写法一定要参照 DefaultUIController 的写法 ，因为UI自由定制，但是回调的方式是固定的，并且一定要回调。
 */
class CustomUIController(private val mActivity: Activity) : AgentWebUIControllerImplBase() {
    override fun onShowMessage(message: String, from: String) {
        super.onShowMessage(message, from)
        loggerI("message:$message")
    }

    override fun onSelectItemsPrompt(
        view: WebView,
        url: String,
        items: Array<String>,
        callback: Handler.Callback
    ) { // 使用默认的UI
        super.onSelectItemsPrompt(view, url, items, callback)
    }

    /**
     * 修改文件选择的弹窗
     */
    /* @Override
    public void onSelectItemsPrompt(WebView view, String mUrl, String[] ways, final Handler.Callback callback) {
        //super.onSelectItemsPrompt(view,mUrl,ways,callback); //这行应该注释或者删除掉
        final AlertDialog mAlertDialog = new AlertDialog.Builder(mActivity)//
                .setSingleChoiceItems(ways, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (callback != null) {
                            Message mMessage = Message.obtain();
                            mMessage.what = which;  //mMessage.what 必须等于ways的index
                            callback.handleMessage(mMessage); //最后callback一定要回调
                        }

                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        if (callback != null) {
                            callback.handleMessage(Message.obtain(null, -1)); //-1表示取消  //最后callback一定要回调
                        }
                    }
                }).create();
        mAlertDialog.show();

    }*/
}