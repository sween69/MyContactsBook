package com.example.mycontactsbook

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
class ContactListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var contactListViewModel: ContactListViewModel
    private lateinit var contactRepository: ContactRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)

        (requireActivity() as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.toolbar))
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_search)
        setHasOptionsMenu(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_contact_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        contactAdapter = ContactAdapter(emptyList())
        recyclerView.adapter = contactAdapter

        contactRepository = ContactRepository(requireContext().applicationContext)
        contactListViewModel = ViewModelProvider(
            this,
            ViewModelFactory(contactRepository, ContactListViewModel::class.java)
        )[ContactListViewModel::class.java]

        contactListViewModel.loadContacts().observe(viewLifecycleOwner) { contacts ->
            contactAdapter.setContacts(contacts)
        }

        contactAdapter.setOnItemClickListener { contact ->
            val action = ContactListFragmentDirections.actionContactListFragmentToContactInfoFragment(contact.id)
            findNavController().navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                // Handle add action
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
