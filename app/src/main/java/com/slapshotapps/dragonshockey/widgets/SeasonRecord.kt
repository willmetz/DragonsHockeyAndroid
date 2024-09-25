package com.slapshotapps.dragonshockey.widgets

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slapshotapps.dragonshockey.ui.theme.Typography


data class SeasonRecord(val wins: String, val losses: String, val overtimeLosses: String, val ties: String)

@Composable
fun SeasonRecordWidget(seasonRecord: SeasonRecord, modifier: Modifier = Modifier){
    Column(modifier.then(Modifier.fillMaxWidth())) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("W", style = Typography.titleMedium, textAlign = TextAlign.Center)
            Text("L", style = Typography.titleMedium, textAlign = TextAlign.Center)
            Text("OTL", style = Typography.titleMedium, textAlign = TextAlign.Center)
            Text("T", style = Typography.titleMedium, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.padding(vertical = 4.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(seasonRecord.wins, style = Typography.titleMedium, textAlign = TextAlign.Center)
            Text(seasonRecord.losses, style = Typography.titleMedium, textAlign = TextAlign.Center)
            Text(seasonRecord.overtimeLosses, style = Typography.titleMedium, textAlign = TextAlign.Center)
            Text(seasonRecord.ties, style = Typography.titleMedium, textAlign = TextAlign.Center)
        }
    }
}


@Preview(widthDp = 200)
@Composable
private fun ShowSeasonRecord(){
    SeasonRecord("2", "1", "3", "2").apply {
        SeasonRecordWidget(seasonRecord = this)
    }

}