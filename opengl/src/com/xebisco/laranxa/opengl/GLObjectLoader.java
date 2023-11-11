package com.xebisco.laranxa.opengl;

import com.xebisco.laranxa.XrTexture;
import com.xebisco.laranxa.XrTextureFilter;
import com.xebisco.laranxa.tid.object.XrIObjectLoader;
import com.xebisco.laranxa.tid.XrVertex;
import com.xebisco.laranxa.tid.animation.*;
import com.xebisco.laranxa.tid.object.XrMaterial;
import com.xebisco.laranxa.tid.object.XrMesh;
import com.xebisco.laranxa.tid.object.XrModel;
import com.xebisco.laranxa.tid.object.XrObject;
import com.xebisco.laranxa.utils.XrColor;
import com.xebisco.laranxa.utils.XrPoint2;
import com.xebisco.laranxa.utils.XrPoint3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.assimp.Assimp.*;

public class GLObjectLoader implements XrIObjectLoader {

    public static final int MAX_BONES = 150, MAX_WEIGHTS = 4;
    private static final float[][] IDENTITY_MATRIX = GLUtils.toMatrix4(new Matrix4f());


    @Override
    public XrObject loadObject(File file, XrMaterial material, boolean animation) {
        AIScene scene;
        scene = aiImportFile(file.getAbsolutePath(), aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_GenSmoothNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights | (animation ? 0 : aiProcess_PreTransformVertices));
        if (scene == null) throw new RuntimeException("Could not load Assimp Scene. " + aiGetErrorString());

        XrMaterial[] materials;

        if (material != null) {
            materials = new XrMaterial[scene.mNumMaterials()];
            for (int i = 0; i < scene.mNumMaterials(); i++) {
                materials[i] = material;
            }
        } else if (scene.mMaterials() != null) {
            materials = new XrMaterial[scene.mNumMaterials()];
            for (int i = 0; i < scene.mNumMaterials(); i++) {
                AIMaterial aiMaterial = AIMaterial.create(Objects.requireNonNull(scene.mMaterials()).get(i));
                XrMaterial mat = processMaterial(aiMaterial);
                materials[i] = mat;
            }
        } else materials = new XrMaterial[0];

        int numMeshes = scene.mNumMeshes();
        PointerBuffer aiMeshes = scene.mMeshes();
        XrModel[] models = new XrModel[numMeshes];

        List<XrBone> boneList = new ArrayList<>();

        for (int i = 0; i < numMeshes; i++) {
            assert aiMeshes != null;
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            XrMesh mesh = processMesh(aiMesh, boneList);
            XrMaterial mat = null;
            if (aiMesh.mMaterialIndex() >= 0) {
                mat = materials[aiMesh.mMaterialIndex()];
            }
            XrModel model = new XrModel(mesh, mat);
            models[i] = model;
        }

        Map<String, XrAnimation> animations = new HashMap<>();
        if (scene.mNumAnimations() > 0) {
            GLAINode rootNode = buildNodesTree(Objects.requireNonNull(scene.mRootNode()), null);
            Matrix4f globalInverseTransformation = GLUtils.toMatrix4f(Objects.requireNonNull(scene.mRootNode()).mTransformation()).invert();
            animations = processAnimations(scene, boneList, rootNode, globalInverseTransformation);
        }

        scene.close();
        return new XrObject(models, new XrObjectAnimationData(), animations);
    }

