package com.xebisco.laranxa.opengl;

import com.xebisco.laranxa.XrIRenderer;
import com.xebisco.laranxa.bid.XrCamera2D;
import com.xebisco.laranxa.bid.XrTransformation2D;
import com.xebisco.laranxa.bid.object.XrObject2D;
import com.xebisco.laranxa.tid.XrCamera;
import com.xebisco.laranxa.tid.XrProjectionConfig;
import com.xebisco.laranxa.tid.XrTransformation;
import com.xebisco.laranxa.tid.animation.XrObjectAnimationData;
import com.xebisco.laranxa.tid.lightning.*;
import com.xebisco.laranxa.tid.object.XrModel;
import com.xebisco.laranxa.tid.object.XrObject;
import com.xebisco.laranxa.utils.XrColor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;

import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class GLRenderer implements XrIRenderer {

    public static final Matrix4f[] DEFAULT_BONES_MATRICES = new Matrix4f[GLObjectLoader.MAX_BONES];
    static {
        Matrix4f zeroMatrix = new Matrix4f().zero();
        Arrays.fill(DEFAULT_BONES_MATRICES, zeroMatrix);
    }

    private GLShaderProgram shaderProgram3d, shaderProgram2d;

    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f transformationMatrix = new Matrix4f();


    @Override
    public void init() {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        shaderProgram3d = new GLShaderProgram(GLUtils.class.getResourceAsStream("/com/xebisco/laranxa/opengl/default3d.vert"), GLUtils.class.getResourceAsStream("/com/xebisco/laranxa/opengl/default3d.frag"));
        shaderProgram2d = new GLShaderProgram(GLUtils.class.getResourceAsStream("/com/xebisco/laranxa/opengl/default2d.vert"), GLUtils.class.getResourceAsStream("/com/xebisco/laranxa/opengl/default2d.frag"));
    }

    private static void disableCulling() {
        glDisable(GL_CULL_FACE);
    }

    private static void enableCulling() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    @Override
    public void clearFramebuffer() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void draw3D(XrObject object, XrTransformation transformation, XrCamera camera, XrSceneLightning sceneLightning) {
        for (int i = 0; i < object.models().length; i++)
            draw(object.models()[i], object.objectAnimationData(), transformation, camera, sceneLightning);
    }

    @Override
    public void draw2D(XrObject2D object, XrTransformation2D transformation, XrCamera2D camera) {
        GLMesh2D mesh = GLMesh2DCache.get(object.mesh2D());
        Matrix4f viewMatrix = orthoViewMatrix(camera);

        shaderProgram2d.bind();

        shaderProgram2d.setUniform("texture_sampler", 0);
        shaderProgram2d.setUniform("viewMatrix", viewMatrix);
        updateTransformationMatrix(transformation);
        shaderProgram2d.setUniform("transformationMatrix", transformationMatrix);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, GLTextureCache.get(object.texture()).handler());

        glBindVertexArray(mesh.vao);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, mesh.vertexCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);

        shaderProgram2d.unbind();
    }

    public void draw(XrModel model, XrObjectAnimationData objectAnimationData, XrTransformation transformation, XrCamera camera, XrSceneLightning sceneLightning) {

        disableCulling();

        GLMesh mesh = GLMeshCache.get(model.mesh());

        Matrix4f viewMatrix = projectionViewMatrix(camera.transform());

        XrColor ambientLight = sceneLightning.ambientLight();
        XrDirectionalLight directionalLight = sceneLightning.directionalLight();

        shaderProgram3d.bind();

        if (objectAnimationData == null || objectAnimationData.currentAnimation() == null) {
            shaderProgram3d.setUniform("bonesMatrices", DEFAULT_BONES_MATRICES);
        } else {
            shaderProgram3d.setUniform("bonesMatrices", objectAnimationData.currentFrame().boneMatrices4());
        }

        List<XrPointLight> pointLights = sceneLightning.pointLights();
        int numPointLights = pointLights.size();
        XrPointLight pointLight;
        for (int i = 0; i < GLShaderProgram.MAX_POINT_LIGHTS; i++) {
            if (i < numPointLights) {
                pointLight = pointLights.get(i);
            } else {
                pointLight = null;
            }
            String name = "point_lights[" + i + "]";
            updatePointLight(pointLight, name, viewMatrix, shaderProgram3d);
        }

        List<XrSpotLight> spotLights = sceneLightning.spotLights();
        int numSpotLights = spotLights.size();
        XrSpotLight spotLight;
        for (int i = 0; i < GLShaderProgram.MAX_SPOT_LIGHTS; i++) {
            if (i < numSpotLights) {
                spotLight = spotLights.get(i);
            } else {
                spotLight = null;
            }
            String name = "spot_lights[" + i + "]";
            updateSpotLight(spotLight, name, viewMatrix, shaderProgram3d);
        }

        shaderProgram3d.setUniform("viewMatrix", viewMatrix);
        shaderProgram3d.setUniform("texture_sampler", 0);
        shaderProgram3d.setUniform("projectionMatrix", projectionMatrix);
        updateTransformationMatrix(transformation);
        shaderProgram3d.setUniform("transformationMatrix", transformationMatrix);
        shaderProgram3d.setUniform("material.ambient", GLUtils.colorToVector4f(model.material().ambientColor()));
        shaderProgram3d.setUniform("material.diffuse", GLUtils.colorToVector4f(model.material().diffuseColor()));
        shaderProgram3d.setUniform("material.specular", GLUtils.colorToVector4f(model.material().specularColor()));
        shaderProgram3d.setUniform("material.hasTexture", model.material().diffuseMap() != null ? 1 : 0);
        shaderProgram3d.setUniform("material.reflectance", model.material().reflectance());
        shaderProgram3d.setUniform("ambient_light", GLUtils.colorToVector3f(ambientLight));

        Vector4f auxDir = new Vector4f(directionalLight.direction().x(), directionalLight.direction().y(), directionalLight.direction().z(), 0);
        auxDir.mul(viewMatrix);
        Vector3f dir = new Vector3f(auxDir.x, auxDir.y, auxDir.z);
        shaderProgram3d.setUniform("directional_light.color", GLUtils.colorToVector3f(directionalLight.color()));
        shaderProgram3d.setUniform("directional_light.direction", dir);
        shaderProgram3d.setUniform("directional_light.intensity", directionalLight.intensity());

        if (model.material().hasTransparency()) disableCulling();
        else enableCulling();

        if (model.material().diffuseMap() != null) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, GLTextureCache.get(model.material().diffuseMap()).handler());
        }

        if (model.material().normalMap() != null) {
            shaderProgram3d.setUniform("normal_sampler", 1);
            shaderProgram3d.setUniform("material.hasNormalMap", 1);
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, GLTextureCache.get(model.material().normalMap()).handler());
        }

        glBindVertexArray(mesh.vao);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, mesh.vertexCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);

        shaderProgram3d.unbind();
    }

    private static void updatePointLight(XrPointLight pointLight, String prefix, Matrix4f viewMatrix, GLShaderProgram shaderProgram) {
        Vector4f aux = new Vector4f();
        Vector3f lightPosition = new Vector3f();
        Vector3f color = new Vector3f();
        float intensity = 0.0f;
        float constant = 0.0f;
        float linear = 0.0f;
        float exponent = 0.0f;
        if (pointLight != null) {
            aux.set(pointLight.position().x(), pointLight.position().y(), pointLight.position().z(), 1);
            aux.mul(viewMatrix);
            lightPosition.set(aux.x, aux.y, aux.z);
            color.set(GLUtils.colorToVector3f(pointLight.color()));
            intensity = pointLight.intensity();
            XrAttenuation attenuation = pointLight.attenuation();
            constant = attenuation.constant();
            linear = attenuation.linear();
            exponent = attenuation.exponent();
        }
        shaderProgram.setUniform(prefix + ".position", lightPosition);
        shaderProgram.setUniform(prefix + ".color", color);
        shaderProgram.setUniform(prefix + ".intensity", intensity);
        shaderProgram.setUniform(prefix + ".att.constant", constant);
        shaderProgram.setUniform(prefix + ".att.linear", linear);
        shaderProgram.setUniform(prefix + ".att.exponent", exponent);
    }

    private void updateSpotLight(XrSpotLight spotLight, String prefix, Matrix4f viewMatrix, GLShaderProgram shaderProgram) {
        XrPointLight pointLight = null;
        Vector3f coneDirection = new Vector3f();
        float cutoff = 0.0f;
        if (spotLight != null) {
            coneDirection = new Vector3f(spotLight.coneDirection().x(), spotLight.coneDirection().y(), spotLight.coneDirection().z());
            cutoff = spotLight.cutOff();
            pointLight = spotLight.pointLight();
        }
        shaderProgram.setUniform(prefix + ".conedir", coneDirection);
        shaderProgram.setUniform(prefix + ".conedir", cutoff);
        updatePointLight(pointLight, prefix + ".pl", viewMatrix, shaderProgram);
    }

    public void updateTransformationMatrix(XrTransformation transformation) {
        transformationMatrix.identity().translate(new Vector3f(transformation.position().x(), transformation.position().y(), transformation.position().z())).rotateX((float) Math.toRadians(transformation.rotation().x())).rotateY((float) Math.toRadians(transformation.rotation().y())).rotateZ((float) Math.toRadians(transformation.rotation().z())).scale(transformation.scale().x(), transformation.scale().y(), transformation.scale().z());
    }

    public void updateTransformationMatrix(XrTransformation2D transformation) {
        transformationMatrix.identity().translate(new Vector3f(transformation.position().x(), transformation.position().y(), 0)).rotateZ((float) Math.toRadians(transformation.rotation())).scaleXY(transformation.scale().x(), transformation.scale().y());
    }

    public static Matrix4f projectionViewMatrix(XrTransformation transformation) {
        return new Matrix4f().identity().rotateX((float) Math.toRadians(transformation.rotation().x())).rotateY((float) Math.toRadians(transformation.rotation().y())).rotateZ((float) Math.toRadians(transformation.rotation().z())).scale(transformation.scale().x(), transformation.scale().y(), transformation.scale().z()).translate(new Vector3f(-transformation.position().x(), -transformation.position().y(), -transformation.position().z()));
    }

    public static Matrix4f orthoViewMatrix(XrCamera2D camera2D) {
        float w = camera2D.size().width() * camera2D.transform().scale().x;
        float h = camera2D.size().height() * camera2D.transform().scale().y;
        float x = camera2D.transform().position().x - w / 2;
        float y = camera2D.transform().position().y - h / 2;
        return new Matrix4f().identity().ortho2D(x, w + x, y, y + h);
    }


    @Override
    public void updateProjection(XrProjectionConfig projectionConfig, float aspectRatio) {
        projectionMatrix.identity().perspective(projectionConfig.fov, aspectRatio, projectionConfig.zNear, projectionConfig.zFar);
    }

    public GLShaderProgram shaderProgram() {
        return shaderProgram3d;
    }

    public void setShaderProgram3d(GLShaderProgram shaderProgram3d) {
        this.shaderProgram3d = shaderProgram3d;
    }
}
