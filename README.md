# Workflows - Compose - withCompositionRoot crash sample repo

When using `withCompositionRoot` in order to establish a Composable that should wrap
all `composeViewFactories`, along with renderings that implement `AndroidViewRendering`, the app
will crash when attempting to find a `ViewFactory` to render an `AndroidViewRendering`.

The root cause of this issue is how the logic to wrap ViewFactories is implemented. It relies
on `ViewRegistry.getFactoryFor` to return a valid view factory for every rendering class, but the
logic to return a `ViewFactory` for an `AndroidViewRendering` is based on a rendering _instance_,
not a rendering class. The logic that takes `AndroidViewRendering`'s instances into account is done
by `ViewRegistry.getFactoryForRendering`, which is what `ViewRegistry.buildView` uses.

## Reproduction

Attempt to start this application and it should crash immediately. You can switch the initial state
in `MainWorkflow` from `ShowingViewRendering` to `ShowingComposeRendering` and the app will still
crash.

Removing the `withCompositionRoot` in `MainActivity` will allow the app to start as normal.