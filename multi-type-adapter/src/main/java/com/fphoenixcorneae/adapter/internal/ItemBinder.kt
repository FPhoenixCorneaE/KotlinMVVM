package com.fphoenixcorneae.adapter.internal

import com.fphoenixcorneae.adapter.wrapper.ViewHolderWrapper

/**
 * 数据-视图绑定器
 */
data class ItemBinder<T>(
        val type: Class<T>, // 待处理的数据类型
        val viewHolderWrappers: MutableList<ViewHolderWrapper<T>>,  // 可供选择的封装器
        val delegation: Delegation<T>   // 委托方式
)