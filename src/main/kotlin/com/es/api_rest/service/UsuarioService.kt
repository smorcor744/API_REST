package com.es.api_rest.service

import com.es.api_rest.dto.UsuarioDTO
import com.es.api_rest.dto.UsuarioRegisterDTO
import com.es.api_rest.error.exception.BadRequestException
import com.es.api_rest.error.exception.UnauthorizedException
import com.es.api_rest.model.Usuario
import com.es.api_rest.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService : UserDetailsService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder


    override fun loadUserByUsername(username: String?): UserDetails {
        val usuario: Usuario = usuarioRepository
            .findByUsername(username!!)
            .orElseThrow {
                UnauthorizedException("$username no existente")
            }

        return User.builder()
            .username(usuario.username)
            .password(usuario.password)
            .roles(usuario.roles)
            .build()
    }

    fun insertUser(usuarioInsertadoDTO: UsuarioRegisterDTO) : UsuarioDTO? {


            if (usuarioInsertadoDTO.username.isBlank() || usuarioInsertadoDTO.password.isBlank() || usuarioInsertadoDTO.passwordRepeat.isBlank()) throw BadRequestException("Uno o más campos vacios")

            if (!usuarioRepository.findByUsername(usuarioInsertadoDTO.username).isEmpty) {
                throw Exception("Usuario ${usuarioInsertadoDTO.username} existente")
            }

            if (usuarioInsertadoDTO.password != usuarioInsertadoDTO.passwordRepeat) {
                throw BadRequestException("Las contraseñas no coinciden")
            }

            if (usuarioInsertadoDTO.rol != null && usuarioInsertadoDTO.rol != "USER" && usuarioInsertadoDTO.rol != "ADMIN") {
                throw BadRequestException("Rol: ${usuarioInsertadoDTO.rol} incorrecto")
            }

            //Comprobar email

            val usuario = Usuario(
                _id = null,
                username = usuarioInsertadoDTO.username,
                email = usuarioInsertadoDTO.email,
                password = passwordEncoder.encode(usuarioInsertadoDTO.password),
                roles = usuarioInsertadoDTO.rol
            )
        usuarioRepository.insert(usuario)
        return UsuarioDTO(usuario.username, usuario.email,usuario.roles)

    }
}