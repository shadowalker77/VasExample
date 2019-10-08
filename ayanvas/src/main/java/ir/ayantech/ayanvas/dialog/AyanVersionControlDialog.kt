package ir.ayantech.ayanvas.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import android.util.Log
import android.view.View
import android.view.Window
import com.coolerfall.download.*
import ir.ayantech.ayanvas.R
import ir.ayantech.ayanvas.model.CheckVersionOutput
import ir.ayantech.ayanvas.model.GetLastVersionOutput
import ir.ayantech.ayanvas.model.VersionControlLinkType
import ir.ayantech.ayanvas.model.VersionControlUpdateStatus
import kotlinx.android.synthetic.main.ayan_dialog_version_control.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class AyanVersionControlDialog(
    activity: Activity,
    checkVersion: CheckVersionOutput,
    getLastVersion: GetLastVersionOutput,
    callback: (updateStatus: Boolean) -> Unit
) : Dialog(activity) {

    private var id = -1
    private val manager: DownloadManager

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.ayan_dialog_version_control)
        this.setCancelable(false)
        this.setCanceledOnTouchOutside(false)

        titleTv.text = getLastVersion.Title
        messageTv.text = getLastVersion.Body
        positiveTv.text = getLastVersion.AcceptButtonText
        negativeTv.text = getLastVersion.RejectButtonText
        when {
            getLastVersion.ChangeLogs == null -> changeLogTv.visibility = View.GONE
            getLastVersion.ChangeLogs.isEmpty() -> changeLogTv.visibility = View.GONE
            else -> {
                val changeLog = StringBuilder()
                for (s in getLastVersion.ChangeLogs) {
                    changeLog.append(s).append("\n")
                }
                changeLogTv.text = changeLog
            }
        }
        manager = DownloadManager.Builder().context(context)
            .downloader(OkHttpDownloader.create())
            .threadPoolSize(2)
            .build()
        positiveTv.setOnClickListener {
            try {
                if (getLastVersion.LinkType == VersionControlLinkType.DIRECT) {
                    if (getRootDirPath(context) == null) {
                        openUrl(context, getLastVersion.Link)
                        callback(false)
                        return@setOnClickListener
                    }
                    progressBar.visibility = View.VISIBLE
                    progressTv.visibility = View.VISIBLE
                    val destPath = getRootDirPath(context) + "/newversion" + Date().time.toString() + ".apk"
                    val request = DownloadRequest.Builder()
                        .url(getLastVersion.Link)
                        .retryTime(5)
                        .retryInterval(2, TimeUnit.SECONDS)
                        .progressInterval(100, TimeUnit.MILLISECONDS)
                        .priority(Priority.HIGH)
                        .destinationFilePath(destPath)
                        .downloadCallback(object : DownloadCallback() {
                            override fun onStart(downloadId: Int, totalBytes: Long) {}

                            override fun onRetry(downloadId: Int) {}

                            override fun onProgress(downloadId: Int, bytesWritten: Long, totalBytes: Long) {
                                val progressPercent = bytesWritten * 100 / totalBytes
                                progressBar.progress = progressPercent.toInt()
                                progressTv.text = String.format("%%%s", progressPercent.toString())
                            }

                            override fun onSuccess(downloadId: Int, filePath: String?) {
                                try {
                                    installApp(context, filePath)
                                    dismiss()
                                    callback(false)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }

                            override fun onFailure(downloadId: Int, statusCode: Int, errMsg: String?) {
                                Log.e("AyanVC:", errMsg)
                                dismiss()
                                openUrl(context, getLastVersion.Link)
                                callback(false)
                            }
                        })
                        .build()

                    id = manager.add(request)
                } else if (getLastVersion.LinkType == VersionControlLinkType.PAGE) {
                    dismiss()
                    openUrl(context, getLastVersion.Link)
                    callback(false)
                }
            } catch (e: Exception) {
                dismiss()
                openUrl(context, getLastVersion.Link)
                callback(false)
            }
        }
        negativeTv.setOnClickListener {
            try {
                manager.cancel(id)
            } catch (e: Exception) {
            }

            dismiss()
            callback(checkVersion.UpdateStatus != VersionControlUpdateStatus.MANDATORY)
        }
    }

    override fun onBackPressed() {}

    private fun installApp(context: Context, path: String?) {
        val toInstall = File(path!!)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                val apkUri = FileProvider.getUriForFile(context, context.packageName + ".provider", toInstall)
                val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                intent.data = apkUri
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.startActivity(intent)
            } catch (e1: Exception) {
                e1.printStackTrace()
            }

        } else {
            val apkUri = Uri.fromFile(toInstall)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(intent)
        }
    }

    private fun openUrl(context: Context, url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }

    private fun getRootDirPath(context: Context): String? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> context.filesDir.absolutePath
            Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() -> context.getExternalFilesDir(null)!!.absolutePath
            else -> null
        }
    }
}
