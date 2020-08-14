package com.selfworking.messageapp.Messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.selfworking.messageapp.Model.user
import com.selfworking.messageapp.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.new_message_user_list.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title ="New Message"

//        val adapter = GroupAdapter<GroupieViewHolder>()
//
//        adapter.add(userItem())
//        adapter.add(userItem())
//        adapter.add(userItem())
//
//        newMessageRecyclerView.adapter = adapter

        fetchUsers()
    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(){
         val ref= FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach{
                    it.toString()
                    Log.d( "xxxxxxxxxx", "${it.toString()}")
                    val user =  it.getValue(user::class.java)
                    if(user!= null){
                        if(FirebaseAuth.getInstance().uid != user.uid){
                            adapter.add(userItem(user))
                        }

                    }
                }
                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as userItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    //intent.putExtra(USER_KEY,  userItem.user.username)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()

                }
                newMessageRecyclerView.adapter = adapter
            }
        })
    }
}


class  userItem(val user: user): Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.userlist_username_newMessae.text = user.username
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_userList_newMessae)
    }
    override fun getLayout(): Int {
        return R.layout.new_message_user_list
    }
}