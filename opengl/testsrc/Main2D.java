import com.xebisco.laranxa.XrIRenderer;
import com.xebisco.laranxa.XrIWindow;
import com.xebisco.laranxa.XrWindowConfig;
import com.xebisco.laranxa.bid.XrCamera2D;
import com.xebisco.laranxa.bid.XrTransformation2D;
import com.xebisco.laranxa.bid.XrVertex2D;
import com.xebisco.laranxa.bid.object.XrMesh2D;
import com.xebisco.laranxa.bid.object.XrObject2D;
import com.xebisco.laranxa.opengl.GLRenderer;
import com.xebisco.laranxa.opengl.GLWindow;
import com.xebisco.laranxa.utils.XrDimension;
import com.xebisco.laranxa.utils.XrPoint2;

public class Main2D {
    public static void main(String[] args) throws Exception {
        XrIWindow window = new GLWindow();
        window.setup(new XrWindowConfig());
        window.init();

        XrIRenderer renderer = new GLRenderer();
        renderer.init();

        XrObject2D o = new XrObject2D(new XrMesh2D(new XrVertex2D[]{
                new XrVertex2D(new XrPoint2(-100, 100), new XrPoint2(0, 0)),
                new XrVertex2D(new XrPoint2(100, 100), new XrPoint2(1, 0)),
                new XrVertex2D(new XrPoint2(100, -100), new XrPoint2(1, 1)),
                new XrVertex2D(new XrPoint2(-100, -100), new XrPoint2(0, 1))
        }, new int[] {
                0, 1, 2,
                2, 3, 0
        }));
        XrTransformation2D t = new XrTransformation2D();
        XrCamera2D cam = new XrCamera2D(new XrTransformation2D(), new XrDimension(1280, 720));

        while (!window.shouldClose()) {
            window.process();
            renderer.clearFramebuffer();
            renderer.draw2D(o, t, cam);
        }

        window.close();
    }
}
