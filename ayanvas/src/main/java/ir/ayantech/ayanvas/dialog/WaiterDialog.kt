package ir.ayantech.ayanvas.dialog

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.Window
import ir.alirezabdn.wp7progress.WP7ProgressBar
import ir.ayantech.ayanvas.R

class WaiterDialog(context: Context) : Dialog(context) {

    private var progressBarCount = 0
    private val progressBar: WP7ProgressBar
    private val handler: Handler

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.ayan_vas_dialog_waiter)
        handler = Handler()
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        progressBar = findViewById(R.id.progressBar)
    }

    fun showDialog() {
        if (progressBarCount == 0) {
            handler.post {
                show()
                progressBar.showProgressBar()
            }
        }
        progressBarCount++
        Log.d("PBAR", progressBarCount.toString())
    }

    fun hideDialog() {
        progressBarCount--
        handler.postDelayed({
            if (progressBarCount == 0) {
                progressBar.hideProgressBar()
                dismiss()
            }
            Log.d("PBAR", progressBarCount.toString())
        }, 50)
    }
}
