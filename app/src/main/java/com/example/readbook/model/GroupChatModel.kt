package com.example.readbook.model

class GroupChatModel (val users: HashMap<String, Boolean> = HashMap(),
<<<<<<< HEAD
                      val comments : HashMap<String, Comment?> = HashMap(),
=======
                      var comments : HashMap<String, Comment> = HashMap(),
>>>>>>> f83bb6f608c05d8266d74937d8aa5f10ba66929b
                      var userLimit : Int = 0, var groupName : String = "", var groupDes : String = "", var groupId : String = ""){
    class Comment(val uid: String? = null, val message: String? = null, val time: String? = null)
}