package com.es.api_rest.service

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
        if (authenticate(authentication, username) ) {
            return tareaRepository.findByUsername(authentication.name)
        } else return null
    }

    fun authenticate(authentication: Authentication, username: String?): Boolean {
        return if (authentication.authorities.any {it.authority == "ROLE_ADMIN" } || authentication.name == username || authentication.name != null) {
            true
        } else false
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
        }
        return null
    }

    fun delete(authentication: Authentication, id: String): Boolean {
        val tarea = tareaRepository.findById(id).orElse(null)

        if (tarea != null && authenticate(authentication, tarea.username)) {
            tareaRepository.delete(tarea)
            return true
        }

        return false
    }

    fun tareaCompletada(authentication: Authentication, titulo: String): Tareas? {
        val tarea = tareaRepository.findByTitulo(titulo) ?: return null
        if (tarea.get().estado != "pendiente") {
            val new = Tareas(
                tarea.get()._id,
                tarea.get().username,
                titulo,
                tarea.get().descripcion,
                "completada",
                tarea.get().fechaCreacion
            )
            if (authenticate(authentication, tarea.get().username)) {

                return new
            }
        }
        return null
    }


}