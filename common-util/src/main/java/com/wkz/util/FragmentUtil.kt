package com.wkz.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * Fragment操作
 *
 * @author wkz
 */
class FragmentUtil private constructor() {

    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }

    companion object {

        /**
         * Replace an existing fragment that was added to a container.
         */
        fun replaceFragment(
            activity: FragmentActivity, containerViewId: Int,
            newFragment: Fragment?, bundle: Bundle,
            canBack: Boolean
        ) {
            if (newFragment == null) {
                return
            }
            val mFragmentTransaction = activity.supportFragmentManager.beginTransaction()
            newFragment.arguments = bundle
            mFragmentTransaction.replace(containerViewId, newFragment, newFragment.javaClass.name)
            if (canBack) {
                mFragmentTransaction.addToBackStack(null)
            }
            mFragmentTransaction.commit()
        }

        /**
         * Add a fragment to the activity state. This fragment may optionally also have its view (if
         * [Fragment.onCreateView] returns non-null) into a container view of the activity.
         */
        fun addFragment(
            activity: FragmentActivity, containerViewId: Int,
            newFragment: Fragment?, bundle: Bundle,
            canBack: Boolean
        ) {
            if (newFragment == null) {
                return
            }
            val mFragmentTransaction = activity.supportFragmentManager.beginTransaction()
            newFragment.arguments = bundle
            if (!newFragment.isAdded) {
                mFragmentTransaction.add(containerViewId, newFragment, newFragment.javaClass.name)
            }
            if (canBack) {
                mFragmentTransaction.addToBackStack(null)
            }
            mFragmentTransaction.commit()
        }

        /**
         * Add a fragment to the fragment state. This fragment may optionally also have its view (if
         * [Fragment.onCreateView] returns non-null) into a container view of the fragment.
         */
        fun addChildFragment(
            fragment: Fragment, containerViewId: Int,
            newFragment: Fragment?, bundle: Bundle,
            canBack: Boolean
        ) {
            if (newFragment == null) {
                return
            }
            val mFragmentTransaction = fragment.childFragmentManager.beginTransaction()
            newFragment.arguments = bundle
            if (!newFragment.isAdded) {
                mFragmentTransaction.add(containerViewId, newFragment, newFragment.javaClass.name)
            }
            if (canBack) {
                mFragmentTransaction.addToBackStack(null)
            }
            mFragmentTransaction.commit()
        }

        /**
         * Hides an existing fragment. This is only relevant for fragments whose views have been added to a container, as
         * this will cause the view to be hidden.
         */
        fun hideAndShowFragment(
            activity: FragmentActivity, containerViewId: Int,
            previousFragment: Fragment?, newFragment: Fragment?,
            bundle: Bundle, canBack: Boolean
        ) {
            if (newFragment == null) {
                return
            }
            val mFragmentTransaction = activity.supportFragmentManager.beginTransaction()
            if (null != previousFragment) {
                mFragmentTransaction.hide(previousFragment)
            }
            newFragment.arguments = bundle
            if (newFragment.isAdded) {
                mFragmentTransaction.show(newFragment)
            } else {
                mFragmentTransaction
                    .add(
                        containerViewId,
                        newFragment,
                        newFragment.javaClass.name
                    )
                    .show(newFragment)
            }
            if (canBack && previousFragment != null) {
                mFragmentTransaction.addToBackStack(newFragment.javaClass.name)
            }
            mFragmentTransaction.commit()
        }

        /**
         * Hides an existing child fragment. This is only relevant for fragments whose views have been added to a container, as
         * this will cause the view to be hidden.
         */
        fun hideAndShowChildFragment(
            fragment: Fragment, containerViewId: Int,
            previousFragment: Fragment?, newFragment: Fragment?,
            bundle: Bundle, canBack: Boolean
        ) {
            if (newFragment == null) {
                return
            }
            val mFragmentTransaction = fragment.childFragmentManager.beginTransaction()
            if (null != previousFragment) {
                mFragmentTransaction.hide(previousFragment)
            }
            newFragment.arguments = bundle
            if (newFragment.isAdded) {
                mFragmentTransaction.show(newFragment)
            } else {
                mFragmentTransaction
                    .add(
                        containerViewId,
                        newFragment,
                        newFragment.javaClass.name
                    )
                    .show(newFragment)
            }
            if (canBack && previousFragment != null) {
                mFragmentTransaction.addToBackStack(newFragment.javaClass.name)
            }
            mFragmentTransaction.commit()
        }

        /**
         * Remove an existing fragment. If it was added to a container, its view is also removed from that container.
         */
        fun removeFragment(activity: FragmentActivity, vararg names: String) {
            val manager = activity.supportFragmentManager
            for (name in names) {
                manager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            val transaction = manager.beginTransaction()
            for (name in names) {
                val fragment = manager.findFragmentByTag(name)
                if (fragment != null) {
                    transaction.remove(fragment)
                }
            }
            transaction.commit()
        }
    }
}
