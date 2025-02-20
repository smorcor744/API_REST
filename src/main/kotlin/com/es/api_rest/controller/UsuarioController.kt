package com.es.api_rest.controller

import com.es.api_rest.dto.LoginUsuarioDTO
import com.es.api_rest.dto.UsuarioDTO
import com.es.api_rest.dto.UsuarioRegisterDTO
import com.es.api_rest.error.exception.UnauthorizedException
import com.es.api_rest.service.TokenService
import com.es.api_rest.service.UsuarioService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.*

@RequestMapping("/usuarios")
@RestController
class UsuarioController {

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager
    @Autowired
    private lateinit var tokenService: TokenService
    @Autowired
    private lateinit var usuarioService: UsuarioService

    @GetMapping("/hola")
    fun hola(): String{
        return "<h1>HOLA MUNDO</h1>"
    }

    @PostMapping("/register")
    fun insert(
        httpRequest: HttpServletRequest,
        @RequestBody usuarioRegisterDTO: UsuarioRegisterDTO
    ) : ResponseEntity<UsuarioDTO>?{


        val newUsuario = usuarioService.insertUser(usuarioRegisterDTO)

        return if(newUsuario != null){
            ResponseEntity<UsuarioDTO>(newUsuario, HttpStatus.CREATED)
        } else ResponseEntity<UsuarioDTO>(null, HttpStatus.INTERNAL_SERVER_ERROR)



    }

    @PostMapping("/login")
    fun login(@RequestBody usuario: LoginUsuarioDTO) : ResponseEntity<Any>? {

        val authentication: Authentication
        try {
            authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(usuario.username, usuario.password))
        } catch (e: AuthenticationException) {
            throw UnauthorizedException("Credenciales incorrectas")
        }

        // SI PASAMOS LA AUTENTICACIÓN, SIGNIFICA QUE ESTAMOS BIEN AUTENTICADOS
        // PASAMOS A GENERAR EL TOKEN
        val token = tokenService.generarToken(authentication)

        return ResponseEntity(mapOf("token" to token), HttpStatus.CREATED)
    }

}