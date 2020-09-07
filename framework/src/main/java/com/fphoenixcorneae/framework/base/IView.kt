package com.fphoenixcorneae.framework.base

/**
 * @desc: 视图基类 接口
 */
interface IView {
    /**
     * 显示加载中视图
     */
    fun showLoading()

    /**
     * 显示数据内容视图
     */
    fun showContent()

    /**
     * 显示空数据视图
     */
    fun showEmpty()

    /**
     * 显示无网络视图
     */
    fun showNoNetwork()

    /**
     * 显示错误视图
     */
    fun showError()

    /**
     * 显示错误信息
     * @param t 异常
     */
    fun showErrorMsg(t: Throwable)

    /**
     * 显示错误信息
     * @param errorMsg 错误信息
     */
    fun showErrorMsg(errorMsg: CharSequence)
}
