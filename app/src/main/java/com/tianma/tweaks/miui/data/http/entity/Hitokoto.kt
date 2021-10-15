package com.tianma.tweaks.miui.data.http.entity

import com.google.gson.annotations.SerializedName

/**
 * 一言
 */
// {
//         "id": 4401,
//         "hitokoto": "那么难受，那么痛苦，可是 世界这么美丽...让我如何能够忘记！",
//         "type": "a",
//         "from": "朝花夕誓",
//         "creator": "飞龙project",
//         "created_at": "1553579805"
// }
data class Hitokoto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("hitokoto")
    val content: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("from")
    val from: String?,
)