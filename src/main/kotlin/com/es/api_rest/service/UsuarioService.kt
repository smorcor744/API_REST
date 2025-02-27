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
    @Autowired
    private lateinit var externalApiService: ExternalApiService

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

        // Comprobar email con regex
        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

        if (!emailPattern.containsMatchIn(usuarioInsertadoDTO.email)) {
            throw Exception("**Error al insertar el Email ${usuarioInsertadoDTO.email}, formato inválido**")
        }
        if (!usuarioRepository.findByEmail(usuarioInsertadoDTO.email).isEmpty()) {
            throw Exception("**Error al insertar el Email ${usuarioInsertadoDTO.email}, ya existente**")
        }


        // Comprobar la provincia
        val datosProvincias = externalApiService.obtenerProvinciasDesdeApi()
        var cpro = ""
        if(datosProvincias != null) {
            if(datosProvincias.data != null) {
                val provinciaEncontrada = datosProvincias.data.stream().filter {
                    it.PRO == usuarioInsertadoDTO.direccion.provincia.uppercase()
                }.findFirst().orElseThrow {
                    BadRequestException("Provincia ${usuarioInsertadoDTO.direccion.provincia} no encontrada")
                }
                cpro = provinciaEncontrada.CPRO
            }
        }

        // Comprobar el municipio
        val datosMunicipios = externalApiService.obtenerMunicipiosDesdeApi(cpro)
        if(datosMunicipios != null) {
            if(datosMunicipios.data != null) {
                datosMunicipios.data.stream().filter {
                    it.DMUN50 == usuarioInsertadoDTO.direccion.municipio.uppercase()
                }.findFirst().orElseThrow {
                    BadRequestException("Municipio ${usuarioInsertadoDTO.direccion.municipio} incorrecto")
                }
            }
        }


        val usuario = Usuario(
            _id = null,
            username = usuarioInsertadoDTO.username,
            password = passwordEncoder.encode(usuarioInsertadoDTO.password),
            email = usuarioInsertadoDTO.email,
            roles = usuarioInsertadoDTO.rol,
            direccion = usuarioInsertadoDTO.direccion
        )

        usuarioRepository.insert(usuario)

        return UsuarioDTO(usuario.username, usuario.email,usuario.roles)

    }
}