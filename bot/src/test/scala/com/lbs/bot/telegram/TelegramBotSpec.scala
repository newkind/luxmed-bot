package com.lbs.bot.telegram

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TelegramBotSpec extends AnyFunSuite with Matchers {

  test("only the configured Telegram chat is allowed") {
    TelegramBot.isAllowedChat("123456789", 123456789L) shouldBe true
    TelegramBot.isAllowedChat("987654321", 123456789L) shouldBe false
  }
}
