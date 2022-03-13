package com.wardellbagby.withcompositionrootcrash

import android.view.Gravity
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import com.google.android.material.button.MaterialButton
import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.ui.*
import com.squareup.workflow1.ui.compose.composeViewFactory
import com.wardellbagby.withcompositionrootcrash.MainWorkflow.State.ShowingComposeRendering
import com.wardellbagby.withcompositionrootcrash.MainWorkflow.State.ShowingViewRendering

@OptIn(WorkflowUiExperimentalApi::class)
data class ComposeRendering(
  val onNext: () -> Unit
) : AndroidViewRendering<ComposeRendering> {
  override val viewFactory = composeViewFactory { rendering: ComposeRendering, _ ->
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Button(onClick = rendering.onNext) {
        Text(text = "I'm a Compose button!")
      }
    }
  }
}

@OptIn(WorkflowUiExperimentalApi::class)
data class ViewRendering(
  val onNext: () -> Unit
) : AndroidViewRendering<ViewRendering> {
  override val viewFactory =
    BuilderViewFactory(ViewRendering::class) { initialRendering, initialViewEnvironment, contextForNewView, _ ->
      val button = MaterialButton(contextForNewView).apply {
        text = "I'm an View button!"
      }

      fun update(rendering: ViewRendering, viewEnvironment: ViewEnvironment) {
        button.setOnClickListener { rendering.onNext() }
      }

      LinearLayout(contextForNewView).apply {
        gravity = Gravity.CENTER
        addView(button)
        bindShowRendering(initialRendering, initialViewEnvironment, ::update)
      }
    }
}

@OptIn(WorkflowUiExperimentalApi::class)
object MainWorkflow : StatefulWorkflow<Unit, MainWorkflow.State, Nothing, Any>() {
  sealed class State {
    object ShowingViewRendering : State()
    object ShowingComposeRendering : State()
  }

  override fun initialState(props: Unit, snapshot: Snapshot?): State = ShowingViewRendering

  override fun render(renderProps: Unit, renderState: State, context: RenderContext): Any {
    return when (renderState) {
      ShowingComposeRendering -> ComposeRendering(
        onNext = context.eventHandler {
          state = ShowingViewRendering
        })
      ShowingViewRendering -> ViewRendering(
        onNext = context.eventHandler {
          state = ShowingComposeRendering
        })
    }
  }

  override fun snapshotState(state: State): Snapshot? = null
}