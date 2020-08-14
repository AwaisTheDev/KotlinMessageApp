package com.selfworking.messageapp.Messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.selfworking.messageapp.Model.chatMessage
import com.selfworking.messageapp.Model.user
import com.selfworking.messageapp.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*


class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG = "ChatLog"
    }
    var toUser: user? = null

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        chat_log_view.adapter = adapter
        toUser= intent.getParcelableExtra<user>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = toUser?.username
        //setDummyData()
        listenForMessages()

        buttonMessage.setOnClickListener{
            sendNewMessage()
        }
    }

    private  fun listenForMessages(){

        val  fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid

        val reference = FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId")


        reference.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(chatMessage::class.java)
                Log.d(TAG , "Message is ${message?.text}")

                if(message?.fromId == FirebaseAuth.getInstance().uid ){
                    val currentUser = LatestMessgesActivity.currentUser
                    adapter.add(chatToItem( currentUser!!, message?.text.toString()))
                }else {
                    adapter.add(chatFromItem(toUser!!, message?.text.toString()))
                }
                chat_log_view.scrollToPosition(adapter.itemCount-1)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }

    private fun sendNewMessage(){
        Log.d(TAG ,"Trying to Send a message" )


        val user = intent.getParcelableExtra<user>(NewMessageActivity.USER_KEY)
        val text = editText.text.toString()
        val fromId =  FirebaseAuth.getInstance().uid.toString()
        val toId = user?.uid


        //val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val reference = FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId").push()
        val toreference = FirebaseDatabase.getInstance().getReference("/messages/$toId/$fromId").push()
        val message = chatMessage(text , reference.key.toString() , fromId, toId!! , System.currentTimeMillis()/1000)

        reference.setValue(message)
            .addOnSuccessListener {
                Log.d(TAG ,"Message Saved ${reference.key}")
                editText.text.clear()
                chat_log_view.scrollToPosition(adapter.itemCount-1)
            }

        toreference.setValue(message)

        val latestMessageReference = FirebaseDatabase.getInstance().getReference("latest-messages/$fromId/$toId")
        latestMessageReference.setValue(message)

        val latestMessageTOReference = FirebaseDatabase.getInstance().getReference("latest-messages/$toId/$fromId")
        latestMessageTOReference.setValue(message)

    }




}


class chatFromItem(val user: user ,val text: String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text= text
        val uri = user.profileImageUrl
        val target = viewHolder.itemView.imageView_from_image

        Picasso.get().load(uri).into(target)

    }
}

class chatToItem(val user: user ,val text: String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
       viewHolder.itemView.textView_to_row.text= text
        val uri = user.profileImageUrl
        val target = viewHolder.itemView.imageView_to_image

        Picasso.get().load(uri).into(target)
    }
}