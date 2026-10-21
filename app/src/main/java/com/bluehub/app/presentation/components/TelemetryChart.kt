package com.bluehub.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluehub.app.domain.model.TelemetryPoint
import com.bluehub.app.presentation.theme.DarkSurface
import com.bluehub.app.presentation.theme.TechCyan
import com.bluehub.app.presentation.theme.TextMuted
import com.bluehub.app.presentation.theme.TextPrimary

@Composable
fun TelemetryChart(
    title: String,
    points: List<TelemetryPoint>,
    unit: String,
    lineColor: Color = TechCyan,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                val latestValue = points.lastOrNull()?.value
                Text(
                    text = if (latestValue != null) String.format("%.1f %s", latestValue, unit) else "-- $unit",
                    color = lineColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas Plot
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                if (points.size < 2) {
                    // Draw placeholder grid
                    drawLine(
                        color = Color.DarkGray,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = 1f
                    )
                    return@Canvas
                }

                val minVal = (points.minOfOrNull { it.value } ?: 0f) - 2f
                val maxVal = ((points.maxOfOrNull { it.value } ?: 100f) + 2f).coerceAtLeast(minVal + 1f)
                val range = maxVal - minVal

                val stepX = size.width / (points.size - 1).coerceAtLeast(1)

                val path = Path()
                val fillPath = Path()

                points.forEachIndexed { index, point ->
                    val x = index * stepX
                    val normalizedY = 1f - ((point.value - minVal) / range)
                    val y = (normalizedY * size.height).coerceIn(0f, size.height)

                    if (index == 0) {
                        path.moveTo(x, y)
                        fillPath.moveTo(x, size.height)
                        fillPath.lineTo(x, y)
                    } else {
                        path.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }

                    if (index == points.size - 1) {
                        fillPath.lineTo(x, size.height)
                        fillPath.close()
                    }
                }

                // Draw gradient under the line
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent),
                        startY = 0f,
                        endY = size.height
                    )
                )

                // Draw main curve line
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}
