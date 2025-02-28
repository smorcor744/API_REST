package com.es.api_rest.service

import com.es.api_rest.dto.TareaCompletadaDTO
import com.es.api_rest.dto.TareaDTO
import com.es.api_rest.model.Tareas
import com.es.api_rest.repository.TareaRepository
import com.es.api_rest.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class TareaService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository
    @Autowired
    private lateinit var tareaRepository: TareaRepository
    @Autowired
    private lateinit var externalApiService: ExternalApiService

    fun getAllTareas(authentication: Authentication): List<Tareas>? {
         if (authenticate(authentication,null) ) {
            return tareaRepository.findAll()
        } else {
            val usuario = usuarioRepository.findByUsername(authentication.name)
            return tareaRepository.findByUsername(usuario.get().username)
        }
    }

    fun getTareasByUsername(authentication: Authentication, username: String?): List<Tareas>? {
        return if (authenticate(authentication, username) && username != null ) {
            tareaRepository.findByUsername(username)
        } else null
    }

    fun authenticate(authentication: Authentication, username: String?): Boolean {
        if (authentication.authorities.any {it.authority == "ROLE_ADMIN" } ) return true
        if ( username.isNullOrBlank() && authentication.name == username) {
            return true
        }
        return false
    }

    fun insertTareas(authentication: Authentication,tareas: TareaDTO): Tareas? {
        val tarea = Tareas(
            username = tareas.username,
            descripcion = tareas.descripcion,
            titulo = tareas.titulo
        )
        if (authenticate(authentication, tareas.username) &&
            !tareaRepository.existsByTituloAndUsername(tareas.titulo, tareas.username)) {
            tareaRepository.insert(tarea)
            return tarea
        }
        return null
    }

    fun delete(authentication: Authentication, tarea: TareaCompletadaDTO): Boolean {
        if (tareaRepository.existsByTituloAndUsername(tarea.titulo, tarea.username)) {
            if (authenticate(authentication, tarea.username)) {
                val tareaa = tareaRepository.findByUsernameAndTitulo(tarea.username, tarea.titulo)
                tareaRepository.delete(tareaa.get())
                return true
            }
        }

        return false
    }

    fun tareaCompletada(authentication: Authentication, tarea2: TareaCompletadaDTO): Tareas? {
        val tarea = tareaRepository.findByUsernameAndTitulo(titulo = tarea2.titulo,username = tarea2.username) ?: return null
        if (tarea.get().estado == "pendiente") {
            val new = Tareas(
                tarea.get()._id,
                tarea.get().username,
                tarea2.titulo,
                tarea.get().descripcion,
                "completada",
                tarea.get().fechaCreacion
            )
            if (authenticate(authentication, tarea2.username)) {
                tareaRepository.save(new)
                return new
            }
        }
        return null
    }


}