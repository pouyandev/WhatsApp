package com.example.whatsapp.Model

class Users() {
    var display_name: String? = null
    var image: String? = null
    var thumb_image: String? = null
    var status: String? = null

    constructor(display_name: String, image: String, thumb_image: String, status: String):this() {
        this.display_name = display_name
        this.image = image
        this.thumb_image = thumb_image
        this.status = status

    }


}