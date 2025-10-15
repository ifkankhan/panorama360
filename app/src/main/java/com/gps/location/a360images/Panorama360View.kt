package com.gps.location.a360images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class Panorama360View @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private val renderer: Panorama360Renderer

    init {
        setEGLContextClientVersion(2)
        renderer = Panorama360Renderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY

                // Reduced sensitivity from 0.5f to 0.2f for slower, smoother movement
                renderer.angleX += dx * 0.2f
                renderer.angleY += dy * 0.2f

                // Limit Y rotation to prevent flipping
                renderer.angleY = renderer.angleY.coerceIn(-90f, 90f)

                requestRender()
            }
        }

        previousX = x
        previousY = y
        return true
    }

    fun loadImageFromResources(resourceId: Int) {
        renderer.loadImageFromResources(resourceId)
        requestRender()
    }

    fun loadImageFromUrl(url: String) {
        renderer.loadImageFromUrl(url)
        requestRender()
    }

    fun loadImageFromBitmap(bitmap: Bitmap) {
        renderer.loadImageFromBitmap(bitmap)
        requestRender()
    }

    private class Panorama360Renderer(private val context: Context) : GLSurfaceView.Renderer {

        var angleX = 0f
        var angleY = 0f

        private val projectionMatrix = FloatArray(16)
        private val viewMatrix = FloatArray(16)
        private val modelMatrix = FloatArray(16)
        private val mvpMatrix = FloatArray(16)

        private var program = 0
        private var textureId = 0
        private lateinit var sphereVertexBuffer: FloatBuffer
        private lateinit var sphereTexCoordBuffer: FloatBuffer
        private lateinit var sphereIndexBuffer: ShortBuffer
        private var vertexCount = 0
        private var indexCount = 0

        private var positionHandle = 0
        private var texCoordHandle = 0
        private var mvpMatrixHandle = 0
        private var textureHandle = 0

        private var pendingBitmap: Bitmap? = null

        private val vertexShaderCode = """
            uniform mat4 uMVPMatrix;
            attribute vec4 vPosition;
            attribute vec2 aTexCoord;
            varying vec2 vTexCoord;
            void main() {
                gl_Position = uMVPMatrix * vPosition;
                vTexCoord = aTexCoord;
            }
        """.trimIndent()

        private val fragmentShaderCode = """
            precision mediump float;
            varying vec2 vTexCoord;
            uniform sampler2D uTexture;
            void main() {
                gl_FragColor = texture2D(uTexture, vTexCoord);
            }
        """.trimIndent()

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)

            createSphere()
            createShaderProgram()
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)

            val ratio = width.toFloat() / height.toFloat()
            // Increased FOV from 45° to 75° to reduce zoom and show more of the image
            Matrix.perspectiveM(projectionMatrix, 0, 75f, ratio, 0.1f, 100f)
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

            // Load pending bitmap if any
            pendingBitmap?.let {
                loadTexture(it)
                pendingBitmap = null
            }

            if (textureId == 0) return

            // Set camera position
            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 0f, 0f, 0f, -1f, 0f, 1f, 0f)

            // Rotate the sphere based on touch input
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.rotateM(modelMatrix, 0, -angleY, 1f, 0f, 0f)
            Matrix.rotateM(modelMatrix, 0, angleX, 0f, 1f, 0f)

            // Calculate MVP matrix
            val tempMatrix = FloatArray(16)
            Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMatrix, 0)

            // Use shader program
            GLES20.glUseProgram(program)

            // Set vertex positions
            GLES20.glVertexAttribPointer(
                positionHandle, 3, GLES20.GL_FLOAT, false, 0, sphereVertexBuffer
            )
            GLES20.glEnableVertexAttribArray(positionHandle)

            // Set texture coordinates
            GLES20.glVertexAttribPointer(
                texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, sphereTexCoordBuffer
            )
            GLES20.glEnableVertexAttribArray(texCoordHandle)

            // Set MVP matrix
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

            // Bind texture
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glUniform1i(textureHandle, 0)

            // Draw sphere
            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, indexCount,
                GLES20.GL_UNSIGNED_SHORT, sphereIndexBuffer
            )

            // Disable vertex arrays
            GLES20.glDisableVertexAttribArray(positionHandle)
            GLES20.glDisableVertexAttribArray(texCoordHandle)
        }

        private fun createSphere() {
            val radius = 10f
            val stacks = 50
            val slices = 50

            val vertices = mutableListOf<Float>()
            val texCoords = mutableListOf<Float>()
            val indices = mutableListOf<Short>()

            for (i in 0..stacks) {
                val theta = i * Math.PI / stacks
                val sinTheta = sin(theta).toFloat()
                val cosTheta = cos(theta).toFloat()

                for (j in 0..slices) {
                    val phi = j * 2 * Math.PI / slices
                    val sinPhi = sin(phi).toFloat()
                    val cosPhi = cos(phi).toFloat()

                    val x = cosPhi * sinTheta
                    val y = cosTheta
                    val z = sinPhi * sinTheta

                    vertices.add(radius * x)
                    vertices.add(radius * y)
                    vertices.add(radius * z)

                    texCoords.add(j.toFloat() / slices)
                    texCoords.add(i.toFloat() / stacks)
                }
            }

            for (i in 0 until stacks) {
                for (j in 0 until slices) {
                    val first = (i * (slices + 1) + j).toShort()
                    val second = (first + slices + 1).toShort()

                    indices.add(first)
                    indices.add(second)
                    indices.add((first + 1).toShort())

                    indices.add(second)
                    indices.add((second + 1).toShort())
                    indices.add((first + 1).toShort())
                }
            }

            vertexCount = vertices.size / 3
            indexCount = indices.size

            // Create vertex buffer
            sphereVertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            sphereVertexBuffer.put(vertices.toFloatArray())
            sphereVertexBuffer.position(0)

            // Create texture coordinate buffer
            sphereTexCoordBuffer = ByteBuffer.allocateDirect(texCoords.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            sphereTexCoordBuffer.put(texCoords.toFloatArray())
            sphereTexCoordBuffer.position(0)

            // Create index buffer
            sphereIndexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
            sphereIndexBuffer.put(indices.toShortArray())
            sphereIndexBuffer.position(0)
        }

        private fun createShaderProgram() {
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

            program = GLES20.glCreateProgram()
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)

            positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
            texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
            mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
            textureHandle = GLES20.glGetUniformLocation(program, "uTexture")
        }

        private fun loadShader(type: Int, shaderCode: String): Int {
            val shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            return shader
        }

        private fun loadTexture(bitmap: Bitmap) {
            val textureIds = IntArray(1)
            GLES20.glGenTextures(1, textureIds, 0)
            textureId = textureIds[0]

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        }

        fun loadImageFromResources(resourceId: Int) {
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
            pendingBitmap = bitmap
        }

        fun loadImageFromUrl(url: String) {
            Thread {
                try {
                    val connection = java.net.URL(url).openConnection()
                    connection.connect()
                    val inputStream = connection.getInputStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    pendingBitmap = bitmap
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }

        fun loadImageFromBitmap(bitmap: Bitmap) {
            pendingBitmap = bitmap
        }
    }
}

