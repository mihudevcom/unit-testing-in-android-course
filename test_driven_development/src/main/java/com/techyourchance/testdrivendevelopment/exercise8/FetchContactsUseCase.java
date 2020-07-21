package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCase {

    public interface Listener {
        void onContactsFetched(List<Contact> capture);

        void onFetchContactsFailed();

        void onNetworkError();
    }

    private final List<Listener> listeners = new ArrayList<>();
    private GetContactsHttpEndpoint getContactsHttpEndpoint;

    public FetchContactsUseCase(GetContactsHttpEndpoint mGetContactsHttpEndpoint) {
        this.getContactsHttpEndpoint = mGetContactsHttpEndpoint;
    }

    public void registerListener(Listener mListener) {
        listeners.add(mListener);
    }

    public void fetchContactsAndNotify(String filter) {
        getContactsHttpEndpoint.getContacts(filter, new GetContactsHttpEndpoint.Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> contacts) {
                for (Listener listener : listeners) {
                    listener.onContactsFetched(contactsFromSchemas(contacts));
                }
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) {
                switch (failReason) {
                    case GENERAL_ERROR:
                        for (Listener listener : listeners) {
                            listener.onFetchContactsFailed();
                        }
                        break;
                    case NETWORK_ERROR:
                        for (Listener listener : listeners) {
                            listener.onNetworkError();
                        }
                        break;
                }
            }
        });
    }

    private List<Contact> contactsFromSchemas(List<ContactSchema> contactSchemas) {
        List<Contact> contacts = new ArrayList<>();
        for (ContactSchema contactSchema : contactSchemas) {
            contacts.add(new Contact(
                    contactSchema.getId(),
                    contactSchema.getFullName(),
                    contactSchema.getImageUrl()));
        }
        return contacts;
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }
}
