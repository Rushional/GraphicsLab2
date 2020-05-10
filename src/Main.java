import model.Lab2Model;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import ui.Drawer;
import ui.Window;

public class Main {
    public static void main(String[] args) {
        Display display = Display.getDefault();
        Window window = new Window(display);
        window.build();
        Drawer drawer = new Drawer(window, new GC(window.getCanvas()));
        window.show(drawer);
        drawer.drawCoordinatesGrid();
        drawer.runLab2(new Lab2Model(window));
        while (!window.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
