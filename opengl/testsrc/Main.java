import com.xebisco.laranxa.*;
import com.xebisco.laranxa.opengl.GLObjectLoader;
import com.xebisco.laranxa.opengl.GLRenderer;
import com.xebisco.laranxa.opengl.GLWindow;
import com.xebisco.laranxa.tid.XrCamera;
import com.xebisco.laranxa.tid.object.XrIObjectLoader;
import com.xebisco.laranxa.tid.XrProjectionConfig;
import com.xebisco.laranxa.tid.XrTransformation;
import com.xebisco.laranxa.tid.lightning.XrAttenuation;
import com.xebisco.laranxa.tid.lightning.XrPointLight;
import com.xebisco.laranxa.tid.lightning.XrSceneLightning;
import com.xebisco.laranxa.tid.object.XrMaterial;
import com.xebisco.laranxa.tid.object.XrObject;
import com.xebisco.laranxa.utils.XrColor;
import com.xebisco.laranxa.utils.XrPoint3;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.ArrayList;

public class Main {

    private static float addX, addX2, addY, addY2;

    public static void main(String[] args) throws Exception {
        XrIWindow window = new GLWindow();

        XrWindowConfig c = new XrWindowConfig();

        XrIObjectLoader o = new GLObjectLoader();

        XrTransformation t = new XrTransformation();

        c.keyListeners = new XrIKeyListener[]{
                new XrIKeyListener() {
                    @Override
                    public void keyPressed(XrKey key) {
                        if (key == XrKey.KEY_1)
                            addX = -1;
                        else if (key == XrKey.KEY_2) addX = 1;

                        if (key == XrKey.KEY_3)
                            addY2 = -1;
                        else if (key == XrKey.KEY_4) addY2 = 1;

                        if (key == XrKey.KEY_A)
                            addX2 = -1;
                        else if (key == XrKey.KEY_D) addX2 = 1;

                        if (key == XrKey.KEY_S)
                            addY = -1;
                        else if (key == XrKey.KEY_W) addY = 1;
                    }

                    @Override
                    public void keyReleased(XrKey key) {
                        if (key == XrKey.KEY_1)
                            addX = 0;
                        else if (key == XrKey.KEY_2) addX = 0;

                        if (key == XrKey.KEY_3)
                            addY2 = 0;
                        else if (key == XrKey.KEY_4) addY2 = 0;

                        if (key == XrKey.KEY_A)
                            addX2 = 0;
                        else if (key == XrKey.KEY_D) addX2 = 0;

                        if (key == XrKey.KEY_S)
                            addY = 0;
                        else if (key == XrKey.KEY_W) addY = 0;
                    }
                }
        };

        XrIRenderer renderer = new GLRenderer();

        window.setup(c);

        window.init();

        renderer.init();

        XrMaterial material = new XrMaterial(null, null, new XrColor(0f, 0, 0, 1), new XrColor(1, 1, 1, 1), new XrColor(1, 1, 1, 1), 1f, false);
        //XrMaterial material1 = new XrMaterial(null, new XrColor(.3f, 0, 1, 1), new XrColor(1, 1, 1, 1), new XrColor(1, 1, 1, 1), 0f);

        XrObject object = o.loadObject(new File("opengl/KlonoaForExport.obj"), false);
        //XrModel o = o.loadModel(Main.class.getResourceAsStream("/com/xebisco/laranxa/opengl/cube.obj"), material1, "obj")[0];

        GL11.glClearColor(0, 0, .4f, 1);

        t.scale().x = .2f;
        t.scale().y = .2f;
        t.scale().z = .2f;

        renderer.updateProjection(new XrProjectionConfig(), 1280f / 720f);
        t.position().y = -.1f;
        t.rotation().y = 180;

        XrCamera camera = new XrCamera(new XrTransformation());

        XrSceneLightning sceneLightning = new XrSceneLightning(new ArrayList<>(), new ArrayList<>());

        sceneLightning.directionalLight().setIntensity(0);

        XrPointLight pointLight = new XrPointLight(new XrAttenuation(0, 0, 1), new XrColor(1, 1, 1), 2, new XrPoint3());
        sceneLightning.pointLights().add(pointLight);

        XrTransformation t2 = new XrTransformation();

        while (!window.shouldClose()) {
            window.process();
            //t.rotation().y += .01f;
            camera.transform().position().z += addX * .0002f;
            camera.transform().position().y += addY2 * .0002f;
            ((XrPoint3) pointLight.position()).z += addY * -.0001f;
            ((XrPoint3) pointLight.position()).x += addX2 * .0001f;
            t2.position().x = pointLight.position().x();
            t2.position().y = pointLight.position().y() - .1f;
            t2.position().z = pointLight.position().z();
            renderer.clearFramebuffer();
            renderer.draw3D(object, t, camera, sceneLightning);
            //renderer.draw3D(model1, t2, camera, sceneLightning);
        }

        window.close();
    }
}