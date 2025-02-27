package com.es.api_rest.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.Date

@Document("Tareas")
data class Tareas(
    @BsonId
    val _id : ObjectId? = null,
    val username: String ,
    val titulo: String,
    val descripcion: String,
    val estado: String? = "pendiente",
    val fechaCreacion: Date? = Date.from(Instant.now()),
)
