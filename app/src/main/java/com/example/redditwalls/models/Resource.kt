package com.example.redditwalls.models

data class Resource<T>(val status: Status, val data: T? = null, val errorMessage: String = "") {

    enum class Status {
        SUCCESS,
        LOADING,
        ERROR
    }

    companion object {
        fun <T> success(data: T) = Resource(Status.SUCCESS, data)

        fun <T> loading() = Resource<T>(Status.LOADING)

        fun <T> error(message: String) = Resource<T>(status = Status.ERROR, errorMessage = message)
    }
}