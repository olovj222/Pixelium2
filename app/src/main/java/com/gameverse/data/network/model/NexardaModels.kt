package com.gameverse.data.network.model

import com.google.gson.annotations.SerializedName

// Lista principal
data class NexardaSearchResponse(
    val success: Boolean,
    val message: String?,
    val results: NexardaResults?
)

data class NexardaResults(
    val items: List<NexardaItem> = emptyList()
)

data class NexardaItem(
    val type: String?,
    val title: String?,
    val text: String?,
    val slug: String?,
    val image: String?,
    val game_info: NexardaGameInfo?
)

data class NexardaGameInfo(
    val id: Int?,
    val name: String?,
    val short_desc: String?,
    val release_date: Long?,
    val lowest_price: Double?
)
