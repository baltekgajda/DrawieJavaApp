package drawie;

import io.socket.client.Socket;
import javafx.scene.paint.Color;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Any;
import org.mockito.internal.util.reflection.Whitebox;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class ModelTests {

    private String roomUrlTest = "https://drawie.herokuapp.com/?room=3332b08b-a9f5-4c40-32s21c-9ea26a3c5ef1";

    @InjectMocks
    Model model;

    @Mock
    Socket socket;

    @Mock
    Vector<int[]> stroke;

    @Mock
    RoomController roomController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void joinRoomEmptyStringReturnsFalse() {
        assertEquals(model.joinRoom(""), false);
    }

    @Test
    public void joinRoomWrongUrlReturnsFalse() {
        assertEquals(model.joinRoom("jestem zlym urlem"), false);
    }

    @Test
    public void joinRoomCorrectRoomURLReturnsTrue() {
        assertEquals(model.joinRoom(roomUrlTest), true);
    }

    @Test
    public void copiedURLIsInClipboardWhenCopyIsDone() {
        Whitebox.setInternalState(model, "roomURL", "ABCD");
        model.copyURLToClipboard();
        String data = "";
        try {
            data = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals("ABCD", data);
    }

    @Test
    public void handleRedoClickEmitsRedoEvent() {
        Whitebox.setInternalState(model, "socket", socket);
        model.handleRedoClick();
        Mockito.verify(socket, Mockito.atLeastOnce()).emit("redo");
    }

    @Test
    public void handleUndoClickEmitsUndoEvent() {
        Whitebox.setInternalState(model, "socket", socket);
        model.handleUndoClick();
        Mockito.verify(socket, Mockito.atLeastOnce()).emit("undo");
    }

    @Test
    public void mousePressedWithBucketFillSendsBucket() {
        Whitebox.setInternalState(model, "mStroke", stroke);
        model.manageOnMousePressed(true, 0, 0, new Color(0, 0, 0, 0));
        Mockito.verify(stroke, Mockito.never()).add(new int[]{0, 0});
    }

    @Test
    public void mousePressedWithoutBucketFillSendsBucket() {
        Whitebox.setInternalState(model, "mStroke", stroke);
        Whitebox.setInternalState(model, "roomController", roomController);
        model.manageOnMousePressed(false, 0, 0, new Color(0, 0, 0, 0));
        Mockito.verify(roomController, Mockito.atLeastOnce()).beginUserStroke(0, 0);
    }


    @Test
    public void mouseReleasedWithBucketFillDoNothing() {
        Whitebox.setInternalState(model, "mStroke", stroke);
        Whitebox.setInternalState(model, "socket", socket);
        model.manageOnMouseReleased(true, new Color(0,0,0,0), "","",0);
        Mockito.verify(socket, Mockito.never()).emit("stroke");
    }

    @Test
    public void mouseDraggedWithBucketFillDoNothing() {
        Whitebox.setInternalState(model, "mStroke", stroke);
        Whitebox.setInternalState(model, "socket", socket);
        model.manageOnMouseDragged(true, 0, 0);
        Mockito.verify(socket, Mockito.never()).emit("stroke");
        Mockito.verify(stroke, Mockito.never()).add(new int[]{0, 0});
    }

    @Test
    public void mouseReleasedWithoutBucketFillSendStroke() {
        Vector<int[]> str = new Vector<>();
        str.add(new int[]{0,0});
        Whitebox.setInternalState(model, "mStroke", str);
       // Whitebox.setInternalState(model, "mStroke", stroke);
        Whitebox.setInternalState(model, "socket", socket);
        model.manageOnMouseReleased(false, new Color(0,0,0,0), "","",0);
        Mockito.verify(socket, Mockito.atLeastOnce()).emit(eq("stroke"), any());
    }

    @Test
    public void mouseDraggedWithoutBucketFill() {
        Whitebox.setInternalState(model, "mStroke", stroke);
        Whitebox.setInternalState(model, "socket", socket);
        model.manageOnMouseDragged(false, 0, 0);
        Mockito.verify(stroke, Mockito.atLeastOnce()).add(new int[]{0, 0});
    }
}
