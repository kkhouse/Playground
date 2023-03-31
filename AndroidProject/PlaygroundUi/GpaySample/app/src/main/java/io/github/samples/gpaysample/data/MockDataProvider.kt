package io.github.samples.gpaysample.data

import io.github.samples.gpaysample.R

object MockDataProvider {
    val suica = Suica(
        R.drawable.suica,
        "suica",
        SuicasAmount(
            12000,
            54000,
            42000
        ),
        listOf(
            SuicasUsageHistory(Pair("東京", "大宮"), 1660),
            SuicasUsageHistory(null, 5000),
            SuicasUsageHistory(Pair("大宮", "東京"), 1660),
        )
    )

}

data class Suica(
    val imageId: Int,
    val contentDiscription: String,
    val amountData: SuicasAmount,
    val usageHistory: List<SuicasUsageHistory>
)

data class SuicasAmount(
    val amount: Int,
    val sumChargedAmount: Int,
    val sumUsedAmount: Int,
)

data class SuicasUsageHistory(
    val fromTo: Pair<String, String>?,
    val amount: Int
)