package ir.ayantech.ayanvas.ui

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ir.ayantech.ayanvas.R
import ir.ayantech.ayanvas.helper.loadBase64
import ir.ayantech.ayanvas.helper.setHtmlText
import ir.ayantech.ayanvas.model.Slider
import ir.ayantech.ayanvas.ui.fragmentation.FragmentationFragment
import kotlinx.android.synthetic.main.fragment_introduction.*
import kotlinx.android.synthetic.main.slide.view.*

class IntroductionFragment : FragmentationFragment() {

    lateinit var sliders: ArrayList<Slider>

    override fun getLayoutId(): Int = R.layout.fragment_introduction

    override fun onCreate() {
        super.onCreate()
        viewPager.adapter = AyanViewPagerAdapter(activity!!, sliders)
        for (slider in sliders) {
            val indicator = LayoutInflater.from(activity).inflate(R.layout.indicator, rootView as ViewGroup, false)
            indicatorsLl.addView(indicator)
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(p0: Int) {
                for (i in 0 until sliders.size) {
                    (indicatorsLl.getChildAt(i) as ImageView).setImageResource(if (i == p0) R.drawable.filled_circle else R.drawable.empty_circle)
                }
                if (p0 == 0) {
                    nextTv.text = "شروع"
                    nextTv.setOnClickListener { start(GetMobileFragment()) }
                } else {
                    nextTv.text = "بعدی"
                    nextTv.setOnClickListener { viewPager.currentItem = viewPager.currentItem - 1 }
                }
            }
        })
        viewPager.currentItem = sliders.size - 1
    }

    class AyanViewPagerAdapter(
        private val context: Context,
        private val slides: ArrayList<Slider>
    ) : PagerAdapter() {
        override fun isViewFromObject(p0: View, p1: Any): Boolean = p0 == p1

        override fun getCount(): Int = slides.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

            val view = layoutInflater!!.inflate(R.layout.slide, container, false)
            if (slides[position].Title.isNullOrEmpty()) view.titleTv.visibility = View.GONE
            view.titleTv.setHtmlText(slides[position].Title)
            view.descriptionTv.setHtmlText(slides[position].Description)
            view.imageIv.loadBase64(slides[position].ImageBase64)
            container.addView(view)

            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}
