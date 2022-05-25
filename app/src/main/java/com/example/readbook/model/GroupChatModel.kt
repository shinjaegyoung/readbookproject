package com.example.readbook.model

class GroupChatModel (val users: HashMap<String, Boolean> = HashMap(),
                      var comments : HashMap<String, Comment> = HashMap(),
                      var userLimit : Int = 0, var groupName : String = "", var groupDes : String = "", var groupId : String = ""){
    class Comment(val uid: String? = null, val message: String? = null, val time: String? = null)
}