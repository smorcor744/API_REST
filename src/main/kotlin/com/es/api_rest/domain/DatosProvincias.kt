package com.es.api_rest.domain

data class DatosProvincias(
    val update_date: String,
    val size: Int,
    val data: List<Provincia>?,
    val warning: String?,
    val error: String?
)
