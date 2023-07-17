package com.example.mycontactsbook

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

class ContactRepository(private val context: Context) {

    private val isFirstLaunchKey = "isFirstLaunch"

    private fun isAppFirstLaunch(): Boolean {
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return prefs.getBoolean(isFirstLaunchKey, true)
    }

    private fun setAppFirstLaunch(isFirstLaunch: Boolean) {
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean(isFirstLaunchKey, isFirstLaunch).apply()
    }

    fun loadContacts(): LiveData<List<Contact>> {
        val isFirstLaunch = isAppFirstLaunch()

        if (isFirstLaunch) {
            copyJsonFileToInternalStorage()
            setAppFirstLaunch(false)
        }

        val fileName = "data.json"
        val file = File(context.filesDir, fileName)
        val contactsJson = file.readText()
        val contactsType = object : TypeToken<List<Contact>>() {}.type
        val contacts = Gson().fromJson<List<Contact>>(contactsJson, contactsType)

        return MutableLiveData(contacts)
    }

    fun getContactById(contactId: String): Contact {
        return loadContacts().value?.find { it.id == contactId }
            ?: throw NoSuchElementException("No contact found with ID: $contactId")
    }

    fun updateContact(contact: Contact) {
        val contacts = loadContacts().value?.toMutableList() ?: mutableListOf()
        val index = contacts.indexOfFirst { it.id == contact.id }

        if (index != -1) {
            contacts[index] = contact
            val contactsJson = Gson().toJson(contacts)
            writeContactsJsonToFile("data.json", contactsJson)
        }
    }

    private fun writeContactsJsonToFile(fileName: String, contactsJson: String) {
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write(contactsJson.toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun copyJsonFileToInternalStorage() {
        val fileName = "data.json"
        val inputStream = context.assets.open(fileName)
        val outputFile = File(context.filesDir, fileName)
        val outputStream = outputFile.outputStream()

        inputStream.copyTo(outputStream)

        inputStream.close()
        outputStream.close()
    }
}
