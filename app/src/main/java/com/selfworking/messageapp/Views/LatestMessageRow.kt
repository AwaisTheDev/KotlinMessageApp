package com.selfworking.messageapp.Views

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.selfworking.messageapp.Model.chatMessage
import com.selfworking.messageapp.Model.user
import com.selfworking.messageapp.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class latestMessageRow(var chatMessage: chatMessage): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    var chatPartnerUser: user? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {


        viewHolder.itemView.latestMessage.text = chatMessage.text

        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(user::class.java)
                viewHolder.itemView.latestMessages_username.text = chatPartnerUser?.username

                val targetImageView = viewHolder.itemView.imageView
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}