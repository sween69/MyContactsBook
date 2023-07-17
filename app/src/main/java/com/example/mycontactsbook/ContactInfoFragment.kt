package com.example.mycontactsbook

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class ContactInfoFragment : Fragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private val args: ContactInfoFragmentArgs by navArgs()
    private lateinit var contactRepository: ContactRepository
    private lateinit var contactInfoViewModel: ContactInfoViewModel
    private lateinit var contactId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactId = args.contactId
        setupView(view)
        setupToolbar()
        setupViewModel()
    }

    private fun setupView(view: View) {
        // Initialize views
        toolbar = view.findViewById(R.id.toolbar)
        firstNameEditText = view.findViewById(R.id.editText_firstName)
        lastNameEditText = view.findViewById(R.id.editText_lastName)
        emailEditText = view.findViewById(R.id.editText_email)
        phoneEditText = view.findViewById(R.id.editText_phone)
    }

    private fun setupViewModel() {
        contactRepository = ContactRepository(requireContext().applicationContext)

        contactInfoViewModel = ViewModelProvider(
            this,
            ViewModelFactory(contactRepository, ContactInfoViewModel::class.java)
        )[ContactInfoViewModel::class.java]

        contactInfoViewModel.contact.observe(viewLifecycleOwner) { contact ->
            updateContactInfo(contact)
        }

        contactInfoViewModel.setContactById(contactId)
    }

    private fun setupToolbar() {

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel)
        }

        toolbar.title = "Contact Info"

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_details, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_save -> {
                        if (isFormValid()) {
                            saveContact()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Main information are required",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun updateContactInfo(contact: Contact) {
        with(contact) {
            firstNameEditText.setText(firstName)
            lastNameEditText.setText(lastName)
            emailEditText.setText(email)
            phoneEditText.setText(phone)
        }
    }

    private fun isFormValid(): Boolean {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        return firstName.isNotEmpty() && lastName.isNotEmpty()
    }

    private fun saveContact() {
        val updatedContact = Contact(
            contactId,
            firstNameEditText.text.toString(),
            lastNameEditText.text.toString(),
            emailEditText.text.toString(),
            phoneEditText.text.toString()
        )

        contactRepository.updateContact(updatedContact)

        Toast.makeText(requireContext(), "Contact saved", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }
}
