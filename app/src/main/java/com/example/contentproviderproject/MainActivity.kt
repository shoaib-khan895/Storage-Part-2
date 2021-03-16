package com.example.contentproviderproject

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    companion object {
        val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loadContacts = findViewById<ImageView>(R.id.loadContacts)

        loadContacts.setOnClickListener { loadContacts() }
    }

    private fun loadContacts() {
        val listContacts = findViewById<TextView>(R.id.listContacts)
        var builder = StringBuilder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                        Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                    PERMISSIONS_REQUEST_READ_CONTACTS)
        } else {
            builder = getContacts()
            listContacts.text = builder.toString()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            }
        }
    }

    private fun getContacts(): StringBuilder {
        val builder = StringBuilder()
        val resolver: ContentResolver = contentResolver;
        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                null)

        if (cursor?.count!! > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = (cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

                if (phoneNumber > 0) {
                    val cursorPhone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)

                    if (cursorPhone != null) {
                        if(cursorPhone.count > 0) {
                            while (cursorPhone.moveToNext()) {
                                val phoneNumValue = cursorPhone.getString(
                                        cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                builder.append("Contact: ").append(name).append(", Phone Number: ").append(
                                        phoneNumValue).append("\n\n")
                                Log.e("Name ===>",phoneNumValue);
                            }
                        }
                    }
                    if (cursorPhone != null) {
                        cursorPhone.close()
                    }
                }
            }
        }
        cursor.close()
        return builder
    }
}