package ir.ayantech.ayanvas.ui.fragmentation

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator
import me.yokeyword.fragmentation_swipeback.SwipeBackActivity

abstract class FragmentationActivity : SwipeBackActivity() {
    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return DefaultHorizontalAnimator()
    }
}