package com.es.api_rest.repository

import com.es.api_rest.model.Tareas
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TareaRepository : MongoRepository<Tareas,String> {
    fun findByUsername(username: String): List<Tareas>
    fun existsByTituloAndUsername(titulo: String, username: String): Boolean
    fun findByUsernameAndTitulo(username: String, titulo: String): Optional<Tareas>
    fun findByTitulo(titulo: String): Optional<Tareas>

}