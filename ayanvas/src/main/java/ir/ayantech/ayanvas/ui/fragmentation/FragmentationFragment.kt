package ir.ayantech.ayanvas.ui.fragmentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.irozon.sneaker.Sneaker
import ir.ayantech.ayanvas.ui.AuthenticationActivity
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment

abstract class FragmentationFragment : SwipeBackFragment() {
    var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView != null)
            return rootView
        rootView = inflater.inflate(getLayoutId(), container, false)
        if (activity?.supportFragmentManager?.backStackEntryCount ?: 2 > 1) {
            rootView = attachToSwipeBack(rootView)
        }
        return rootView
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)
        onCreate()
    }

    protected abstract fun getLayoutId(): Int

    protected open fun onCreate() {}

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return DefaultHorizontalAnimator()
    }

    fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    fun getAyanApi() = (activity as AuthenticationActivity).ayanApi

    fun getResponseOfGetServiceInfo() = (activity as AuthenticationActivity).getServiceInfo?.response?.Parameters

    override fun onSupportVisible() {
        super.onSupportVisible()
        try {
            Sneaker.hide()
        } catch (e: Exception) {}
    }
}