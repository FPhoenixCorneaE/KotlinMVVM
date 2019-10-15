package com.wkz.adapter.internal

import com.wkz.adapter.wrapper.ViewHolderWrapper

/**
 * 委托
 * 数据类型委托给何种封装器的描述
 */
interface Delegation<T> {
    fun getWrapperType(item: T): Class<out ViewHolderWrapper<T>>
}