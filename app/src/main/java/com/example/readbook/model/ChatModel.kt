package com.example.readbook.model

import kotlin.collections.HashMap

class ChatModel (val users: HashMap<String, Boolean> = HashMap(),
                 val comments : HashMap<String, Comment> = HashMap()){
    class Comment(val uid: String? = null, var message: String? = null, val time: String? = null)
}