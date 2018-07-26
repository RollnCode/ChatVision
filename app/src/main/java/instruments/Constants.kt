package instruments

import com.rollncode.chatVision.BuildConfig

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.24
 */
const val PHONE_NUMBER_MIN_LENGTH = 6
const val AUTH_PROVIDER_TIMEOUT = 10L
const val CODE_RESENT_TIMEOUT = 5L
const val USER_NAME_MIN_LENGTH = 3

const val QB_APP_ID = BuildConfig.QB_APP_ID
const val QB_AUTH_KEY = BuildConfig.QB_AUTH_KEY
const val QB_AUTH_SECRET = BuildConfig.QB_AUTH_SECRET
const val QB_ACCOUNT_KEY = BuildConfig.QB_ACCOUNT_KEY
const val QB_CHAT_ENDPOINT = BuildConfig.QB_CHAT_ENDPOINT

const val DB_URL = BuildConfig.DB_URL
const val DB_PASSWORDS_KEY = BuildConfig.DB_PASSWORDS_KEY

//const val QB_APP_ID = "63900"
//const val QB_AUTH_KEY = "edhFGU4kftKAL22"
//const val QB_AUTH_SECRET = "O8Hym-KsJqCQjsD"
//const val QB_ACCOUNT_KEY = "JpjfUdfDRCWnyooXX5Dr"
//const val QB_CHAT_ENDPOINT = "chat.quickblox.com"
//
//const val DB_URL = "https://phone-chat-vision.firebaseio.com/"
//const val DB_PASSWORDS_KEY = "dbPassKey"

const val USER_LOGIN_SUFFIX = "@chatvision.rollncode.com"

const val USERS_PER_PAGE = 100 // 100 is max value
const val DIALOG_MESSAGES_LIMIT = 20
const val SORT_BY_DATE = "date_sent"