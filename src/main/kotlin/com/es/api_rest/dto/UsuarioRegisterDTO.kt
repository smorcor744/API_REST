package com.es.api_rest.dto

import com.es.api_rest.model.Direccion

data class UsuarioRegisterDTO(
    val username: String,
    val email: String,
    val password: String,
    val passwordRepeat: String,
    val rol: String?,
    val direccion: Direccion
)
