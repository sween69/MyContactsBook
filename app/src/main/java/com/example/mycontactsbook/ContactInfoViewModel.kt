package com.example.mycontactsbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class ContactInfoViewModel(private val contactRepository: ContactRepository) : ViewModel() {
    private val _contact = MutableLiveData<Contact>()
    val contact: LiveData<Contact>
        get() = _contact
    fun setContactById(contactId: String) {
        val contact = contactRepository.getContactById(contactId)
        _contact.value = contact
    }
}