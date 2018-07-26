package utils

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.02
 */
class UnknownException : Exception("Unknown exception")

class WrongPhoneException : Exception("Wrong phone number")

class UnauthorizedException : Exception("User is not authorized")

class OfflineException : Exception("Not available in offline mode")