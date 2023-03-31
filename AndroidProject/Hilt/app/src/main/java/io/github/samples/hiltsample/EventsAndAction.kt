package io.github.samples.hiltsample

sealed class Events
sealed class CardEvent: Events() {
    object Tap: CardEvent()
}

sealed class Action
sealed class CardAction: Action() {

}