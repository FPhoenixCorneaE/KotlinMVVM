package com.wkz.extension

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

inline fun <reified T : ViewModel> FragmentActivity.viewModel() =
    lazy { ViewModelProviders.of(this).get(T::class.java) }

inline fun <reified T : ViewModel> FragmentActivity.viewModel(crossinline block: T.() -> Unit) =
    lazy {
        ViewModelProviders.of(this).get(T::class.java).apply(block)
    }

inline fun <reified T : ViewModel> Fragment.viewModel() =
    lazy { ViewModelProviders.of(this).get(T::class.java) }

/**
 * 相同类型使用多个的情况下
 */
@JvmOverloads
inline fun <reified T : ViewModel> Fragment.viewModel(
    key: Int? = null,
    crossinline block: T.() -> Unit
) =
    lazy {
        ViewModelProviders.of(this).get(key.toString(), T::class.java)
            .apply(block)
    }