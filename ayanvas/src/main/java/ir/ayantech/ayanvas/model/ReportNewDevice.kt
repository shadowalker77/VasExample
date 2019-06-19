package ir.ayantech.ayanvas.model

import android.os.Build

data class ReportNewDeviceInput(
    val ApplicationName: String,
    val ApplicationType: String,
    val UniqueID: String,
    val ApplicationUniqueToken: String,
    val ApplicationVersion: String,
    val ExtraInfo: ReportNewDeviceExtraInfo,
    val OperatorName: String
)

data class ReportNewDeviceExtraInfo(
    val PackageName: String,
    val InstalledApps: String,
    val VersionCode: String,
    val BuildVersion: String = Build.VERSION.RELEASE,
    val Brand: String = Build.BRAND,
    val Model: String = Build.MODEL,
    val Device: String = Build.DEVICE
)

data class ReportNewDeviceOutput(
    val Token: String
)