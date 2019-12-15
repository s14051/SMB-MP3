package todo_database

class Shop(
        var id: String?,
        var name: String,
        var description: String,
        var radius: Float,
        var coordinates: String) {
    constructor() : this(null, "", "", -1.0f, "")
}