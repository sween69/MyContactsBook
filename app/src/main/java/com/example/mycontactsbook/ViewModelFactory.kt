package com.example.mycontactsbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
class ViewModelFactory(
    private val contactRepository: ContactRepository,
    private val viewModelClass: Class<out ViewModel>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(viewModelClass)) {
            return viewModelClass.getConstructor(ContactRepository::class.java)
                .newInstance(contactRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}