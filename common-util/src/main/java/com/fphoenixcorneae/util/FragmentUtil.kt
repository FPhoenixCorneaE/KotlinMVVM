package com.fphoenixcorneae.util

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * Fragment操作工具类
 *
 */
class FragmentUtil private constructor() {

    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }

    companion object {

        private var fragmentManager: FragmentManager? = null
        private var fragmentTransaction: FragmentTransaction? = null

        @IdRes
        private var containerViewId: Int = 0

        fun with(activity: FragmentActivity): Companion {
            fragmentManager = activity.supportFragmentManager
            fragmentTransaction = fragmentManager?.beginTransaction()
            return this
        }

        fun with(fragment: Fragment): Companion {
            fragmentManager = fragment.childFragmentManager
            fragmentTransaction = fragmentManager?.beginTransaction()
            return this
        }

        /**
         * 设置容器id
         */
        fun setContainerViewId(@IdRes containerId: Int): Companion {
            containerViewId = containerId
            return this
        }

        /**
         * 设置自定义动画
         */
        fun setCustomAnimations(
            @AnimatorRes @AnimRes enterAnim: Int = 0,
            @AnimatorRes @AnimRes exitAnim: Int = 0,
            @AnimatorRes @AnimRes popEnterAnim: Int = 0,
            @AnimatorRes @AnimRes popExitAnim: Int = 0
        ): Companion {
            fragmentTransaction?.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
            return this
        }

        /**
         * 添加共享元素
         */
        fun addSharedElement(vararg views: View) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (view in views) {
                    fragmentTransaction?.addSharedElement(view, view.transitionName)
                }
            }
        }

        /**
         * Replace an existing fragment that was added to a container.
         * @param addToBackStack 将fragment添加到回退栈中
         */
        fun replaceFragment(
            newFragment: Fragment,
            bundle: Bundle? = null,
            addToBackStack: Boolean = false
        ): Companion {
            newFragment.arguments = BundleBuilder.of(bundle).get()
            fragmentTransaction?.replace(containerViewId, newFragment, newFragment.javaClass.name)
            if (addToBackStack) {
                fragmentTransaction?.addToBackStack(newFragment.javaClass.name)
            }
            return this
        }

        /**
         * Add a fragment to the activity state. This fragment may optionally also have its view (if
         * [Fragment.onCreateView] returns non-null) into a container view of the activity.
         * @param addToBackStack 将fragment添加到回退栈中
         */
        fun addFragment(
            newFragment: Fragment,
            bundle: Bundle? = null,
            addToBackStack: Boolean = false
        ): Companion {
            newFragment.arguments = BundleBuilder.of(bundle).get()
            if (!newFragment.isAdded) {
                fragmentTransaction?.add(containerViewId, newFragment, newFragment.javaClass.name)
            }
            if (addToBackStack) {
                fragmentTransaction?.addToBackStack(newFragment.javaClass.name)
            }
            return this
        }

        /**
         * Hides an existing fragment. This is only relevant for fragments whose views have been added to a container, as
         * this will cause the view to be hidden.
         * @param addToBackStack 将fragment添加到回退栈中
         */
        fun hideAndShowFragment(
            previousFragment: Fragment?,
            newFragment: Fragment,
            bundle: Bundle? = null,
            addToBackStack: Boolean = false
        ): Companion {
            if (null != previousFragment) {
                fragmentTransaction?.hide(previousFragment)
            }
            newFragment.arguments = BundleBuilder.of(bundle).get()
            if (newFragment.isAdded) {
                fragmentTransaction?.show(newFragment)
            } else {
                fragmentTransaction
                    ?.add(
                        containerViewId,
                        newFragment,
                        newFragment.javaClass.name
                    )
                    ?.show(newFragment)
            }
            if (addToBackStack) {
                fragmentTransaction?.addToBackStack(newFragment.javaClass.name)
            }
            return this
        }

        /**
         * Remove an existing fragment. If it was added to a container, its view is also removed from that container.
         */
        fun removeFragment(
            vararg names: String
        ): Companion {
            for (name in names) {
                fragmentManager?.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            for (name in names) {
                val fragment = fragmentManager?.findFragmentByTag(name)
                if (fragment != null) {
                    fragmentTransaction?.remove(fragment)
                }
            }
            return this
        }

        fun commit() {
            fragmentTransaction?.commit()
        }

        fun commitNow() {
            fragmentTransaction?.commitNow()
        }

        fun commitAllowingStateLoss() {
            fragmentTransaction?.commitAllowingStateLoss()
        }

        fun commitNowAllowingStateLoss() {
            fragmentTransaction?.commitNowAllowingStateLoss()
        }
    }
}
