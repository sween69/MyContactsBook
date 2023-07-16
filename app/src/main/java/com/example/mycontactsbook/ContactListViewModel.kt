package com.example.mycontactsbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
class ContactListViewModel(private val contactRepository: ContactRepository) : ViewModel() {
    init {
        loadContacts()
    }
    fun loadContacts(): LiveData<List<Contact>> {
        return contactRepository.loadContacts()
    }
}

