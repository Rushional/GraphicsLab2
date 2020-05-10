package ui;

//import model.Lab1Model;
import model.Lab2Model;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class Window extends Shell {
    private Composite canvas;
    private Button rerunLabButton;

    public Window(Display display/*, Lab1Model model*/) {
        super(display);
        setText("Лабораторная работа 2");
        setSize(1300, 920);
//        this.model = model;
    }

    public void build() {
        Color backgroundColor = new Color(getDisplay(), new RGB(173, 196, 228)); // Blue background
//        Color backgroundColor = new Color(getDisplay(), new RGB(255, 255, 255)); // Plain white
        Color textBoxColor = new Color(getDisplay(), new RGB(0, 0 ,0)); //Plain black
        setLayout(new GridLayout(1, false));

        rerunLabButton = new Button(this, SWT.PUSH);
        rerunLabButton.setText("Перестроить");
        rerunLabButton.pack();

        canvas = new Canvas(this, SWT.FILL);
        canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        canvas.setBackground(backgroundColor);

    }

    public void show(Drawer drawer) {
//        sliderX.assignSelectionListener(textCurrentX, sliderY, drawManager);
        Window window = this;
        rerunLabButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                drawer.clear();
                drawer.runLab2(new Lab2Model(window));
            }

            public void widgetDefaultSelected(SelectionEvent event) {} //Is this how this should be done? IDK
        });
        open();
    }

    public Composite getCanvas() {
        return canvas;
    }

    @Override
    protected void checkSubclass() {
        //  allow subclass
//        System.out.println("info   : checking menu subclass");
    }
}