    private static Map<String, XrAnimation> processAnimations(AIScene aiScene, List<XrBone> boneList, GLAINode rootNode, Matrix4f globalInverseTransformation) {
        Map<String, XrAnimation> animations = new HashMap<>();
        // Process all animations
        int numAnimations = aiScene.mNumAnimations();
        PointerBuffer aiAnimations = aiScene.mAnimations();
        for (int i = 0; i < numAnimations; i++) {
            assert aiAnimations != null;
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));
            int maxFrames = calcAnimationMaxFrames(aiAnimation);
            List<XrAnimatedFrame> frames = new ArrayList<>();
            for (int j = 0; j < maxFrames; j++) {
                float[][][] boneMatrices = new float[MAX_BONES][4][4];
                Arrays.fill(boneMatrices, IDENTITY_MATRIX);
                XrAnimatedFrame animatedFrame = new XrAnimatedFrame(boneMatrices);
                buildFrameMatrices(aiAnimation, boneList, animatedFrame, j, rootNode, rootNode.nodeTransformation(), globalInverseTransformation);
                frames.add(animatedFrame);
            }
            XrAnimation animation = new XrAnimation(aiAnimation.mDuration(), frames.toArray(new XrAnimatedFrame[0]));
            animations.put(aiAnimation.mName().dataString(), animation);
        }
        return animations;
    }

    private static AINodeAnim findAIAnimNode(AIAnimation aiAnimation, String nodeName) {
        AINodeAnim result = null;
        int numAnimNodes = aiAnimation.mNumChannels();
        PointerBuffer aiChannels = aiAnimation.mChannels();
        for (int i = 0; i < numAnimNodes; i++) {
            assert aiChannels != null;
            AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
            if (nodeName.equals(aiNodeAnim.mNodeName().dataString())) {
                result = aiNodeAnim;
                break;
            }
            aiNodeAnim.close();
        }
        return result;
    }

    private static void buildFrameMatrices(AIAnimation aiAnimation, List<XrBone> boneList, XrAnimatedFrame animatedFrame,
                                           int frame, GLAINode node, Matrix4f parentTransformation, Matrix4f globalInverseTransform) {
        String nodeName = node.name();
        AINodeAnim aiNodeAnim = findAIAnimNode(aiAnimation, nodeName);
        Matrix4f nodeTransform = node.nodeTransformation();
        if (aiNodeAnim != null) {
            nodeTransform = buildNodeTransformationMatrix(aiNodeAnim, frame);
        }
        Matrix4f nodeGlobalTransform = new Matrix4f(parentTransformation).mul(nodeTransform);
        List<XrBone> affectedBones = boneList.stream().filter(b -> b.boneName().equals(nodeName)).toList();
        for (XrBone bone : affectedBones) {
            Matrix4f boneTransform = new Matrix4f(globalInverseTransform).mul(nodeGlobalTransform).
                    mul(GLUtils.toMatrix4f(bone.offsetMatrix4()));
            animatedFrame.boneMatrices4()[bone.id()] = GLUtils.toMatrix4(boneTransform);
        }
        for (GLAINode childNode : node.children()) {
            buildFrameMatrices(aiAnimation, boneList, animatedFrame, frame, childNode, nodeGlobalTransform,
                    globalInverseTransform);
        }
    }

    private static Matrix4f buildNodeTransformationMatrix(AINodeAnim aiNodeAnim, int frame) {
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();
        AIVectorKey aiVecKey;
        AIVector3D vec;
        Matrix4f nodeTransform = new Matrix4f();
        int numPositions = aiNodeAnim.mNumPositionKeys();
        if (numPositions > 0) {
            assert positionKeys != null;
            aiVecKey = positionKeys.get(Math.min(numPositions - 1, frame));
            vec = aiVecKey.mValue();
            nodeTransform.translate(vec.x(), vec.y(), vec.z());
        }
        int numRotations = aiNodeAnim.mNumRotationKeys();
        if (numRotations > 0) {
            assert rotationKeys != null;
            AIQuatKey quatKey = rotationKeys.get(Math.min(numRotations - 1, frame));
            AIQuaternion aiQuat = quatKey.mValue();
            Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
            nodeTransform.rotate(quat);
        }
        int numScalingKeys = aiNodeAnim.mNumScalingKeys();
        if (numScalingKeys > 0) {
            assert scalingKeys != null;
            aiVecKey = scalingKeys.get(Math.min(numScalingKeys - 1, frame));
            vec = aiVecKey.mValue();
            nodeTransform.scale(vec.x(), vec.y(), vec.z());
        }
        return nodeTransform;
    }

    private static int calcAnimationMaxFrames(AIAnimation aiAnimation) {
        int maxFrames = 0;
        int numNodeAnims = aiAnimation.mNumChannels();
        PointerBuffer aiChannels = aiAnimation.mChannels();
        for (int i = 0; i < numNodeAnims; i++) {
            assert aiChannels != null;
            AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
            int numFrames = Math.max(Math.max(aiNodeAnim.mNumPositionKeys(), aiNodeAnim.mNumScalingKeys()),
                    aiNodeAnim.mNumRotationKeys());
            maxFrames = Math.max(maxFrames, numFrames);
            aiNodeAnim.close();
        }
        return maxFrames;
    }

    private static XrMeshAnimationData processBones(AIMesh aiMesh, List<XrBone> boneList) {
        List<Optional<XrBone>> bones = new ArrayList<>();
        List<Optional<XrVertexWeight>> weights = new ArrayList<>();
        Map<Integer, List<XrVertexWeight>> weightSet = new HashMap<>();
        int numBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();
        for (int i = 0; i < numBones; i++) {
            assert aiBones != null;
            AIBone aiBone = AIBone.create(aiBones.get(i));
            int id = boneList.size();
            XrBone bone = new XrBone(id, aiBone.mName().dataString(), GLUtils.toMatrix4(aiBone.mOffsetMatrix()));
            boneList.add(bone);
            int numWeights = aiBone.mNumWeights();
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
            aiBone.close();
            for (int j = 0; j < numWeights; j++) {
                AIVertexWeight aiWeight = aiWeights.get(j);
                XrVertexWeight vw = new XrVertexWeight(bone, aiWeight.mVertexId(), aiWeight.mWeight());
                List<XrVertexWeight> vertexWeightList = weightSet.computeIfAbsent(vw.vertexWeightID(), k -> new ArrayList<>());
                vertexWeightList.add(vw);
            }
        }
        int numVertices = aiMesh.mNumVertices();
        for (int i = 0; i < numVertices; i++) {
            List<XrVertexWeight> vertexWeightList = weightSet.get(i);
            int size = vertexWeightList != null ? vertexWeightList.size() : 0;
            for (int j = 0; j < MAX_WEIGHTS; j++) {
                if (j < size) {
                    XrVertexWeight vw = vertexWeightList.get(j);
                    weights.add(Optional.of(vw));
                    bones.add(Optional.of(vw.bone()));
                } else {
                    weights.add(Optional.empty());
                    bones.add(Optional.empty());
                }
            }
        }
        //noinspection unchecked
        return new XrMeshAnimationData(weights.toArray(new Optional[0]), bones.toArray(new Optional[0]));
    }

    private static GLAINode buildNodesTree(AINode aiNode, GLAINode parentNode) {
        String nodeName = aiNode.mName().dataString();
        GLAINode node = new GLAINode(nodeName, parentNode, GLUtils.toMatrix4f(aiNode.mTransformation()));
        int numChildren = aiNode.mNumChildren();
        PointerBuffer aiChildren = aiNode.mChildren();
        for (int i = 0; i < numChildren; i++) {
            assert aiChildren != null;
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            GLAINode childNode = buildNodesTree(aiChildNode, node);
            node.children().add(childNode);
        }
        return node;
    }

    private static XrMesh processMesh(AIMesh aiMesh, List<XrBone> boneList) {

        List<XrVertex> vertexList = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        List<XrPoint3> vertices = new ArrayList<>();
        List<XrPoint2> texCoords = new ArrayList<>();
        List<XrPoint3> normals = new ArrayList<>();
        List<XrPoint3> tangents = new ArrayList<>();
        List<XrPoint3> bitangents = new ArrayList<>();

        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(new XrPoint3(aiVertex.x(), aiVertex.y(), aiVertex.z()));
        }

        AIVector3D.Buffer aiTexCoords = aiMesh.mTextureCoords(0);
        if (aiTexCoords != null) {
            while (aiTexCoords.remaining() > 0) {
                AIVector3D aiTexCoord = aiTexCoords.get();
                texCoords.add(new XrPoint2(aiTexCoord.x(), -aiTexCoord.y()));
            }
        }

        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        if (aiNormals != null) {
            while (aiNormals.remaining() > 0) {
                AIVector3D aiNormal = aiNormals.get();
                normals.add(new XrPoint3(aiNormal.x(), aiNormal.y(), aiNormal.z()));
            }
        }

        AIVector3D.Buffer aiTangents = aiMesh.mNormals();
        assert aiTangents != null;
        while (aiTangents.remaining() > 0) {
            AIVector3D aiTangent = aiTangents.get();
            tangents.add(new XrPoint3(aiTangent.x(), aiTangent.y(), aiTangent.z()));
        }

        AIVector3D.Buffer aiBitangents = aiMesh.mNormals();
        assert aiBitangents != null;
        while (aiBitangents.remaining() > 0) {
            if (!(aiBitangents.remaining() > 0)) break;
            AIVector3D aiBitangent = aiBitangents.get();
            bitangents.add(new XrPoint3(aiBitangent.x(), aiBitangent.y(), aiBitangent.z()));
        }


        AIFace.Buffer aifaces = aiMesh.mFaces();
        while (aifaces.remaining() > 0) {
            AIFace aiface = aifaces.get();

            if (aiface.mNumIndices() == 3) {
                IntBuffer indicesBuffer = aiface.mIndices();
                indices.add(indicesBuffer.get(0));
                indices.add(indicesBuffer.get(1));
                indices.add(indicesBuffer.get(2));
            }
            if (aiface.mNumIndices() == 4) {
                IntBuffer indicesBuffer = aiface.mIndices();
                indices.add(indicesBuffer.get(0));
                indices.add(indicesBuffer.get(1));
                indices.add(indicesBuffer.get(2));
                indices.add(indicesBuffer.get(0));
                indices.add(indicesBuffer.get(1));
                indices.add(indicesBuffer.get(3));
                indices.add(indicesBuffer.get(1));
                indices.add(indicesBuffer.get(2));
                indices.add(indicesBuffer.get(3));
            }

        }

        for (int i = 0; i < vertices.size(); i++) {
            XrPoint3 position = vertices.get(i), normal;
            XrPoint2 textureCoord;
            if (!normals.isEmpty()) {
                normal = normals.get(i);
            } else {
                normal = new XrPoint3();
            }
            if (!texCoords.isEmpty()) {
                textureCoord = texCoords.get(i);
            } else {
                textureCoord = new XrPoint2();
            }
            XrPoint3 tangent = null;
            XrPoint3 bitangent = null;
            tangent = tangents.get(i);
            bitangent = bitangents.get(i);
            vertexList.add(new XrVertex(position, normal, tangent, bitangent, textureCoord));
        }

        XrVertex[] vertexData = GLUtils.toVertexArray(vertexList);
        int[] facesData = GLUtils.toIntArray(indices);

        return new XrMesh(vertexData, facesData, processBones(aiMesh, boneList));
    }


    private static XrMaterial processMaterial(AIMaterial aiMaterial) {
        AIColor4D colour = AIColor4D.create();

        XrColor ambient = new XrColor(XrMaterial.DEFAULT_COLOR);
        int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, colour);
        if (result == 0) {
            ambient = new XrColor(colour.r(), colour.g(), colour.b(), colour.a());
        }

        XrColor diffuse = new XrColor(XrMaterial.DEFAULT_COLOR);
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, colour);
        if (result == 0) {
            diffuse = new XrColor(colour.r(), colour.g(), colour.b(), colour.a());
        }

        XrColor specular = new XrColor(XrMaterial.DEFAULT_COLOR);
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, colour);
        if (result == 0) {
            specular = new XrColor(colour.r(), colour.g(), colour.b(), colour.a());
        }

        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_NORMALS, 0, path, (IntBuffer) null, null, null, null, null, null);
        String textPath = path.dataString();
        GLTexture normalMap = null;
        if (!textPath.isEmpty()) {
            normalMap = GLTextureCache.get(new XrTexture(textPath, XrTextureFilter.NEAREST));
            diffuse = new XrColor(0);
        }
        path.close();

        path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        textPath = path.dataString();
        GLTexture diffuseMap = null;
        if (!textPath.isEmpty()) {
            diffuseMap = GLTextureCache.get(new XrTexture(textPath, XrTextureFilter.NEAREST));
            diffuse = new XrColor(0);
        }
        path.close();

        float[] reflectance = new float[]{0};
        result = aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_SHININESS_STRENGTH, aiTextureType_NONE, 0, reflectance, new int[]{1});
        if (result != aiReturn_SUCCESS) {
            reflectance[0] = 1;
        }

        boolean hasTransparency;

        if (diffuseMap != null && diffuseMap.hasTransparency()) hasTransparency = true;
        else hasTransparency = diffuse.alpha() < 1.0;


        return new XrMaterial(diffuseMap != null ? diffuseMap.tex() : null, normalMap != null ? normalMap.tex() : null, diffuse, ambient, specular, reflectance[0], hasTransparency);
    }
}
