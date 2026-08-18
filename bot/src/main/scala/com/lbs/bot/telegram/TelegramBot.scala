package com.lbs.bot.telegram

import com.bot4s.telegram.models.InlineKeyboardMarkup
import com.lbs.bot.PollBot
import com.lbs.bot.model.*
import com.lbs.bot.telegram.TelegramModelConverters.*

class TelegramBot(onCommand: Command => Unit, botToken: String, allowedChatId: Long) extends PollBot[TelegramEvent] {

  private val telegramBot = new TelegramClient(onReceive, botToken)
  telegramBot.run()

  def sendMessage(chatId: String, text: String): Unit =
    forAllowedChat(chatId)(telegramBot.sendMessage(chatId.toLong, text))

  def sendMessage(chatId: String, text: String, buttons: Option[InlineKeyboard] = None): Unit =
    forAllowedChat(chatId) {
      telegramBot.sendMessage(chatId.toLong, text, replyMarkup = buttons.map(_.mapTo[InlineKeyboardMarkup]))
    }

  def sendEditMessage(chatId: String, messageId: String, buttons: Option[InlineKeyboard]): Unit =
    forAllowedChat(chatId) {
      telegramBot.sendEditMessage(
        chatId.toLong,
        messageId.toInt,
        replyMarkup = buttons.map(_.mapTo[InlineKeyboardMarkup])
      )
    }

  def sendEditMessage(chatId: String, messageId: String, text: String, buttons: Option[InlineKeyboard] = None): Unit =
    forAllowedChat(chatId) {
      telegramBot.sendEditMessage(
        chatId.toLong,
        messageId.toInt,
        text,
        replyMarkup = buttons.map(_.mapTo[InlineKeyboardMarkup])
      )
    }

  def sendFile(chatId: String, filename: String, contents: Array[Byte], caption: Option[String] = None): Unit =
    forAllowedChat(chatId)(telegramBot.sendFile(chatId.toLong, filename, contents, caption))

  override protected def onReceive(command: TelegramEvent): Unit = {
    val mappedCommand = command.mapTo[Command]
    forAllowedChat(mappedCommand.source.chatId)(onCommand(mappedCommand))
  }

  private def forAllowedChat(chatId: String)(action: => Unit): Unit =
    if (TelegramBot.isAllowedChat(chatId, allowedChatId)) action
}

object TelegramBot {
  private[telegram] def isAllowedChat(chatId: String, allowedChatId: Long): Boolean =
    chatId == allowedChatId.toString
}
