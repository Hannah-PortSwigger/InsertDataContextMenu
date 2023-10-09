import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class InsertDataContextMenu implements BurpExtension
{
    private static final String NAME = "Insert image data";
    private static final String DATA = "test\nnewline";

    @Override
    public void initialize(MontoyaApi api)
    {
        api.extension().setName(NAME);

        api.userInterface().registerContextMenuItemsProvider(new ContextMenuItemsProvider()
        {
            @Override
            public List<Component> provideMenuItems(ContextMenuEvent event)
            {
                MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().isPresent() ? event.messageEditorRequestResponse().get() : null;
                if (messageEditorHttpRequestResponse != null && messageEditorHttpRequestResponse.selectionContext().equals(MessageEditorHttpRequestResponse.SelectionContext.REQUEST))
                {
                    JMenuItem insertDataMenuItem = new JMenuItem("Insert data");
                    insertDataMenuItem.addActionListener(l -> {
                        insertData(messageEditorHttpRequestResponse);
                    });

                    return List.of(insertDataMenuItem);
                }

                return Collections.emptyList();
            }
        });
    }

    private void insertData(MessageEditorHttpRequestResponse messageEditorHttpRequestResponse)
    {
        int caretPosition = messageEditorHttpRequestResponse.caretPosition();
        HttpRequest originalRequest = messageEditorHttpRequestResponse.requestResponse().request();

        ByteArray fullRequestByteArray = originalRequest.toByteArray();
        int fullOriginalRequestLength = fullRequestByteArray.length();

        ByteArray prepend = fullRequestByteArray.subArray(0, caretPosition);

        ByteArray postpend = ByteArray.byteArrayOfLength(0);
        if (caretPosition < fullOriginalRequestLength)
        {
            postpend = fullRequestByteArray.subArray(caretPosition, fullOriginalRequestLength);
        }

        HttpRequest newRequest = HttpRequest.httpRequest(prepend.withAppended(DATA).withAppended(postpend)).withService(originalRequest.httpService());

        messageEditorHttpRequestResponse.setRequest(newRequest);
    }
}
