package com.example.mycontactsbook

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
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
        val view = inflater.inflate(R.layout.fragment_contact_info, container, false)

        contactId = args.contactId

        contactRepository = ContactRepository(requireContext().applicationContext)
        contactInfoViewModel = ViewModelProvider(
            this,
            ViewModelFactory(contactRepository, ContactInfoViewModel::class.java)
        )[ContactInfoViewModel::class.java]

        contactInfoViewModel.contact.observe(viewLifecycleOwner) { contact ->
            updateContactInfo(contact)
        }

        contactInfoViewModel.setContactById(contactId)

        // Initialize views
        toolbar = view.findViewById(R.id.toolbar)
        firstNameEditText = view.findViewById(R.id.editText_firstName)
        lastNameEditText = view.findViewById(R.id.editText_lastName)
        emailEditText = view.findViewById(R.id.editText_email)
        phoneEditText = view.findViewById(R.id.editText_phone)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel)
        }

        toolbar.title = "Contact Info"

        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setHasOptionsMenu(true)

        return view
    }

    private fun updateContactInfo(contact: Contact) {
        with(contact) {
            firstNameEditText.setText(firstName)
            lastNameEditText.setText(lastName)
            emailEditText.setText(email)
            phoneEditText.setText(phone)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                if (isFormValid()) {
                    saveContact()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Main information is required",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
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
