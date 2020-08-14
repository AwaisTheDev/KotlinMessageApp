package com.selfworking.messageapp.Messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.selfworking.messageapp.R
import com.selfworking.messageapp.LoginRegister.RegisterActivity
import com.selfworking.messageapp.Model.chatMessage
import com.selfworking.messageapp.Model.user
import com.selfworking.messageapp.Views.latestMessageRow
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_latest_messges.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.new_message_user_list.view.*
import java.util.*

class LatestMessgesActivity : AppCompatActivity() {

    companion object{
        var currentUser: user? = null
        val TAG = "LatestMessages"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messges)
        verifyIsUserLoggedIn()
        fetchCurrentUser()
        litenForLatesMessages()
        recyclerView_latestMessages.adapter=  adapter
        recyclerView_latestMessages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "123")
            val intent = Intent(this, ChatLogActivity::class.java)

            // we are missing the chat partner user

            val row = item as latestMessageRow
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
    }

    val latestMessagesMap = HashMap<String, chatMessage>()

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(latestMessageRow(it))
        }

    }

    private fun litenForLatesMessages(){
        val  fromId = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        reference.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newChatMessage = snapshot.getValue(chatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = newChatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val newChatMessage = snapshot.getValue(chatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = newChatMessage
                refreshRecyclerViewMessages()
            }
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })

    }

    private fun fetchCurrentUser(){
       val uID = FirebaseAuth.getInstance().uid
       Log.d("LatestMessageActivity" , "${uID}")
       val ref = FirebaseDatabase.getInstance().getReference("/users/${uID}")
           ref.addListenerForSingleValueEvent( object:ValueEventListener {
               override fun onCancelled(error: DatabaseError) {

               }
               override fun onDataChange(snapshot: DataSnapshot) {
                   currentUser = snapshot.getValue(user::class.java)
                   Log.d("LatestMessageActivity" , "${currentUser?.username}")
                   supportActionBar?.title = currentUser?.username
               }
               })

    }

    private fun verifyIsUserLoggedIn(){
        var uid =  FirebaseAuth.getInstance().uid

        if(uid == null)
        {
            val intent =  Intent(this , RegisterActivity::class.java )
            intent.flags =  Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.new_message ->{
                val intent =  Intent(this , NewMessageActivity::class.java )
                startActivity(intent)

            }
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent =  Intent(this , RegisterActivity::class.java )
                intent.flags =  Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}

