package pl.karatesan.dump;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class testy {

    static void main(String[] args) {
        Vector4f vec = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
        Matrix4f trans = new Matrix4f().translate(5.0f, 1.0f, 0.0f);
        vec.mul(trans);
        System.out.println(vec);

        Matrix4f toRotate = new Matrix4f();
        toRotate.rotate(90, new Vector3f(0.0f, 0.0f, 1.0f)).scale(0.5f, 0.5f, 0.5f);
        System.out.println(toRotate);
    }
}
