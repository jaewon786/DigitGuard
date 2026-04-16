package com.digitguard.app.domain.model

import com.google.gson.annotations.SerializedName

enum class ThreatLevel(val severity: Int) {
    @SerializedName("none") NONE(0),
    @SerializedName("low") LOW(1),
    @SerializedName("medium") MEDIUM(2),
    @SerializedName("high") HIGH(3);

    operator fun compareTo(other: ThreatLevel): Int = this.severity - other.severity
}
