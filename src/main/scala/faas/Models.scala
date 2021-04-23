package faas

case class Update(
    message: Option[Message],
    callback_query: Option[CallbackQuery]
)

case class CallbackQuery(
    data: String,
    from: From,
    message: CallbackMessage
)

case class CallbackMessage(
    chat: Chat,
    message_id: Int
)

case class From(
    id: Int
)

case class Message(
    text: String,
    chat: Chat
)

case class Chat(
    id: Int
)

case class InlineKeyboardMarkup(
    inline_keyboard: List[List[InlineKeyboardButton]]
)

case class InlineKeyboardButton(
    text: String,
    callback_data: String
)

case class SendImageBody(
    chat_id: Int,
    caption: String,
    reply_markup: InlineKeyboardMarkup,
    photo: Option[String],
    animation: Option[String]
)

case class UpdateImageBody(
    chat_id: Int,
    message_id: Int,
    media: Media,
    reply_markup: InlineKeyboardMarkup
)

case class Media(
    `type`: String,
    media: String,
    caption: String
)

case class MarsImage(
    id: String,
    title: String,
    url: String,
    prev: Option[String],
    next: Option[String],
    publish_date: String
)