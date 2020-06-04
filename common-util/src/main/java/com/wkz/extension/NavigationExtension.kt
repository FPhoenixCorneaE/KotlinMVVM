package com.wkz.extension

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment

fun Activity.navigate(
    @IdRes viewId: Int,
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
): Unit {
    return Navigation.findNavController(this, viewId)
        .navigate(resId, args, navOptions, navigatorExtras)
}

fun Activity.navigateUp(
    @IdRes viewId: Int
): Boolean {
    return Navigation.findNavController(this, viewId)
        .navigateUp()
}

fun Fragment.navigate(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
): Unit {
    return NavHostFragment.findNavController(this)
        .navigate(resId, args, navOptions, navigatorExtras)
}

fun Fragment.navigateUp(): Boolean {
    return NavHostFragment.findNavController(this)
        .navigateUp()
}

fun View.navigate(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
): Unit {
    return Navigation.findNavController(this).navigate(resId, args, navOptions, navigatorExtras)
}

fun View.navigateUp(): Boolean {
    return Navigation.findNavController(this)
        .navigateUp()
}