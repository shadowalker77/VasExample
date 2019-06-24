package ir.ayantech.ayanvas.model

import ir.ayantech.pushnotification.networking.model.ExtraInfo

internal data class AppExtraInfo(val ApplicationToken: String) : ExtraInfo()