package com.tunombre.hotelreservas.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatDateRange(entrada: Long, salida: Long): String {
        return "${formatDate(entrada)} - ${formatDate(salida)}"
    }

    fun calcularNoches(entrada: Long, salida: Long): Int {
        val diferencia = salida - entrada
        return (diferencia / (1000 * 60 * 60 * 24)).toInt()
    }
}