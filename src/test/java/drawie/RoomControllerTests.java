package drawie;

import io.socket.client.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoomControllerTests {
    @Mock
    Model model;
    @Mock
    Socket socket;
    @Mock
    View view;
    @Mock
    Vector<int[]> stroke;
    @InjectMocks
    RoomController roomController;
    private String roomUrlTest = "https://drawie.herokuapp.com/?room=3332b08b-a9f5-4c40-32s21c-9ea26a3c5ef1";

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenSettingModelItIsSet() {
        roomController.setModel(model);
        assertEquals(Whitebox.getInternalState(roomController, "model"), model);
    }

    @Test
    public void whenSettingViewItIsSet() {
        roomController.setView(view);
        assertEquals(Whitebox.getInternalState(roomController, "view"), view);
    }


}

