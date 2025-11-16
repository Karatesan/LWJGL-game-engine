package pl.karatesan.engine.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Animation {

    private boolean isAnimating;
    private boolean isTranslating;
    private boolean isRotating;
    private float currentAngle = 0.0f;
    private float targetAngle;
    private Vector3f rotationAxis;
    private Vector3f translation;
    private float currentDistance;
    private float totalTranslationDistance;
    private final Matrix4f translationMatrix = new Matrix4f();
    private final Matrix4f rotationMatrix = new Matrix4f();
    private final Matrix4f transformation = new Matrix4f();
    private float rotationSpeedDegPerSec = 90;
    private final float translationSpeed = 0.5f;

    public boolean isAnimating() {
        return isAnimating;
    }

    public void startAnimation() {
        if (targetAngle == 0.0f && rotationAxis == null) {
            System.out.println("Error cant animate");
        } else if (!isAnimating) {
            this.isAnimating = true;
            this.isTranslating = true;
            this.isRotating = true;
            reset();
            totalTranslationDistance = translation.length();
        }
    }

    private void reset() {
        //reset
        currentAngle = 0;
        currentDistance = 0;
        transformation.identity();
    }

    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    public void animate(double deltaTime) {
        if (isAnimating && isTranslating) {
            currentDistance += (float) (translationSpeed * deltaTime);
            float progress = currentDistance / totalTranslationDistance;
            translationMatrix.identity().translate(progress * translation.x, progress * translation.y, progress * translation.z);
            if (progress >= 1) isTranslating = false;
        } else if (isAnimating && isRotating) {
            currentAngle += (float) (rotationSpeedDegPerSec * deltaTime);
            if (currentAngle > targetAngle) currentAngle = targetAngle;
            rotationMatrix.identity().rotate((float) Math.toRadians(currentAngle), rotationAxis);
            if (currentAngle >= targetAngle) isRotating = false;
        }
        if (!isTranslating && !isRotating) {
            isAnimating = false;
        }
    }

    public void setTargetAngle(float targetAngle) {
        this.targetAngle = targetAngle;
    }

    public void setRotationAxis(Vector3f rotationAxis) {
        this.rotationAxis = rotationAxis;
    }

    public Matrix4f getTransformation() {
        return rotationMatrix.mul(translationMatrix, transformation);
    }

    public void setRotationAxis(float v, float v1, float v2) {
        setRotationAxis(new Vector3f(v, v1, v2));
    }
}
