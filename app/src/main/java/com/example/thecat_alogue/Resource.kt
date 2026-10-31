package com.example.thecat_alogue

data class Resource<out T>(
    val status: Status,
    val data: T? = null,
    val message: String? = null
) {
    enum class Status { SUCCESS, ERROR, LOADING }

    val isLoading: Boolean get() = status == Status.LOADING
    val isSuccess: Boolean get() = status == Status.SUCCESS
    val isError: Boolean get() = status == Status.ERROR

    companion object {
        fun <T> success(data: T): Resource<T> = Resource(Status.SUCCESS, data)
        fun <T> error(msg: String): Resource<T> = Resource(Status.ERROR, message = msg)
        fun <T> loading(): Resource<T> = Resource(Status.LOADING)
    }
}
