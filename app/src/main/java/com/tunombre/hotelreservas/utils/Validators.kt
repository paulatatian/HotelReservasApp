package com.tunombre.hotelreservas.utils

object Validators {

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.length >= 10 && phone.all { it.isDigit() }
    }

    fun isValidName(name: String): Boolean {
        return name.length >= 2 && name.all { it.isLetter() || it.isWhitespace() }
    }
}