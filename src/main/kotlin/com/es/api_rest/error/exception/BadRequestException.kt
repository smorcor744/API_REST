package com.es.api_rest.error.exception

class BadRequestException(message :String): Exception("Bad request exception (401). $message") {
}