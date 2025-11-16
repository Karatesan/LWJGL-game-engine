package pl.karatesan.engine.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera3D {

    private Vector3f direction;
    private Vector3f up;
    private Vector3f cameraPos;
    private Vector3f cameraTarget;

    private float yaw;
    private float pitch;
    private final double pitchMax = Math.toRadians(89.0f);
    private Matrix4f view = new Matrix4f();

    public Camera3D(Vector3f cameraPosition, Vector3f worldUp, Vector3f direction) {
        this.cameraPos = cameraPosition;
        this.up = worldUp;
        this.direction = direction;
        this.cameraTarget = new Vector3f();
        yaw = (float) Math.atan2(direction.z, direction.x);
        pitch = (float) Math.asin(direction.y);
    }

    public void moveCamera(Vector3f newPosition) {
        cameraPos.set(newPosition);
    }

    public Matrix4f getViewMatrix() {
        cameraPos.add(direction, cameraTarget);
        view.identity().lookAt(cameraPos, cameraTarget, up);
        return view;
    }

    public void handleLook(double x, double y) {
        yaw += (float) x;
        pitch += (float) y;

        if (pitch > pitchMax) pitch = (float) pitchMax;
        if (pitch < -pitchMax) pitch = (float) -pitchMax;

        direction.set(
                (float) Math.cos(yaw) * (float) Math.cos(pitch),
                (float) Math.sin(pitch),
                (float) Math.sin(yaw) * (float) Math.cos(pitch)
        );
    }

    public Vector3f getDirection() {
        return direction;
    }

    public Vector3f getUp() {
        return up;
    }


}
