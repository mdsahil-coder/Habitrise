package com.example.habitrise.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.habitrise.data.model.Habit
import com.example.ui.theme.DangerRed
import com.example.habitrise.util.Localization

@Composable
fun DeleteHabitConfirmDialog(
    habit: Habit,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
    onArchiveInstead: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Localization.get("delete_confirm_title", isHindi),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Text(
                text = String.format(Localization.get("delete_confirm_msg", isHindi), habit.name),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                modifier = Modifier.testTag("confirm_delete_button")
            ) {
                Text(Localization.get("delete", isHindi))
            }
        },
        dismissButton = {
            Row {
                OutlinedButton(onClick = onArchiveInstead) {
                    Text(Localization.get("archive_habit", isHindi))
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = onDismiss) {
                    Text(Localization.get("cancel", isHindi))
                }
            }
        }
    )
}

@Composable
fun ResetDataConfirmDialog(
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onConfirmReset: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Localization.get("reset_data", isHindi),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = DangerRed)
            )
        },
        text = {
            Text(
                text = Localization.get("reset_confirm_msg", isHindi),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmReset,
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                modifier = Modifier.testTag("confirm_reset_button")
            ) {
                Text(Localization.get("reset_data", isHindi))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(Localization.get("cancel", isHindi))
            }
        }
    )
}

@Composable
fun ExportDataDialog(
    jsonString: String,
    isHindi: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("export_data_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = Localization.get("export_data", isHindi),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Text(
                        text = jsonString,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("HabitRise Data", jsonString)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Data copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Copy JSON")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, jsonString)
                            type = "application/json"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Export HabitRise Data")
                        context.startActivity(shareIntent)
                    }) {
                        Text("Share")
                    }
                }
            }
        }
    }
}
