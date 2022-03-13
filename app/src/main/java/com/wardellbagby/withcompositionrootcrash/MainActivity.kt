package com.wardellbagby.withcompositionrootcrash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.workflow1.ui.*
import com.squareup.workflow1.ui.compose.withCompositionRoot
import kotlinx.coroutines.flow.StateFlow

@OptIn(WorkflowUiExperimentalApi::class)
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val viewEnvironment = ViewEnvironment(
      mapOf(
        ViewRegistry to ViewRegistry(),
      )
    ).withCompositionRoot { content ->
      MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors(
          primary = Color.Magenta,
        ) else lightColors(
          primary = Color.Green,
        )
      ) {
        content()
      }
    }

    val model: AppViewModel by viewModels()
    setContentView(WorkflowLayout(this).apply {
      start(lifecycle, model.renderings, viewEnvironment)
    })
  }
}

class AppViewModel(
  savedState: SavedStateHandle
) : ViewModel() {
  @OptIn(WorkflowUiExperimentalApi::class)
  val renderings: StateFlow<Any> =
    renderWorkflowIn(
      workflow = MainWorkflow,
      scope = viewModelScope,
      prop = Unit,
      savedStateHandle = savedState
    )
}