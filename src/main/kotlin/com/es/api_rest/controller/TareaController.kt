package com.es.api_rest.controller

import com.es.api_rest.dto.TareaCompletadaDTO
import com.es.api_rest.dto.TareaDTO
import com.es.api_rest.model.Tareas
import com.es.api_rest.service.TareaService
import com.es.api_rest.service.UsuarioService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RequestMapping("/tareas")
@RestController
class TareaController {

    @Autowired
    private lateinit var tareaService: TareaService

    @GetMapping
    fun obtenerTareas(
        authentication: Authentication,
        httpRequest: HttpServletRequest
    ): ResponseEntity<List<Tareas>> {

        val tareas = tareaService.getAllTareas(authentication)

        return if (tareas != null) ResponseEntity(tareas, HttpStatus.OK) else ResponseEntity(HttpStatus.FORBIDDEN)
    }

    @GetMapping("/{username}")
    fun obtenerTareasUsuario(
        authentication: Authentication,
        httpRequest: HttpServletRequest,
        @PathVariable username: String?
    ): ResponseEntity<List<Tareas>> {

        val tareas = tareaService.getTareasByUsername(authentication,username)

        return if (tareas != null) ResponseEntity(tareas, HttpStatus.OK) else ResponseEntity(HttpStatus.FORBIDDEN)
    }

    @PostMapping
    fun crearTarea(
        authentication: Authentication,
        httpRequest: HttpServletRequest,
        @RequestBody tarea: TareaDTO
    ): ResponseEntity<Tareas> {
        val tareaInsertada = tareaService.insertTareas(authentication,tarea)
        if (tareaInsertada != null){
            return ResponseEntity(tareaInsertada, HttpStatus.CREATED)
        }else return ResponseEntity(null, HttpStatus.FORBIDDEN)

    }

    @DeleteMapping
    fun eliminarTarea(
        authentication: Authentication,
        httpRequest: HttpServletRequest,
        @RequestBody tarea: TareaCompletadaDTO): ResponseEntity<String> {
        if (tareaService.delete(authentication, tarea)) {
            return ResponseEntity("Tarea eliminada con exito.",HttpStatus.OK)
        } else {
            return ResponseEntity("No se a podido eliminar esta tarea.",HttpStatus.FORBIDDEN)
        }

    }

    @PutMapping("/completar")
    fun marcarComoHechaPorTitulo(
        authentication: Authentication,
        httpRequest: HttpServletRequest,
        @RequestBody tarea1: TareaCompletadaDTO
    ): ResponseEntity<Tareas> {
        val tarea = tareaService.tareaCompletada(authentication,tarea1)
        if (tarea != null){
            return ResponseEntity(tarea, HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.FORBIDDEN)
    }

}