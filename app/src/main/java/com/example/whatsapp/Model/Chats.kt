package com.example.whatsapp.Model

class Chats() {
    var message:String?=null
    var type:String?=null
    var from:String?=null

    constructor(message: String, type: String, from: String):this() {
        this.message = message
        this.type = type
        this.from = from
    }

}