package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest {

    // region constants
    public static final String ID = "id";
    public static final String FULL_NAME = "fullName";
    public static final String IMAGE_URL = "imageUrl";
    public static final String FILTER = "filter";
    public static final int AGE = 18;
    public static final String FULL_PHONE_NUMBER = "fullPhoneNumber";

    // endregion constants

    // region helper fields
    @Mock
    GetContactsHttpEndpoint mGetContactsHttpEndpointMock;
    @Mock
    FetchContactsUseCase.Listener mListenerMock1;
    @Mock
    FetchContactsUseCase.Listener mListenerMock2;

    @Captor
    ArgumentCaptor<List<Contact>> mAcListContactItem;

    // endregion helper fields
    FetchContactsUseCase SUT;

    @Before
    public void setup() throws Exception {
        SUT = new FetchContactsUseCase(mGetContactsHttpEndpointMock);
        success();
    }

    @Test
    public void fetchContacts_filterPassedToEndpoint() {
        // Arrange
        ArgumentCaptor<String> acString = ArgumentCaptor.forClass(String.class);
        // Act
        SUT.fetchContactsAndNotify(FILTER);
        // Assert
        verify(mGetContactsHttpEndpointMock).getContacts(acString.capture(), any(Callback.class));
        assertThat(acString.getValue(), is(FILTER));
    }

    @Test
    public void fetchContacts_success_observersNotifiedWithCorrectData() {
        // Arrange
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER);
        // Assert
        verify(mListenerMock1).onContactsFetched(mAcListContactItem.capture());
        verify(mListenerMock2).onContactsFetched(mAcListContactItem.capture());
        List<List<Contact>> captures = mAcListContactItem.getAllValues();
        List<Contact> capture1 = captures.get(0);
        List<Contact> capture2 = captures.get(1);
        assertThat(capture1, is(getContacts()));
        assertThat(capture2, is(getContacts()));
    }

    @Test
    public void fetchContacts_success_unsubscribedObserversNotNotified() {
        // Arrange
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.unregisterListener(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER);
        // Assert
        verify(mListenerMock1).onContactsFetched(any(List.class));
        verifyNoMoreInteractions(mListenerMock2);
    }

    @Test
    public void fetchContacts_generalError_observersNotifiedOfFailure() {
        // Arrange
        generalError();
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER);
        // Assert
        verify(mListenerMock1).onFetchContactsFailed();
        verify(mListenerMock2).onFetchContactsFailed();
    }

    @Test
    public void fetchContacts_networkError_observersNotifiedOfFailure() {
        // Arrange
        networkError();
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER);
        // Assert
        verify(mListenerMock1).onNetworkError();
        verify(mListenerMock2).onNetworkError();
    }

    // region helper methods
    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactItemsSchemes());
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void generalError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private List<ContactSchema> getContactItemsSchemes() {
        List<ContactSchema> schemas = new ArrayList<>();
        schemas.add(new ContactSchema(ID, FULL_NAME, FULL_PHONE_NUMBER, IMAGE_URL, AGE));
        return schemas;
    }

    private List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(ID, FULL_NAME, IMAGE_URL));
        return contacts;
    }

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}